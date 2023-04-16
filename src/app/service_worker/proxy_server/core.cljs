(ns service-worker.proxy-server.core
  (:require
   [clojure.string :as string]
   [reitit.core :as r]
   [service-worker.html.core :as html]
   [service-worker.html.svg :as svg]
   [service-worker.proxy-server.impl :as impl]
   [service-worker.examples.person :as person]
   [service-worker.examples.chart :as chart]
   [service-worker.examples.ping :as ping]))

(def origin (str js/origin "/"))

(def routes
  [["/" {:name :home
         :get {:handler
               (fn [request]
                 (js/Promise.resolve
                  (impl/->Response
                   (html/render-html
                    (html/create-document
                     (->
                      (html/create-default-head)
                      (html/include-script {:src "https://unpkg.com/htmx.org@1.8.5"}))
                     (->
                      [:body
                       (ping/pings-example)
                       [:hr]
                       (person/persons-example)
                       [:hr]
                       (chart/chart-example)]
                      (html/include-script {:src "/static/js/functions._hs" :type "text/hyperscript"})
                      (html/include-script {:src "https://unpkg.com/hyperscript.org@0.9.8"})
                      (html/include-script {:src "https://unpkg.com/htmx.org/dist/ext/json-enc.js"})
                      (html/include-script {:src "/static/js/helpers.js"})
                      (html/include-script {:src "shared.js"})
                      (html/include-script {:src "main.js"}))))
                   [["Content-Type" "text/html; charset=utf-8"]
                    ["Origin-Agent-Cluster" "?1"]])))}}]
   ["/api/"
    ["ping"
     {:name :ping
      :get {:handler
            (fn [_]
              (js/Promise.resolve
               (impl/->Response
                (html/render-html
                 [:div
                  {:data-ping true
                   :_ "on click transition opacity to 0 then remove me"
                   :class "removable-ping"}
                  [:p (.toString (new js/Date))]])
                [["Content-Type" "text/html; charset=utf-8"]
                 ["Origin-Agent-Cluster" "?1"]])))}}]]])

(defn not-found []
  (js/Promise.resolve
   (->
    (impl/->Response
     [:div
      [:h1 [:code 404]]
      [:p "Not found.\nWe wish to hold the whole sky,\nBut we never will."]]
     [["Content-Type" "text-plain"]])
    (impl/set-status 404))))

(defn handler [^js e]
  (let [url (.. e -request -url)]
    (cond
      (or (= url origin)
          (string/starts-with? url (str origin "api")))
      (.respondWith
       e
       (->
        (impl/make-request (.. e -request))
        (.then
         (fn [request]
           (js/console.debug "request" (clj->js request))
           (let [match (r/match-by-path (r/router routes) (:uri request))
                 router-handler (get-in match [:data (:method request) :handler] not-found)
                 response (router-handler request)]
             (-> response
                 (.then (fn [r] (impl/to-json r)))))))))

      :else (js/console.debug "Service worker is not handling" url))))




