(ns support-form-challenge.core
  (:require [ring.adapter.jetty :as j]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [support-form-challenge.templates :refer [support-form]]
            [support-form-challenge.shared-spec :refer [categories]]
            [support-form-challenge.sql :refer [store make-tables]]
            [support-form-challenge.email :refer [send-email]]))

; An unfortunate hack to add :enctype to the hiccup form
(defn with-enctype
  "Adds encoding type of the form, to a vector since f/form-to doesn't support that option"
  [enctype form-body]
  (update form-body 1 #(assoc % :enctype enctype)))

(defn page-response []
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (support-form)})

(defn format-form-data
  "Reformats the map how I like.
  In this case, by keywording the given string keys, then nulling empty file uploads."
  [raw-params]
  (let [keyworded (reduce-kv
                    (fn [m k v] (assoc m (keyword k) v))
                    {}
                    raw-params)]
    (if (-> keyworded
            :file
            :size
            (= 0))
      (dissoc keyworded :file)
      keyworded)))

; TODO: validation, that on failure triggers the form to show red for bad input
; TODO?: validate that :size actually reflects upload size before storing
(defn validate-form-data
  ""
  [form-data])

(defn handle-upload [form-data]
  (let [pretty-data (format-form-data form-data)
        stored-id (store pretty-data)]
    (send-email (assoc pretty-data :id stored-id))))

(defn one-page-handler [req]
  (if (= :post (:request-method req))
    (let [form-data (:params req)]
      (handle-upload form-data)))
  (page-response))

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
  ; TODO?: Command-line arguments for initializing mailgun settings?
  (make-tables)
  (boot))
