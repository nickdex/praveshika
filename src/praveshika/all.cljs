(ns praveshika.all
  (:require-macros [hiccups.core :refer [html]])
  (:require [praveshika.db :as db]))

(defn transactions-list-item [transaction]
  (html
   [:li#transaction.border.p-4
    [:button.p-1.bg-red-400.text-white.float-right
     {:name "remove-button"}
     [:svg.w-6.h-6 {:xmlns "http://www.w3.org/2000/svg"
                    :fill "none"
                    :viewBox "0 0 24 24"
                    :stroke-width "1.5"
                    :stroke "currentColor"}
      [:path
       {:stroke-linecap "round"
        :stroke-linejoin "round"
        :d
        "M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0"}]]]
    [:div.grid.grid-cols-3
     [:span.text-lg.text-green-700.font-bold (:date transaction)]
     [:span (:payee transaction)]
     [:span (:tag transaction)]]
    [:ul.mt-2
     (for [posting (:postings transaction)]
       [:li.grid.grid-cols-3
        [:span.col-span-2 (:account posting)]
        [:span
         [:span (:amount posting)]
         "&nbsp"
         [:span (:currency posting)]]
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