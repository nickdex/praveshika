(ns praveshika.db)

(def transactions (atom []))
(defrecord Posting [account amount currency comment])
(defrecord Transaction [date payee tag postings])
