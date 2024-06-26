(ns praveshika.db
  (:require
   [alandipert.storage-atom :refer [local-storage]]
   [praveshika.transaction :as t]))

(def transactions (local-storage (atom []) :transactions))
(def payees (local-storage (atom ["Swiggy" "Shoppy Mart" "Zomato" "Shell"]) :payees))
(def accounts (local-storage (atom ["Expenses:Education:Books"
                                    "Expenses:Education:Online Courses"
                                    "Expenses:Entertainment"
                                    "Expenses:Entertainment:Events"
                                    "Expenses:Entertainment:Games"
                                    "Expenses:Entertainment:Movies"
                                    "Expenses:Entertainment:Toys"
                                    "Expenses:Food"
                                    "Expenses:Food:Beverages"
                                    "Expenses:Food:Groceries"
                                    "Expenses:Health"
                                    "Expenses:Health:Dental"
                                    "Expenses:Health:Eye"
                                    "Expenses:Health:Medicine"
                                    "Expenses:Health:Personal Care"
                                    "Expenses:Health:Skin"
                                    "Expenses:Hobbies:Sports"
                                    "Expenses:Misc. Brokerage"
                                    "Expenses:Miscellaneous:Bank Charges"
                                    "Expenses:Miscellaneous:Brokerage"
                                    "Expenses:Miscellaneous:Charges"
                                    "Expenses:Miscellaneous:Fraud"
                                    "Expenses:Miscellaneous:Gifts"
                                    "Expenses:Miscellaneous:Job"
                                    "Expenses:Miscellaneous:Office Supplies"
                                    "Expenses:Miscellaneous:Shipping"
                                    "Expenses:Miscellaneous:Transportation"
                                    "Expenses:Miscellaneous:Website"
                                    "Expenses:People"
                                    "Expenses:Shopping"
                                    "Expenses:Shopping:Accessories"
                                    "Expenses:Shopping:Apparel"
                                    "Expenses:Shopping:Apparel:Shoes"
                                    "Expenses:Shopping:Electronics"
                                    "Expenses:Shopping:Electronics:Extra"
                                    "Expenses:Shopping:Software"
                                    "Expenses:Tax"
                                    "Expenses:Tax:GST"
                                    "Expenses:Tax:Import"
                                    "Expenses:Tax:Markup"
                                    "Expenses:Travel"
                                    "Expenses:Travel:Cab"
                                    "Expenses:Travel:Equipment"
                                    "Expenses:Travel:Railway"
                                    "Expenses:Travel:Trip"
                                    "Expenses:Unknown"
                                    "Expenses:Utilities"
                                    "Expenses:Utilities:Cell Phone"
                                    "Expenses:Utilities:Gas"
                                    "Expenses:Utilities:Home"
                                    "Expenses:Utilities:Internet"
                                    "Expenses:Utilities:Rent"]) :accounts))

(defn get-all-transactions
  "Fetch all transactions from data store"
  []
  (->> @transactions
       (sort-by :date)
       reverse
       vec
       (map t/make-transaction)))

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

(defn get-all-accounts
  []
  (->> @accounts
       vec))

(defn add-account!
  [account]
  (swap! accounts conj account))

(defn remove-account!
  "Remove an account from data store"
  [account]
  (swap! accounts remove-element account))
