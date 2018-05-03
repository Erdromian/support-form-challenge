(ns support-form-challenge.core
  (:require [ring.adapter.jetty :as j]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [hiccup.core :as hiccup]))

(def support-form
  (hiccup/html
    [:h1 "Hello World"]))

(defmulti
  ;"Naive router that dispatches based on request-method only.  Consider Compojure instead."
  handler
  :request-method)

(defmethod handler :get [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body support-form})

; TODO: Do input validation.
(defmethod handler :post [req]
  {:status 200})

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
