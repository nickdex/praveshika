(ns praveshika.db)

(def transactions (atom []))
(defrecord Posting [account amount currency comment])
(defrecord Transaction [date payee tag postings])

#_{:clj-kondo/ignore [:redefined-var]}
(defn ->Posting [account amount currency comment]
  (Posting. account
            (if (empty? amount) 0 amount)
            currency
            comment))
