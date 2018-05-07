(ns support-form-challenge.email
  (:require [clojure.core :refer [slurp spit]]
            [clojure.spec.alpha :as s]
            [support-form-challenge.shared-spec :as specs]
            [mailgun.mail :as mail]
            [clojure.java.io :refer [file make-parents]]))

; TODO: add config instructions to README, and/or in server startup
(let [settings (read-string (slurp "mail-settings.txt"))]
  (def mailgun-api-key (:mailgun-key settings))
  (def mailgun-domain (:mailgun-domain settings))
  (def mail-target (:mail-target settings)))
; TODO: if file is missing, or api key is not found, give useful message.  And/or take api key as a startup parameter.

(defn make-notification-body [email category message id]
  (str "Support request: " category
       "\nRequest ID: " id
       "\nResponse address: " email
       "\nMessage: \"" message "\""))

(def temp-subdir "pretty-file-nameplace")

; If we want the recieved attachment to have its name and file format sent as well, it needs renaming.
(defn format-file [{:keys [filename content-type tempfile size] :as file-map}]
  (let [temp-dir (.getParent tempfile)
        real-name (file temp-dir temp-subdir filename)]
    (make-parents real-name)
    (.renameTo tempfile real-name)
    real-name))

(defn make-mail-content [{:keys [email category message file id] :as email-data}]
  {:pre [(s/valid? specs/email-form email-data)]}
  (let [content {:from "no-reply@test.net"
                 :to mail-target
                 :subject (str "Support Request #" id)
                 :text (make-notification-body email category message id)}]
    (if (nil? file)
      content
      (merge content {:attachment [(format-file file)]}))))

(defn send-email [{:keys [email category message file id] :as email-data}]
  {:pre [(s/valid? specs/email-form email-data)]}
  (let [auth {:key mailgun-api-key :domain mailgun-domain}
        content (make-mail-content email-data)
        temp-file (:attachment content)
        mail-response (mail/send-mail auth content)]; TODO: Use response to determine failures, etc.
    (if (not (nil? temp-file))
      (run! #(.delete %) temp-file)))) ; TODO: Check this for safety!!!  Allowing POST data to cause a file delete is dangerous!
