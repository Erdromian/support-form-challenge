(ns support-form-challenge.core
  (:require [ring.adapter.jetty :as j]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [hiccup.core :as hiccup]
            [hiccup.form :as f]))

; An unfortunate hack to add :enctype to the hiccup form
(defn with-enctype
  "Adds encoding type of the form, to a vector since f/form-to doesn't support that option"
  [enctype form-body]
  (update form-body 1 #(assoc % :enctype enctype)))

; TODO: support-form refilling to make more usable.  Maybe support error notifications too?
(def support-form
  (hiccup/html
    [:div
     [:h1 "Hello World?"]
     (with-enctype "multipart/form-data"
       (f/form-to [:post ""]
         (f/label "category" "Support Category")
         (f/drop-down "category" [1 2 3 4]) ; TODO: Enumeration of categories
         (f/label "message" "How can we help?")
         (f/text-area "message")
         (f/label "file" "Picture of the issue?")
         (f/file-upload "file")
         (f/label "email" "Enter your Email")
         (f/email-field "email")
         (f/submit-button "Submit")))]))

(def page-response
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body support-form})

(defn format-form-data
  "Reformats the map how I like.
  In this case, by just keywording the given string keys"
  [raw-params]
  (reduce-kv
    (fn [m k v] (assoc m (keyword k) v))
    {}
    raw-params))

; TODO: can this just be a :pre or :post check on other functions?
(defn validate-form-data
  ""
  [form-data])

; TODO: Store/email

(defn handle-upload [form-data]
  (-> form-data
      format-form-data))

  ; Conform data to what I like
  ; Validate what we wish
  ; Store
  ; Send email

(defn one-page-handler [req]
  (if (= :post (:request-method req))
    (println req
      "\n-P" (:params req)
      "\n-K" (format-form-data (:params req))))

  page-response)

(def app
  (-> #'one-page-handler
      (wrap-reload '(support-form-challenge.core))
      (wrap-stacktrace)
      wrap-params
      wrap-multipart-params))

(defn boot []
  (j/run-jetty #'app {:port 8080}))

(defn -main
  "I just start up a server"
  [& args]
  (println "Hello, World!")
  (boot))
