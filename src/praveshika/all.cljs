(ns praveshika.all
  (:require-macros [hiccups.core :refer [html]])
  (:require [praveshika.db :as db]))

(defn transactions-list-item [transaction]
  (html
   [:li#transaction.border.p-4
    [:div.grid.grid-cols-3
     [:span.text-lg.text-green-700.font-bold (:date transaction)]
     [:span (:payee transaction)]
     [:span (:tag transaction)]]
    [:ul.mt-2
     (for [posting (:postings transaction)]
       [:li.grid.grid-cols-3
        [:span.col-span-2 (:account posting)]
        [:span (:amount posting)]
        [:span.col-span-full (:comment posting)]])]]))

(defn insert-latest-transaction [transaction]
  (.insertAdjacentHTML (js/document.getElementById "transactions")
                       "afterbegin"
                       (transactions-list-item transaction)))

(defn all-transactions-page []
  (html
   [:div#all-transactions-page.min-h-full.grid.hidden
    [:ul#transactions.m-2
     (for [transaction (->> @db/transactions
                            (sort-by :date)
                            reverse)]
       (transactions-list-item transaction))]]))