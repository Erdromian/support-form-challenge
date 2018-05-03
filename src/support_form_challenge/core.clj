(ns support-form-challenge.core
  (:require [ring.adapter.jetty :as j]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [hiccup.core :as hiccup]
            [hiccup.form :as f]))

(def support-form
  (hiccup/html
    [:div
     [:h1 "Hello World?"]
     (f/form-to [:post "/"]
       (f/label "Category" "Support Category")
       (f/drop-down "Category" [1 2 3 4])
       (f/label "Message" "How can we help?")
       (f/text-area "Message")
       (f/label "File" "Picture of the issue?")
       (f/file-upload "File")
       (f/label "Email" "Enter your Email")
       (f/email-field "Email")
       (f/submit-button "Submit"))]))

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
