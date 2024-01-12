(ns praveshika.common
  (:require [clojure.string :as str]))
(defn get-todays-date []
  (let [today (js/Date.)
        year (.getFullYear today)
        month (str (inc (.getMonth today))) ; JavaScript months are 0-indexed
        day (.getDate today)
        formatted-month (if (< (count month) 2) (str "0" month) month)
        formatted-day (if (< (count (str day)) 2) (str "0" day) day)]
    (str year "-" formatted-month "-" formatted-day)))

(defn ->hledger-transaction
  [transaction]
  (let [{:keys [date payee tag postings]} transaction
        ->hledger-posting (fn [{:keys [account amount currency comment]}]
                            (str account "   "
                                 amount " "
                                 currency
                                 (when comment (str "\n    ; " comment))))]
    (str date " " payee (when tag (str " ; " tag))
         (when postings 
           (str "\n"
                (->> postings
                     (map ->hledger-posting)
                     (str/join "\n")))))))

(defn ->hledger-transactions [transactions]
  (->> transactions
       (map ->hledger-transaction)
       (str/join "\n\n")))