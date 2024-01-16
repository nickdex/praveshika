(ns praveshika.settings
  (:require-macros [hiccups.core :refer [html]])
  (:require [praveshika.db :as db]))

(defn- chip-li [item]
  [:li.inline-block.p-2.text-center.text-sm.bg-green-300.m-2.rounded-full
   [:span item]
   [:button.remove.float-right.rounded-full.ml-2.px-2.bg-red-200
    [:svg.w-4.h-5 {:xmlns "http://www.w3.org/2000/svg"
                   :fill "none"
                   :viewBox "0 0 24 24"
                   :stroke-width "2"
                   :stroke "currentColor"}
     [:path
      {:stroke-linecap "round" :stroke-linejoin "round" :d "M6 18 18 6M6 6l12 12"}]]]])

(defn page []
  [:section.page.p-4.hidden
   {:data-link "settings"}
   [:article.payee
    [:label.mr-2 "Payee"
     [:input.ml-2 {:name "payee"
                   :placeholder "Enter Payee"}]]
    [:button.add.mx-2.bg-blue-500.p-2.text-white.rounded-lg "Add"]
    [:ul#payees.border.my-2.h-32.overflow-y-auto
     (for [item (db/get-all-payees)]
       (chip-li item))]]
   [:article.account
    [:label.mr-2 "Account"
     [:input.ml-2 {:name "account"
                   :placeholder "Enter Account"}]]
    [:button.add.mx-2.bg-blue-500.p-2.text-white.rounded-lg "Add"]
    [:ul#accounts.border.my-2.h-32.overflow-y-auto
     (for [item @db/accounts]
       (chip-li item))]]])

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