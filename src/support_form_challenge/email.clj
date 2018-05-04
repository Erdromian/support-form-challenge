(ns support-form-challenge.email
  (:require [clojure.core :refer [slurp spit]]
            [clojure.spec.alpha :as s]
            [support-form-challenge.shared-spec :as specs]
            [mailgun.mail :as mail]))

; TODO: add config instructions to README, and/or in server startup
(let [settings (read-string (slurp "mail-settings.txt"))]
  (def mailgun-api-key (:mailgun-key settings))
  (def mailgun-domain (:mailgun-domain settings))
  (def mail-target (:mail-target settings)))
; TODO: if file is missing, or api key is not found, give useful message.  And/or take api key as a startup parameter.

(defn make-notification-body [email category message]
  (str "Support request: " category
       "\nResponse address: " email
       "\n\n\"" message "\""))

; TODO: add database entry ID?
(defn make-mail-content [{:keys [email category message-body file] :as form-data}]
  {:pre [(s/valid? specs/form-data form-data)]}
  (let [content {:from "no-reply@test.net"
                 :to mail-target
                 :subject "Support Request"
                 :text (make-notification-body email category message-body)}]
    (if (nil? file)
      content
      (merge content {:attachment file})))) ; TODO: multipart encode

(defn send-email [{:keys [email category message-body file] :as form-data}]
  {:pre [(s/valid? specs/form-data form-data)]}
  (let [auth {:key mailgun-api-key :domain mailgun-domain}
        content (make-mail-content form-data)]
    (println auth content)
    (mail/send-mail auth content)))
