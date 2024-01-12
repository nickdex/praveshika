(ns praveshika.db
  (:require [cognitect.transit :as t]))

(def r (t/reader :json))
(def w (t/writer :json))

(defn make-posting
  ([posting]
   (apply make-posting (vals posting)))
  ([account amount currency comment]
   {:account account
    :amount (if (= "" amount) 0 (js/parseInt amount))
    :currency (if-not currency "INR" currency)
    :comment (if (empty? comment) nil comment)}))

(defn make-transaction
  ([transaction]
   (let [{:keys [date payee tag postings]} transaction]
     (make-transaction date payee tag (when postings (map make-posting postings)))))
  ([date payee tag postings]
   {:date date
    :payee payee
    :tag (if (empty? tag) nil tag)
    :postings (vec postings)}))

(defn get-all-transactions
  "Fetch all transactions from data store"
  []
  (->> (js/localStorage.getItem "transactions")
       (t/read r)
       (map make-transaction)))

(defn- reset-transactions!
  [transactions]
  (->> transactions
       (sort-by :date)
       reverse
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