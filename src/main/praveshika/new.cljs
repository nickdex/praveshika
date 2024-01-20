(ns praveshika.new
  (:require-macros [hiccups.core :refer [html]])
  (:require [clojure.string :as str]
            [praveshika.common :as common]
            [praveshika.db :as db]))

(defn- account-select []
  [:div.account.relative.mt-2.rounded-md.shadow-sm.relative
   [:label.block.text-sm.font-medium.leading-6.text-gray-900
    "Account"
    [:input.block.w-full.rounded-md.border-0.py-1.pr-10.text-gray-900.ring-1.ring-inset.ring-gray-300.placeholder:text-gray-400.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm.sm:leading-6
     {:name "account"
      :value "Assets:Cash"
      :list "account-suggestion"}]]
   [:button.clear.absolute.z-10.right-0.top-7.pr-2.py-0.pl-2
    [:svg.w-6.h-6 {:xmlns "http://www.w3.org/2000/svg"
                   :fill "none"
                   :viewBox "0 0 24 24"
                   :stroke-width "1"
                   :stroke "currentColor"}
     [:path
      {:stroke-linecap "round"
       :stroke-linejoin "round"
       :d "m9.75 9.75 4.5 4.5m0-4.5-4.5 4.5M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"}]]]
   [:datalist#account-suggestion
    (for [val (db/get-all-accounts)]
      [:option val])]])

(defn- payee-select []
  [:div.payee.relative.mt-2.rounded-md.shadow-sm.relative
   [:label.block.text-sm.font-medium.leading-6.text-gray-900
    "Payee"
    [:input#payee.block.w-full.rounded-md.border-0.py-1.pr-10.text-gray-900.ring-1.ring-inset.ring-gray-300.placeholder:text-gray-400.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm.sm:leading-6
     {:name "payee"
      :value "Swiggy"
      :list "payee-suggestion"}]]
   [:button.clear.absolute.z-10.right-0.top-7.pr-2.py-0.pl-2
    [:svg.w-6.h-6 {:xmlns "http://www.w3.org/2000/svg"
                   :fill "none"
                   :viewBox "0 0 24 24"
                   :stroke-width "1"
                   :stroke "currentColor"}
     [:path
      {:stroke-linecap "round"
       :stroke-linejoin "round"
       :d "m9.75 9.75 4.5 4.5m0-4.5-4.5 4.5M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"}]]]
   [:datalist#payee-suggestion
    (for [val (db/get-all-payees)]
      [:option val])]])

(defn refresh-payees!
  [payees]
  (set! (js/document.querySelector "select#payee") -innerHTML
        (html (map (fn [element] [:option element]) payees))))

(defn refresh-accounts!
  [payees]
  (set! (js/document.querySelector "select.accounts") -innerHTML
        (html (map (fn [element] [:option element]) payees))))

(defn- tag-select []
  [:label.block.text-sm.font-medium.leading-6.text-gray-900
   "Tag"
   [:select#tag.w-full.rounded-md.border-0.py-1.text-gray-900.ring-1.ring-inset.ring-gray-300.placeholder:text-gray-400.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm.sm:leading-6
    {:name "tag"}
    [:option {:value ""} "-"]
    (let [tag ["Friends"]]
      (for [val tag]
        [:option {:value val} val]))]])

(defn posting-values! [posting]
  (->> (.querySelectorAll posting "#postings input, #postings select, #postings textarea")
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
    (account-select)
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
     [:textarea.w-full.rounded-md.border-0.py-1.text-gray-900.ring-1.ring-inset.ring-gray-300.placeholder:text-gray-400.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm.sm:leading-6
      {:type "text"
       :name "comment"
       :rows 2
       :placeholder "Use for adding item(s) details"}]]]))

(defn add-posting! [e]
  (.preventDefault e)
  (.insertAdjacentHTML (js/document.getElementById "postings") "beforeend" (posting)))

(defn home []
  [:section.page
   {:data-link "new"}
   [:form.px-4
    [:div.mt-2.gap-x-6.grid.grid-cols-2
     [:label.block.text-sm.font-medium.leading-6.text-gray-900
      "Date"
      [:input#date.block.w-full.rounded-md.border-0.py-1.text-gray-900.ring-1.ring-inset.ring-gray-300.placeholder:text-gray-400.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm.sm:leading-6
       {:type "date" :value (common/get-todays-date)}]]
     (tag-select)]
    (payee-select)
    [:div.flex.items-center.my-3
     [:h2.grow.font-medium.leading-6.text-gray-900.text-xl.text-center
      "Postings"]
     [:button#add-posting.bg-blue-500.rounded-md.px-3.py-1.text-center.text-white
      [:svg.w-6.h-6 {:xmlns "http://www.w3.org/2000/svg"
                     :fill "none"
                     :viewBox "0 0 24 24"
                     :stroke-width "1.5"
                     :stroke "currentColor"}
       [:path {:stroke-linecap "round"
               :stroke-linejoin "round"
               :d "M12 4.5v15m7.5-7.5h-15"}]]]]
    [:ul#postings
     (posting)]
    [:button#save-transaction.w-full.bg-blue-500.rounded-md.px-6.py-2.text-center.text-white
     "Save"]]]) 
