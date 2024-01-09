(ns praveshika.core
  (:require-macros [hiccups.core :refer [html]])
  (:require [hiccups.runtime]
            [praveshika.all :as all]
            [praveshika.db :as db]
            [praveshika.new :as new]))

(defn load-transactions!
  "Load transactions from data store to app store"
  []
  (when-let [db-transactions (-> "transactions"
                                 js/localStorage.getItem
                                 js/JSON.parse
                                 (js->clj :keywordize-keys true))]
    (reset! db/transactions  db-transactions)))

(defn route [route-id]
  (let [all-element (.getElementById js/document "all-transactions-page")
        new-element (.getElementById js/document "new-transaction-page")
        all-link (.getElementById js/document "all-transactions-link")
        new-link (.getElementById js/document "new-transaction-link")]
    (condp = route-id
      :new (do (.. all-element -classList (add "hidden"))
               (.. new-element -classList (remove "hidden"))
               (.. all-link -classList (remove "text-blue-600" "bg-gray-100"))
               (.. new-link -classList (add "text-blue-600" "bg-gray-100")))
      :all (do (.. new-element -classList (add "hidden"))
               (.. all-element -classList (remove "hidden"))
               (.. new-link -classList (remove "text-blue-600" "bg-gray-100"))
               (.. all-link -classList (add "text-blue-600" "bg-gray-100"))))))

(defn- tab-nav-bar []
  [:ul#nav-bar.flex.flex-wrap.text-sm.font-medium.text-center.text-gray-500.border-b.border-gray-200
   [:li#new-transaction-link.nav-link.me-2.grow.rounded-t-lg.cursor-pointer.hover:bg-gray-200.text-blue-600.bg-gray-100
    [:a.inline-block.p-4
     {:aria-current "page"}
     "New Transaction"]]
   [:li#all-transactions-link.nav-link.me-2.grow.rounded-t-lg.cursor-pointer.hover:bg-gray-200
    [:a.inline-block.p-4
     {:href "#"}
     "All Transactions"]]])

(defn app []
  (html
   [:header.mt-2 [:h1.text-center.text-2xl.font-semibold "Praveshika"]]
   (tab-nav-bar)
   [:div#shell.min-h-full
    (new/new-transaction-page)
    (all/all-transactions-page)]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn ^:dev/after-load main []
  (load-transactions!)
  ;; Create App
  (aset (.getElementById js/document "app") "innerHTML" (app))
  ;; Register Click Listeners
  (-> (js/document.getElementById "add-posting")
      (.addEventListener "click" new/add-posting))
  (->> (.getElementsByName js/document "remove-button")
       (map (fn [el]
              (.addEventListener el
                                 "click"
                                 #(.. el -parentElement remove))))
       doall)
  (-> (.getElementById js/document "save-transaction")
      (.addEventListener "click" new/save-transaction))
  (-> (.getElementById js/document "all-transactions-link")
      (.addEventListener "click" #(route :all)))
  (-> (.getElementById js/document "new-transaction-link")
      (.addEventListener "click" #(route :new))))
