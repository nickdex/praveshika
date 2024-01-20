(ns praveshika.core
  (:require-macros [hiccups.core :refer [html]])
  (:require [hiccups.runtime]
            [praveshika.all :as all]
            [praveshika.common :as common]
            [praveshika.db :as db]
            [praveshika.new :as new]
            [praveshika.settings :as settings]))

(defn set-active-link! [el state]
  (if state
    (.. el -classList (add "text-blue-600" "border-b" "border-b-blue-600"))
    (.. el -classList (remove "text-blue-600" "border-b" "border-b-blue-600"))))

(defn set-active-page! [el state]
  (if state
    (.. el -classList (remove "hidden"))
    (.. el -classList (add "hidden"))))

(defn route [event]
  (.preventDefault event)
  (let [pages (js/document.querySelectorAll ".page")
        links (js/document.querySelectorAll "nav li")
        clicked-link (.-currentTarget event)
        match-data-link (fn [element]
                          (=
                           (.. clicked-link -dataset -link)
                           (.. element -dataset -link)))
        {matched-pages true
         unmatched-pages false} (->> pages
                                     (group-by match-data-link))
        unmatched-links (->> links
                             (remove match-data-link))]
    (doseq [un unmatched-pages]
      (set-active-page! un false))
    (doseq [link unmatched-links]
      (set-active-link! link false))
    (set-active-link! clicked-link true)
    (set-active-page! (first matched-pages) true)))

(defn- tab-nav-bar []
  [:nav
   [:ol.flex.text-sm.font-medium.text-center.text-gray-400.border-b
    [:li.me-2.flex-1.w-full.hover:text-gray-600.hover:border-gray-600.text-blue-600.border-b.border-b-blue-600
     {:data-link "new"}
     [:a.inline-block.p-4
      {:aria-current "page"}
      "New"]]
    [:li.me-2.flex-1.w-full.hover:text-gray-600.hover:border-gray-600.hover:border-b
     {:data-link "all"}
     [:a.inline-block.p-4
      "History"]]
    [:li.me-2.flex-1.w-full.hover:text-gray-600.hover:border-gray-600.hover:border-b
     {:data-link "settings"}
     [:a.inline-block.p-4
      "Settings"]]]])

(defn app []
  (html
   [:header.py-2.bg-blue-500.text-white [:h1.text-center.text-2xl.font-semibold "Praveshika"]]
   (tab-nav-bar)
   [:main
    (new/home)
    (all/home)
    (settings/home)]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn ^:dev/after-load main []
  ;; Create App
  (aset (.getElementById js/document "app") "innerHTML" (app))
  ;; Register Click Listeners
  (doseq [nav-link (js/document.querySelectorAll "nav li")]
    (.addEventListener nav-link "click" route))
  (add-watch db/transactions
             :new
             #(all/refresh! (db/get-all-transactions)))
  (add-watch db/payees
             :new
             #(do (settings/refresh-payees! (db/get-all-payees))
                  (new/refresh-payees! (db/get-all-payees))))
  (add-watch db/accounts
             :new
             #(do (settings/refresh-accounts! (db/get-all-accounts))
                  (new/refresh-accounts! (db/get-all-accounts))))
  (-> (js/document.getElementById "add-posting")
      (.addEventListener "click" new/add-posting!))
  (all/register-remove-button-click-listeners)
  (settings/register-remove-button-click-listeners
   (js/document.querySelectorAll "article.account button.remove")
   settings/delete-account!)
  (settings/register-remove-button-click-listeners
   (js/document.querySelectorAll "article.payee button.remove")
   settings/delete-payee!)
  (doseq [button (js/document.querySelectorAll "button.clear")]
    (.addEventListener button "click" common/clear-input!))
  (-> (.getElementById js/document "save-transaction")
      (.addEventListener "click" new/save-transaction!))
  (-> (js/document.querySelector "article.payee button.add")
      (.addEventListener "click" settings/add-payee!))
  (-> (js/document.querySelector "article.account button.add")
      (.addEventListener "click" settings/add-account!))
  (-> (js/document.getElementById "copy")
      (.addEventListener "click" all/copy-transactions!)))
