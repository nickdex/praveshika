(ns praveshika.settings
  (:require [praveshika.db :as db]))

(defn page []
  [:div#settings.page.p-4.hidden
   {:data-link "settings"}
   [:label.mr-2 "Payee"
    [:input.ml-2 {:placeholder "Enter Payee"}]]
   [:button.mx-2.bg-blue-500.p-2.text-white.rounded-lg "Add"]
   [:ul.border.my-2.h-32.overflow-y-auto
    (for [item (db/get-all-payees)]
      [:li.inline-block.p-2.text-center.text-sm.bg-green-300.m-2.rounded-full
       [:span item]
       [:button.remove.float-right.rounded-full.ml-2.px-2.bg-red-200
        [:svg.w-4.h-5 {:xmlns "http://www.w3.org/2000/svg"
                       :fill "none"
                       :viewBox "0 0 24 24"
                       :stroke-width "2"
                       :stroke "currentColor"}
         [:path
          {:stroke-linecap "round" :stroke-linejoin "round" :d "M6 18 18 6M6 6l12 12"}]]]])]])

(defn delete-payee!
  "Remove payee from app"
  ([event]
   (.preventDefault event)
   (let [payee-el (.. event -currentTarget -parentElement)]
     (.remove payee-el)
     (db/remove-payee! (.. payee-el -firstChild -textContent)))))

(defn register-remove-button-click-listeners []
  (doseq [remove-button (js/document.querySelectorAll "#settings button.remove")]
    (.addEventListener remove-button
                       "click"
                       delete-payee!)))