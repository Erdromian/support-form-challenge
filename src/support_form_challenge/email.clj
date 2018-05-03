(ns support-form-challenge.email
  (:require [clojure.spec.alpha :as s]
            [support-form-challenge.shared-spec :as specs]))

(def notify-target "austin@finlinson.net")

(defn send-email [{:keys [email category message-body file] :as form-data}]
  {:pre [(s/valid? specs/form-data form-data)]}
  (println form-data))

