(ns praveshika.settings
  (:require-macros [hiccups.core :refer [html]])
  (:require [praveshika.db :as db]
            [clojure.string :as str]))

(defn- chip-li [item]
  [:li.p-2.text-center.text-sm
   [:span item]
   [:button.remove.float-right.rounded-full.ml-2.px-2.bg-red-200
    [:svg.w-4.h-5 {:xmlns "http://www.w3.org/2000/svg"
                   :fill "none"
                   :viewBox "0 0 24 24"
                   :stroke-width "2"
                   :stroke "currentColor"}
     [:path
      {:stroke-linecap "round" :stroke-linejoin "round" :d "M6 18 18 6M6 6l12 12"}]]]])

(defn input
  [{:keys [input-id label list default-value]}]
  [:label.sr-only {:for input-id}
   (str/capitalize label)]
  [:div.w-full.inline-block.relative
   [:input.w-full.rounded-md.border-0.py-1.pr-10.text-gray-900.ring-1.ring-inset.ring-gray-300.placeholder:text-gray-400.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm.sm:leading-6
    {:name label
     :id input-id
     :placeholder  (str "Enter" " " label)
     :value default-value
     :list list}]
   [:button.clear.absolute.z-10.right-0.top-1.pr-2.py-0.pl-2
    [:svg.w-6.h-6 {:xmlns "http://www.w3.org/2000/svg"
                   :fill "none"
                   :viewBox "0 0 24 24"
                   :stroke-width "1"
                   :stroke "currentColor"}
     [:path
      {:stroke-linecap "round"
       :stroke-linejoin "round"
       :d "m9.75 9.75 4.5 4.5m0-4.5-4.5 4.5M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"}]]]])

(defn- list-editor-component [{:keys [label items]}]
  [:article
   {:class label}
   [:div.flex
    (input {:input-id (str "new-" label)
            :label label})
    [:button.add.ml-2.bg-blue-500.px-2.text-white.rounded-lg "Add"]]
   [:ul#payees.border.my-2.h-32.overflow-y-auto.divide-y
    (for [item items]
      (chip-li item))]])

(defn home []
  [:section.page.pt-4.px-4.hidden.gap-y-3.grid
   {:data-link "settings"}
   (list-editor-component
    {:label "payee"
     :items (db/get-all-payees)})
   (list-editor-component
    {:label "account"
     :items (db/get-all-accounts)})])

(defn delete-payee!
  "Remove payee from app"
  ([event]
   (.preventDefault event)
   (let [payee-el (.. event -currentTarget -parentElement)]
     (.remove payee-el)
     (db/remove-payee! (.. payee-el -firstChild -textContent)))))

(defn register-remove-button-click-listeners [buttons remove-fn]
  (doseq [remove-button buttons]
    (.addEventListener remove-button
                       "click"
                       remove-fn)))

(defn refresh-payees!
  ([]
   (refresh-payees! (db/get-all-payees)))
  ([payees]
   (set! (.-innerHTML (js/document.getElementById "payees"))
         (html
          (map chip-li payees)))
   (register-remove-button-click-listeners
    (js/document.querySelectorAll "article.payee button.remove") delete-payee!)))

(defn add-payee!
  "Add new payee to app"
  [event]
  (.preventDefault event)
  (.. event -target -classList (add "bg-green-500"))
  (let [input-el (.. event -currentTarget -parentElement (querySelector "input"))
        payee-name (.-value input-el)]
    (db/add-payee! payee-name)
    (set! (.-value input-el) nil))
  (js/setTimeout #(.. event -target -classList (remove "bg-green-500")) 1500))

(defn delete-account!
  "Remove account from app"
  ([event]
   (.preventDefault event)
   (let [account-el (.. event -currentTarget -parentElement)]
     (.remove account-el)
     (db/remove-account! (.. account-el -firstChild -textContent)))))

(defn refresh-accounts!
  ([]
   (refresh-accounts! (db/get-all-accounts)))
  ([accounts]
   (set! (.-innerHTML (js/document.getElementById "accounts"))
         (html
          (map chip-li accounts)))
   (register-remove-button-click-listeners
    (js/document.querySelectorAll "article.account button.remove")
    delete-account!)))

(defn add-account!
  "Add new account to app"
  [event]
  (.preventDefault event)
  (.. event -target -classList (add "bg-green-500"))
  (let [input-el (.. event -currentTarget -parentElement (querySelector "input"))
        account-name (.-value input-el)]
    (db/add-account! account-name)
    (set! (.-value input-el) nil))
  (js/setTimeout #(.. event -target -classList (remove "bg-green-500")) 1500))