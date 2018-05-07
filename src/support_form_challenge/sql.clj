(ns support-form-challenge.sql
  (:require [clojure.spec.alpha :as s]
            [clojure.java.jdbc :as sql]
            [support-form-challenge.shared-spec :as specs]
            [clojure.java.io :as io]))

(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname "support-form.db"})

; TODO: Create table if not exists (so we don't keep dropping all data)
(defn remake-tables [target-db]
  (sql/execute! target-db ["drop table if exists requests"])
  (sql/execute! target-db ["drop table if exists files"])
  (let [requests-table-script
        (sql/create-table-ddl
            :requests
            [[:id :integer :primary :key :autoincrement]
             [:category :text :not :null] ; TODO?: use an enumeration of numbers for this?
             [:email :text :not :null]
             [:message :text :not :null]
             [:file_id :integer "references files (id)"]]) ; NOTE: SQLite does not support actually using Foreign Keys.
                                                ; If you switch to another SQL, check that the reference actually works
        files-table-script
        (sql/create-table-ddl
          :files
          [[:id :integer :primary :key :autoincrement]
           [:filename :text :not :null]
           [:content_type :text :not :null] ; TODO?: Enumeration?
           [:size :integer :not :null]
           [:file :blob :not :null]])]
    (sql/db-do-commands db [files-table-script requests-table-script])))
(remake-tables db) ; TODO: move this table definition somewhere sane.

(defn file->bytes
  [file]
  (let [xout (java.io.ByteArrayOutputStream.)]
    (io/copy file xout)
    (.toByteArray xout)))

(defn unbox-last-rowid
  [SQL-response]
  (-> SQL-response
      first
      vals
      first))

(defn store-file [{:keys [filename content-type size tempfile] :as file}]
  {:pre [(s/valid? specs/file-map file)]}
  {:post [integer?]}
  (-> (sql/insert! db :files {:filename filename
                              :content_type content-type
                              :size size
                              :file (file->bytes tempfile)})  ; TODO?: move data transform out of the insert?
      unbox-last-rowid)) ; TODO: check that rowID stays consistent if we delete entries

; TODO: Upsert-only?  Don't recreate duplicates, filter user spamming.
(defn store [{:keys [email category message file] :as form-data}]
  {:pre [(s/valid? specs/form-data form-data)]}
  {:post [integer?]}
  ; TODO: do both inserts in a single transaction
  (-> (if (or (nil? file) (= 0 (:size file)))
        (sql/insert! db :requests {:category category
                                   :message message
                                   :email email})
        (let [file-id (store-file file)]
          (sql/insert! db :requests {:category category
                                     :message message
                                     :email email
                                     :file_id file-id})))
    unbox-last-rowid))

(def base-get-query "SELECT r.id, r.category, r.email, r.message, f.filename, f.content_type, f.size, f.file FROM requests AS r
    LEFT JOIN files AS f ON r.file_id = f.id")

(defn get-request
  ([] (sql/query db [base-get-query]))
  ([id](sql/query db [(str base-get-query " where r.id = ?") id])))
