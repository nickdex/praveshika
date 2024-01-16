(ns praveshika.new
  (:require-macros [hiccups.core :refer [html]])
  (:require [clojure.string :as str]
            [praveshika.common :as common]
            [praveshika.db :as db]))

(defn- account-select []
  (let [accounts @db/accounts]
    [:select.accounts.w-full {:name "account"}
     (for [val accounts]
       [:option val])]))

(defn- payee-select []
  (let [payee (db/get-all-payees)]
    [:select#payee.w-full {:name "payee"}
     (for [val payee]
       [:option val])]))

(defn refresh-payees!
  [payees]
  (set! (js/document.querySelector "select#payee") -innerHTML
        (html (map (fn [element] [:option element]) payees))))

(defn refresh-accounts!
  [payees]
  (set! (js/document.querySelector "select.accounts") -innerHTML
        (html (map (fn [element] [:option element]) payees))))

(defn- tag-select []
  (let [tag ["Friends"]]
    [:select#tag.w-full {:name "tag"}
     [:option {:value ""} "-"]
     (for [val tag]
       [:option {:value val} val])]))

(defn posting-values! [posting]
  (->> (.querySelectorAll posting "#postings input, #postings select")
       (map #(.-value %))
       (apply db/make-posting)))

(defn create-transaction
  "Creates a transaction object from view"
  []
  (let [get-value (fn [element-id]
                    (-> (.getElementById js/document element-id)
                        .-value))
        date (str/replace (get-value "date") #"-" "/")
        payee (get-value "payee")
        tag (get-value "tag")
        postings (->> (js/document.querySelectorAll "li.posting")
                      (map posting-values!))]
    (db/make-transaction date payee tag postings)))

(defn save-transaction! [event]
  (.. event -target -classList (add "bg-green-500"))
  (db/add-transaction! (create-transaction))
  (js/setTimeout #(.. event -target -classList (remove "bg-green-500")) 1500))

(defn- posting []
  (html
   [:li.posting.my-1.col-span-full.p-4.border.border-neutral-300.space-y-3
    [:label.block.text-sm.font-medium.leading-6.text-gray-900
     "Account"
     (account-select)]
    [:div.relative.mt-2.rounded-md.shadow-sm
     [:label.block.text-sm.font-medium.leading-6.text-gray-900
      "Amount"
      [:input.block.w-full.rounded-md.border-0.py-1.pr-20.text-gray-900.ring-1.ring-inset.ring-gray-300.placeholder:text-gray-400.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm.sm:leading-6
       {:type "number" :placeholder "0.00" :name "amount"}]]
     [:label.absolute.right-0.top-7
      [:span.sr-only
       "Currency"]
      [:select.h-full.rounded-md.border-0.bg-transparent.py-0.pl-2.pr-7.text-gray-500.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm
       {:name "currency"}
       [:option "INR"]
       [:option "USD"]
       [:option "EUR"]]]]
    [:label.block.text-sm.font-medium.leading-6.text-gray-900
     [:span.block "Comment"]
     [:input {:type "text" :name "comment"}]]]))

(defn add-posting! [e]
  (.preventDefault e)
  (.insertAdjacentHTML (js/document.getElementById "postings") "beforeend" (posting)))

(defn new-transaction-page []
  [:section.page
   {:data-link "new"}
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
