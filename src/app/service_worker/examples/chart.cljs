(ns service-worker.examples.chart
  (:require [service-worker.html.hiccup-components :as components]
            [clojure.string :as string]))

(defn chart-example []
  [:section.chart
   [:h1 [:code "Chart example"]]
   [:p "Make a chart"]

   [:div.call-to-action
    [:p "Draw"]
    [:button "chart"]]

   [:div.box
    [:div.left
     "Table"]
    [:div.right]]])

