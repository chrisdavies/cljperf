(ns cljperf.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [cheshire.core :as ches]
            [ring.util.request :as req]
            [taoensso.timbre :as log]
            [ring.util.response :as rsp]
            [timbre-ns-pattern-level]))

; Disable super spammy logging from hikari (connection pool)
(log/merge-config!
  {:level :error})
  ;  :middleware [(timbre-ns-pattern-level/middleware {"com.zaxxer.hikari.*" :error
  ;                                                    :all :debug})]})

; (defn wrap-body-json [handler]
;   (fn [request]
;     (let [body-str (req/body-string request)
;           body-json (when (seq body-str)
;                           (ches/parse-string body-str true))]
;       (handler (assoc request :body body-json)))))


(defn app [req]
  (rsp/response "Hello, World!"))

(def app-reload
  (wrap-reload #'app))

(defn run []
  (jetty/run-jetty #'app-reload {:port 8080 :join? false}))
