(ns praveshika.db
  (:require
   [alandipert.storage-atom :refer [local-storage]]))

(def transactions (local-storage (atom []) :transactions))
(def payees (local-storage (atom ["Swiggy" "Shoppy Mart" "Zomato" "Shell"]) :payees))

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
  (->> @payees
       vec))

(defn add-payee!
  "Add payee on top in data store"
  [payee]
  (swap! payees conj payee))

(defn remove-payee!
  "Remove a payee from data store"
  [payee]
  (swap! payees remove-element payee))
