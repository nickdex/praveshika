(ns praveshika.db
  (:require [cognitect.transit :as t]
            [alandipert.storage-atom :refer [local-storage]]))

(def transactions (local-storage (atom []) :transactions))
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
  (->> @transactions
       (sort-by :date)
       reverse
       vec
       (map make-transaction)))

(defn remove-element
  [coll element]
  (remove #(= % element) coll))

(defn remove-transaction!
  "Remove a transaction from data store"
  [transaction]
  (swap! transactions remove-element transaction))

(defn add-transaction!
  "Add latest transaction on top in data store"
  [transaction]
  (swap! transactions conj transaction))

(defn get-all-payees
  "Fetch all payees from data store"
  []
  (->> (js/localStorage.getItem "payees")
       (t/read r)))

(defn reset-payees!
  [payees]
  (->> payees
       vec
       (t/write w)
       (js/localStorage.setItem "payees")))

(defn add-payee!
  "Add payee on top in data store"
  [payee]
  (->> (get-all-payees)
       (cons payee)
       vec
       reset-payees!))

(defn remove-payee!
  "Remove a payee from data store"
  [payee]
  (->> (get-all-payees)
       (remove #(= payee %))
       reset-payees!))
