(ns praveshika.settings)

(def payees (flatten (repeat 5 ["Swiggy" "Zomato" "Shoppy Mart"])))
(defn page []
  [:div#settings.page.p-4
   {:data-link "settings"}
   [:label.mr-2 "Payee"
    [:input.ml-2 {:placeholder "Enter Payee"}]]
   [:button.mx-2.bg-blue-500.p-2.text-white.rounded-lg "Add"]
   [:ul.border.my-2.h-32.overflow-y-auto
    (for [item payees]
      [:li.inline-block.p-2.text-center.text-sm.bg-green-300.m-2.rounded-full item
       [:button.float-right.rounded-full.ml-2.px-2.bg-red-400.text-white "X"]])]])
