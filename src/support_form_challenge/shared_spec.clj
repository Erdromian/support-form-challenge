(ns support-form-challenge.shared-spec
  (:require [clojure.spec.alpha :as s]))

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))

(s/def ::message-body string?)
(s/def ::category string?) ; TODO: enumerate the categorys instead
(s/def ::email ::email-type)
;(s/def ::file) ;TODO: file type...

(s/def ::form-data (s/keys :req-un [::email ::category ::message-body]
                           :opt-un [::file]))

(def form-data ::form-data)