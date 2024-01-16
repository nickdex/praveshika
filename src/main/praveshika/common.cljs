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

(defn- separator
  ([]
   (separator 1))
  ([times]
   (apply str (repeat (* times 4) " "))))

(defn ->hledger-transaction
  [transaction]
  (let [{:keys [date payee tag postings]} transaction
        ->hledger-posting (fn [{:keys [account amount currency comment]}]
                            (str (separator)
                                 account (separator)
                                 amount " "
                                 currency
                                 (when comment
                                   (str "\n"
                                        (->>
                                         (str/split-lines comment)
                                         (map #(str (separator 2)  "; " %))
                                         (str/join "\n"))))))]
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