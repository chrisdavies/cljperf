(ns cljperf.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [cheshire.core :as ches]
            [ring.util.request :as req]
            [taoensso.timbre :as log]
            [ring.util.response :as rsp]
            [cljs-router.core :as router]
            [camel-snake-kebab.core :as camel]
            [clojure.walk :as w]
            [timbre-ns-pattern-level]))

; Disable super spammy logging from hikari (connection pool)
(log/merge-config!
  {:level :info
   :middleware [(timbre-ns-pattern-level/middleware {"com.zaxxer.hikari.*" :error
                                                     :all :info})]})

; (defn wrap-body-json [handler]
;   (fn [request]
;     (let [body-str (req/body-string request)
;           body-json (when (seq body-str)
;                           (ches/parse-string body-str true))]
;       (handler (assoc request :body body-json)))))

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
  (json {:hello "World!"}))

(defn goodbye [req]
  (rsp/response "Goodbye, World!"))

(defn hello-n [{{:keys [name]} :params}]
  (rsp/response (str "Hello, " name "!")))

(defn not-found [req]
  (-> "404 Not Found"
      rsp/response
      (rsp/status 404)))

(def routes (router/make-routes
             {"get /api/query/:name"               hello-n
              "get /api/goodbye"                   goodbye
              "get /api/hello"                     hello
              "*url"                               not-found}))

(defn run-handler [[f params] req]
  (f (assoc req :params params)))

(defn app [req]
  (log/info req)
  (let [uri (str (-> req :request-method name) " " (:uri req) "?" (:query-string req))]
    (-> uri
        (->> (router/route routes))
        (run-handler req))))

(def app-reload
  (wrap-reload #'app))

(defn run []
  (jetty/run-jetty #'app-reload {:port 8080 :join? false}))
