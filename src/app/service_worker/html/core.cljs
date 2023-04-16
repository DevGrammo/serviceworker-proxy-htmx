(ns service-worker.html.core
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]))

(defn include-script [hiccup-form script]
  (conj hiccup-form [:script script]))

(defn include-link [hiccup-form link]
  (conj hiccup-form [:link link]))

(defn include-meta [hiccup-form meta]
  (conj hiccup-form [:meta meta]))

(defn create-default-head []
  [:head
   [:title "title"]
   [:meta {:charset "UTF-8"}]
   [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
   [:meta {:http-equiv "Origin-Agent-Cluster" :content "?1"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:link {:rel "stylesheet" :href "/static/css/style.css"}]])

(defn create-document
  ([body]
   [:html
    (create-default-head)
    body])
  ([head body]
   [:html
    head
    body]))

(defn render-html [hiccup-form]
  (str (html hiccup-form)))