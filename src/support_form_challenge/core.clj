(ns support-form-challenge.core
  (:require [ring.adapter.jetty :as j]))

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(defn boot []
  (j/run-jetty handler {:port 8080}))

(defn -main
  "I just start up a server"
  [& args]
  (println "Hello, World!")
  (boot))
