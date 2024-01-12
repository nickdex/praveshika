(ns praveshika.core
  (:require-macros [hiccups.core :refer [html]])
  (:require [hiccups.runtime]
            [praveshika.all :as all]
            [praveshika.new :as new]
            [praveshika.settings :as settings]))

(defn set-active-link [el state]
  (if state
    (.. el -classList (add "text-blue-600" "bg-gray-100"))
    (.. el -classList (remove "text-blue-600" "bg-gray-100"))))

(defn set-active-page [el state]
  (if state
    (.. el -classList (remove "hidden"))
    (.. el -classList (add "hidden"))))

(defn route [event]
  (.preventDefault event)
  (let [pages (js/document.querySelectorAll ".page")
        links (js/document.querySelectorAll ".nav-link")
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
      (set-active-page un false))
    (doseq [link unmatched-links]
      (set-active-link link false))
    (set-active-link clicked-link true)
    (set-active-page (first matched-pages ) true)))

(defn- tab-nav-bar []
  [:ul#nav-bar.flex.flex-wrap.text-sm.font-medium.text-center.text-gray-500.border-b.border-gray-200
   [:li.nav-link.me-2.grow.rounded-t-lg.cursor-pointer.hover:bg-gray-200.text-blue-600.bg-gray-100
    {:data-link "new"}
    [:a.inline-block.p-4
     {:aria-current "page"}
     "New"]]
   [:li.nav-link.me-2.grow.rounded-t-lg.cursor-pointer.hover:bg-gray-200
    {:data-link "all"}
    [:a.inline-block.p-4
     {:href "#"}
     "History"]]
   [:li.nav-link.me-2.grow.rounded-t-lg.cursor-pointer.hover:bg-gray-200
    {:data-link "settings"}
    [:a.inline-block.p-4
     {:href "#"}
     "Settings"]]])

(defn app []
  (html
   [:header.mt-2 [:h1.text-center.text-2xl.font-semibold "Praveshika"]]
   (tab-nav-bar)
   [:div#shell.min-h-full
    (new/new-transaction-page)
    (all/all-transactions-page)
    (settings/page)]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn ^:dev/after-load main []
  ;; Create App
  (aset (.getElementById js/document "app") "innerHTML" (app))
  ;; Register Click Listeners
  (-> (js/document.getElementById "add-posting")
      (.addEventListener "click" new/add-posting!))
  (all/register-remove-button-click-listeners)
  (-> (.getElementById js/document "save-transaction")
      (.addEventListener "click" new/save-transaction!))
  (doseq [nav-link (js/document.querySelectorAll ".nav-link")]
    (.addEventListener nav-link "click" route))
  (-> (js/document.getElementById "copy")
      (.addEventListener "click" all/copy-transactions!)))
