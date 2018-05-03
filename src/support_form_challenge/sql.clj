(ns support-form-challenge.sql
  (:require [clojure.spec.alpha :as s]
            [clojure.java.jdbc :as sql]))

(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname "support-form.db"})

; TODO: Create table if not exists (so we don't keep dropping all data)
(defn remake-table [target-db]
  (sql/execute! target-db ["drop table if exists requests"])
  (let [creation-script (sql/create-table-ddl
                          :requests
                          [[:id :integer :primary :key :autoincrement]
                           [:category :text :not :null]
                           [:email :text :not :null]
                           [:message :text :not :null]
                           [:file :blob]])]
    (sql/execute! db [creation-script])))
(remake-table db)

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))

(s/def ::message-body string?)
(s/def ::category string?) ; TODO: enumerate the categorys instead
(s/def ::email ::email-type)
;(s/def ::file) ;TODO: file type...

(s/def ::form-data (s/keys :req-un [::email ::category ::message-body]
                           :opt-un [::file]))
(defn store [{:keys [email category message-body file] :as form-data}]
  {:pre [(s/valid? ::form-data form-data)]}
  (sql/insert! db :requests {:category category
                             :message message-body
                             :email email
                             :file file}))

(defn get-all []
  (sql/query db ["select id, category, email, message, file from requests"]))
