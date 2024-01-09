(ns praveshika.core
  (:require-macros [hiccups.core :refer [html]])
  (:require [hiccups.runtime]))

(def transactions (atom []))

(defn load-transactions!
  "Load transactions from data store to app store"
  []
  (when-let [db-transactions (js/localStorage.getItem "transactions")]
    (reset! transactions db-transactions)))

(defn get-todays-date []
  (let [today (js/Date.)
        year (.getFullYear today)
        month (str (inc (.getMonth today))) ; JavaScript months are 0-indexed
        day (.getDate today)
        formatted-month (if (< (count month) 2) (str "0" month) month)
        formatted-day (if (< (count (str day)) 2) (str "0" day) day)]
    (str year "-" formatted-month "-" formatted-day)))

(defn- account-select []
  (let [accounts ["ICICI"
                  "SBI"
                  "Sodexo:6102"]]
    [:select#account.w-full {:name "account"}
     (for [val accounts]
       [:option {:value (str "Asset:Checking" val)} val])]))

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

(defn- all-transactions-page []
  (html
   [:div#all-transactions-page
    [:div.m-2
     (for [transaction (->> @transactions
                            (sort-by :date)
                            reverse)]
       [:div#transaction.border.p-4
        [:div.grid.grid-cols-3
         [:span.text-lg.text-green-700.font-bold (:date transaction)]
         [:span (:payee transaction)]
         [:span (:tag transaction)]]
        [:ul.mt-2
         (for [posting (:postings transaction)]
           [:li.grid.grid-cols-2
            [:span (:account posting)]
            [:span (:amount posting)]
            [:span.col-span-full (:comment posting)]])]])]]))

(defn new-transaction-page []
  [:div#new-transaction-page.hidden
   [:form.px-4.space-y-5
    [:div.mt-2.grid.grid-cols-2.gap-x-6.gap-y-5
     [:div.col-span-full
      [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "date"}
       "Date"]
      [:input#date {:type "date" :value (get-todays-date)}]]
     [:div
      [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "payee"}
       "Payee"]
      (payee-select)]
     [:div
      [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "tag"}
       "Tag"]
      (tag-select)]]
    [:div#postings
     [:div.flex.p-2
      [:h1.block.grow.font-medium.leading-6.text-gray-900.text-xl.text-center
       "Postings"]
      [:button.bg-blue-500.rounded-md.px-6.py-2.text-center.text-white "+"]]
     [:fieldset#posting.col-span-full.p-4.border.border-neutral-300.space-y-3
      {:name "posting"}
      [:div
       [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "account"}
        "Account"]
       (account-select)]
      [:div
       [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "amount"}
        "Amount"]
       [:div.relative.mt-2.rounded-md.shadow-sm
        [:input#amount.block.w-full.rounded-md.border-0.py-1.5.pr-20.text-gray-900.ring-1.ring-inset.ring-gray-300.placeholder:text-gray-400.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm.sm:leading-6
         {:type "number" :name "amount" :placeholder "0.00"}]
        [:div.absolute.inset-y-0.right-0.flex.items-center
         [:label.sr-only {:for "currency"}
          "Currency"]
         [:select#currency.h-full.rounded-md.border-0.bg-transparent.py-0.pl-2.pr-7.text-gray-500.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm
          {:name "currency"}
          [:option "INR"]
          [:option "USD"]
          [:option "EUR"]]]]]
      [:div
       [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "comment"}
        "Comment"]
       [:input#comment {:type "text" :name "comment"}]]]]
    [:button#save-transaction.w-full.bg-blue-500.rounded-md.px-6.py-2.text-center.text-white
     {:type "button"} "Save"]]])

(defn- save-transaction []
  (let [get-value (fn [element-id]
                    (-> (.getElementById js/document element-id)
                        .-value))
        date (get-value "date")
        payee (get-value "payee")
        tag (get-value "tag")]
    (swap! transactions conj
           {:date date
            :payee payee
            :tag tag
            :postings [{:account "ICICI"
                        :amount 10}]})
    ;; Save to data store
    (js/localStorage.setItem "transactions" @transactions)))

(defn route [route-id]
  (let [all-element (.getElementById js/document "all-transactions-page")
        new-element (.getElementById js/document "new-transaction-page")]
    (condp = route-id
      :new (do (.add (.-classList all-element) "hidden")
               (.remove (.-classList new-element) "hidden"))
      :all (do (.add (.-classList new-element) "hidden")
               (.remove (.-classList all-element) "hidden")))))

(defn- tab-nav-bar []
  [:ul.flex.flex-wrap.text-sm.font-medium.text-center.text-gray-500.border-b.border-gray-200
   [:li.me-2.grow
    [:a#new-transaction-link.inline-block.p-4.text-blue-600.bg-gray-100.rounded-t-lg.active
     {:aria-current "page"}
     "New Transaction"]]
   [:li.me-2.grow
    [:a#all-transactions-link.inline-block.p-4.rounded-t-lg.hover:text-gray-600.hover:bg-gray-50
     {:href "#"}
     "All Transactions"]]])

(defn app []
  (html
   [:header.mt-2 [:h1.text-center.text-2xl.font-semibold "Praveshika"]]
   (tab-nav-bar)
   [:div#shell
    (new-transaction-page)
    (all-transactions-page)]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn ^:dev/after-load main []
  (load-transactions!)
  ;; Create App
  (aset (.getElementById js/document "app") "innerHTML" (app))
  ;; Register Click Listeners
  (-> (.getElementById js/document "save-transaction")
      (.addEventListener "click" save-transaction))
  (-> (.getElementById js/document "all-transactions-link")
      (.addEventListener "click" #(route :all)))
  (-> (.getElementById js/document "new-transaction-link")
      (.addEventListener "click" #(route :new))))
