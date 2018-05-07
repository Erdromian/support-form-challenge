(ns support-form-challenge.templates
  (:require [hiccup.core :as hiccup]
            [hiccup.form :as f]
            [support-form-challenge.shared-spec :refer [categories]]))

; An unfortunate hack to add :enctype to the hiccup form
(defn with-enctype
  "Adds encoding type of the form, to a vector since f/form-to doesn't support that option"
  [enctype form-body]
  (update form-body 1 #(assoc % :enctype enctype)))

(defn support-form []
  (hiccup/html
    [:div
     [:h1 "Support Page"]
     (with-enctype "multipart/form-data"
                   (f/form-to [:post ""]
                              (f/label "category" "Support Category")
                              [:br]
                              (f/drop-down "category" categories)
                              [:br]
                              (f/label "message" "How can we help?")
                              [:br]
                              (f/text-area "message")
                              [:br]
                              (f/label "file" "Picture of the issue?")
                              [:br]
                              (f/file-upload "file")
                              [:br]
                              (f/label "email" "Enter your Email")
                              [:br]
                              (f/email-field "email")
                              [:br]
                              (f/submit-button "Submit")))]))
