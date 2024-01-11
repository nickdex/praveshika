(ns praveshika.all
  (:require-macros [hiccups.core :refer [html]])
  (:require [praveshika.db :as db]))

(defn remove-transaction! [e remove-button]
  (.preventDefault e)
  (js/console.debug "remove-transaction entered")
  (let [transaction-container (.-parentElement remove-button)
        values (map #(.-textContent %) (.querySelectorAll transaction-container "span"))
        postings (->> values
                      (drop 3)
                      (partition 4)
                      (map #(apply db/make-posting %)))
        transaction (->> postings
                         (conj (take 3 values))
                         (apply db/make-transaction))]
    (js/console.debug "removing transaction" transaction)
    (.remove transaction-container)
    (db/remove-transaction! transaction)))

(defn transactions-list-item [transaction]
  (html
   [:li.transaction-container.border.p-4
    [:button.remove.p-1.bg-red-400.text-white.float-right.active:bg-red-600
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
    [:div.transaction-header.grid.grid-cols-3
     [:span.date.text-lg.text-green-700.font-bold (:date transaction)]
     [:span.payee (:payee transaction)]
     [:span.tag (:tag transaction)]]
    [:ul.postings.mt-2
     (for [posting (:postings transaction)]
       [:li.posting-container.grid.grid-cols-3
        [:span.account.col-span-2 (:account posting)]
        [:div
         [:span (:amount posting)]
         "&nbsp"
         [:span (:currency posting)]]
        [:span.col-span-full (:comment posting)]])]]))

(defn prepend-transaction! [transaction]
  (.insertAdjacentHTML (js/document.getElementById "transactions")
                       "afterbegin"
                       (transactions-list-item transaction)))

(defn insert-latest-transaction! [transaction]
  ;; Update View
  (prepend-transaction! transaction)
  ;; Attcha click listener
  (let [remove-button (js/document.querySelector  "#transactions > :first-child button.remove")]
    (.addEventListener remove-button "click" #(remove-transaction! % remove-button))))

(defn all-transactions-page []
  (html
   [:div#all-transactions-page.min-h-full.grid.hidden
    [:ul#transactions.m-2
     (for [transaction (db/get-all-transactions)]
       (transactions-list-item transaction))]]))