(ns support-form-challenge.core
  (:require [ring.adapter.jetty :as j]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World?"})

(def app
  (-> #'handler
      (wrap-reload '(support-form-challenge.core))
      (wrap-stacktrace)))

(defn boot []
  (j/run-jetty #'app {:port 8080}))

(defn -main
  "I just start up a server"
  [& args]
  (println "Hello, World!")
  (boot))
