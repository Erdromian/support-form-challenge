(ns support-form-challenge.shared-spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::message string?)

(def categories #{"Bug Report" "Product Support" "Feedback" "Other"})
(s/def ::category categories)

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))
(s/def ::email ::email-type)

(s/def ::filename string?)

(s/def ::content-type string?) ; TODO: enumerate content types

(s/def ::file-type (partial instance? java.io.File))
(s/def ::tempfile ::file-type)

(s/def ::size int?)

; TODO: decide between file and tempfile
(s/def ::file-map (s/keys :req-un {::filename ::content-type ::tempfile ::size}))
(def file-map ::file-map)
;(s/def ::file-blob bytes?)
(s/def ::file ::file-map)

(s/def ::form-data (s/keys :req-un [::email ::category ::message]
                           :opt-un [::file]))
(def form-data ::form-data)