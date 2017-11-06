(ns cljperf.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.request :as req]
            [taoensso.timbre :as log]
            [ring.util.response :as rsp]
            [reitit.ring :as ring]
            [cheshire.core :as ches]
            [clojure.core.async :refer [go-loop <! >!! sliding-buffer chan]]
            [clojure.walk :as w]
            [timbre-ns-pattern-level]))

(def sliding-chan (chan (sliding-buffer 100)))

(go-loop []
  (log/info (<! sliding-chan))
  (recur))

; Disable super spammy logging from hikari (connection pool)
(log/merge-config!
  {:level :info
   :middleware [(timbre-ns-pattern-level/middleware {"com.zaxxer.hikari.*" :error
                                                     :all :info})]
   :appenders
   {:println
    {:fn
     (fn [data]
       (let [{:keys [output_]} data]
         (println data)))}}})

(defn camel-case [k]
  (-> k
      name
      (clojure.string/replace #"-(.)" #(-> % (nth 1) clojure.string/upper-case))))

(defn camel-case-map [m]
  (cond
    (map? m)  (w/walk (fn [[k v]] [(camel-case k) (camel-case-map v)]) identity m)
    (coll? m) (map camel-case-map m)
    :else     m))

(defn json [payload]
  (-> payload
      (ches/generate-string {:key-fn camel-case})
      rsp/response
      (rsp/content-type "application/json")))

(defn hello [req]
  (json {:msg-val "World!"
         :age 32
         :num-likes ["one" "two" "three"]
         :address {:street "Here we go"
                   :zip 3243}}))

(defn goodbye [req]
  (rsp/response "Goodbye, World!"))

(defn hello-n [{{:keys [name]} :params}]
  (rsp/response (str "Hello, " name "!")))

(defn not-found [req]
  (-> "404 Not Found"
      rsp/response
      (rsp/status 404)))

(defn logger-middleware [req]
  (>!! sliding-chan req)
  req)

(def router
  (ring/ring-handler
    (ring/router
      [["/api"
        ["/query/:name" hello-n]
        ["/goodbye"     goodbye]
        ["/hello"       hello]]
       ["/*path"        not-found]]
      {:conflicts (comp println reitit.core/conflicts-str)})))

(def app
  (comp router logger-middleware))
