(ns praveshika.db
  (:require [cognitect.transit :as t]))

(def r (t/reader :json))
(def w (t/writer :json))

(defn make-transaction [date payee tag postings]
  {:date date
   :payee payee
   :tag tag
   :postings (vec postings)})

(defn make-posting [account amount currency comment]
  {:account account
   :amount (if (empty? amount) 0 amount)
   :currency currency
   :comment comment})

(defn get-all-transactions
  "Fetch all transactions from data store"
  []
  (t/read r (js/localStorage.getItem "transactions")))

(defn- reset-transactions!
  [transactions]
  (->> transactions
       vec
       (t/write w)
       (js/localStorage.setItem "transactions")))

(defn remove-transaction!
  "Remove a transaction from data store"
  [transaction]
  (->> (get-all-transactions)
       (remove #(= transaction %))
       reset-transactions!))
  
(defn prepend-transaction!
  "Add latest transaction on top in data store"
  [transaction]
  (->> (get-all-transactions)
       (cons transaction)
       reset-transactions!))
       
