(ns praveshika.new
  (:require-macros [hiccups.core :refer [html]])
  (:require [clojure.string :as str]
            [praveshika.all :as all]
            [praveshika.common :as common]
            [praveshika.db :as db]))

(defn- account-select []
  (let [accounts ["ICICI"
                  "SBI"
                  "Sodexo:6102"]]
    [:select#account.posting-input.w-full {:name "account"}
     (for [val accounts]
       [:option {:value (str "Asset:Checking:" val)} val])]))

(defn- payee-select []
  (let [payee ["Swiggy"]]
    [:select#payee.w-full {:name "payee"}
     (for [val payee]
       [:option val])]))

(defn- tag-select []
  (let [tag ["Friends"]]
    [:select#tag.w-full {:name "tag"}
     [:option {:value ""} "-"]
     (for [val tag]
       [:option {:value val} val])]))

(defn posting-values [posting]
  (->> (.getElementsByClassName posting "posting-input")
       (map #(.-value %))
       (apply db/->Posting)))

(defn save-transaction [event]
  (.. event -target -classList (add "bg-green-500"))
  (let [get-value (fn [element-id]
                    (-> (.getElementById js/document element-id)
                        .-value))
        date (str/replace (get-value "date") #"-" "/")
        payee (get-value "payee")
        tag (get-value "tag")
        postings (->> (.getElementsByName js/document "posting")
                      (map posting-values))
        transaction (db/Transaction. date payee tag postings)]
    (swap! db/transactions conj
           transaction)
    ;; Update History
    (all/insert-latest-transaction transaction)
    ;; Save to data store
    (js/localStorage.setItem "transactions" (js/JSON.stringify (clj->js @db/transactions))))
  (js/setTimeout #(.. event -target -classList (remove "bg-green-500")) 1500))

(defn- posting []
  (html
   [:li#posting.my-1.col-span-full.p-4.border.border-neutral-300.space-y-3
    {:name "posting"}
    [:div
     [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "account"}
      "Account"]
     (account-select)]
    [:div
     [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "amount"}
      "Amount"]
     [:div.relative.mt-2.rounded-md.shadow-sm
      [:input#amount.posting-input.block.w-full.rounded-md.border-0.py-1.5.pr-20.text-gray-900.ring-1.ring-inset.ring-gray-300.placeholder:text-gray-400.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm.sm:leading-6
       {:type "number"  :placeholder "0.00"}]
      [:div.absolute.inset-y-0.right-0.flex.items-center
       [:label.sr-only {:for "currency"}
        "Currency"]
       [:select#currency.posting-input.h-full.rounded-md.border-0.bg-transparent.py-0.pl-2.pr-7.text-gray-500.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm
        {:name "currency"}
        [:option "INR"]
        [:option "USD"]
        [:option "EUR"]]]]]
    [:div
     [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "comment"}
      "Comment"]
     [:input#comment.posting-input {:type "text" :name "comment"}]]]))

(defn add-posting []
  (.insertAdjacentHTML (js/document.getElementById "postings") "beforeend" (posting)))

(defn new-transaction-page []
  [:div#new-transaction-page
   [:form.px-4.space-y-5
    [:div.mt-2.grid.grid-cols-2.gap-x-6.gap-y-5
     [:div.col-span-full
      [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "date"}
       "Date"]
      [:input#date {:type "date" :value (common/get-todays-date)}]]
     [:div
      [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "payee"}
       "Payee"]
      (payee-select)]
     [:div
      [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "tag"}
       "Tag"]
      (tag-select)]]
    [:div
     [:div.flex.p-2
      [:h1.block.grow.font-medium.leading-6.text-gray-900.text-xl.text-center
       "Postings"]
      [:button#add-posting.bg-blue-500.rounded-md.px-6.py-2.text-center.text-white "+"]]
     [:ul#postings
      (posting)]]
    [:button#save-transaction.w-full.bg-blue-500.rounded-md.px-6.py-2.text-center.text-white
     {:type "button"} "Save"]]]) 
