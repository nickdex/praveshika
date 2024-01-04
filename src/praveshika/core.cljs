(ns praveshika.core
  (:require-macros [hiccups.core :refer [html]]))

(defn- appendHTML [element htmlString]
  (.insertAdjacentHTML element "beforeend" htmlString))

(defn app []
  (html
   [:header.mt-2 [:h1.text-center.text-2xl.font-semibold "Praveshika"]]
   [:form#transaction.px-4.space-y-5
    [:div.mt-2.grid.grid-cols-2.gap-x-6.gap-y-5
     [:div.col-span-full
      [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "date"}
       "Date"]
      [:input#date {:type "date"}]]
     [:div
      [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "payee"}
       "Payee"]
      [:select#payee.w-full {:name "payee"}
       [:option {:value ""}]]]
     [:div
      [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "tag"}
       "Tag"]
      [:select#tag.w-full {:name "tag"}
       [:option {:value ""}]]]]
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
       [:select#account.w-full {:name "account"}
        [:option {:value ""}]]]
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
       [:input#comment {:type "text" :name "comment"}]]]]]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn main []
  (appendHTML (.getElementById js/document "app") (app)))
  
