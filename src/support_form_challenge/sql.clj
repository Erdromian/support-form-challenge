(ns support-form-challenge.sql
  (:require [clojure.spec.alpha :as s]
            [clojure.java.jdbc :as sql]
            [support-form-challenge.shared-spec :as specs]))

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

; TODO: Make a table for the files only.  Put a foreign key constraint from file id to here

; TODO: Upsert-only?  Don't recreate duplicates, filter user spamming.
(defn store [{:keys [email category message file] :as form-data}]
  {:pre [(s/valid? specs/form-data form-data)]}
  (sql/insert! db :requests {:category category
                             :message message
                             :email email
                             :file file}))

; TODO: Use a transaction for file storage/body storage

(defn get-all []
  (sql/query db ["select id, category, email, message, file from requests"]))
