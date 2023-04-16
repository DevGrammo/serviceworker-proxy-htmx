(ns service-worker.examples.table
  (:require [browser-level :refer [BrowserLevel]]
            [clojure.edn :as edn]
            [service-worker.html.hiccup-components :as components]))

(def ^js wealth-db-disk (new BrowserLevel "db"))

(def wealth-db (atom false))

(defn load-db! []
  (if @wealth-db
    (js/Promise.resolve wealth-db)
    (-> (.get wealth-db-disk "db")
        (.then (fn [r]
                 (reset! wealth-db (edn/read-string r))
                 wealth-db))
        (.catch (fn [err]
                  (js/console.warn err)
                  (reset! wealth-db {:balance 100 :history [] :rows []})
                  wealth-db)))))

(defn make-fake-payment! []
  (-> (load-db!)
      (.then (fn [db]
               (let [domain (rand-nth ["com" "xyz" "org"])
                     agent (str "agent_void@null." domain)
                     id (str (js/crypto.randomUUID))
                     ^js date (new js/Date)
                     debit (rand-int (:balance @db))
                     payment {:type "debit"
                              :agent agent
                              :amount debit
                              :info (str "amount: " debit " " (- (:balance @db) debit))
                              :value (- (:balance @db) debit)
                              :date (.getTime date)}
                     row [:tr.debit
                          [:td  {:data-col-header "id"} [:code.id id]]
                          [:td  {:data-col-header "amount"} [:code "- " debit]]
                          [:td agent]
                          [:td (str (.toDateString date) " - " (.getHours date) ":" (.getMinutes date) ":" (.getSeconds date))]]
                     new-db (swap!
                             wealth-db
                             (fn [db]
                               (-> db
                                   (update :balance (fn [b] (- b debit)))
                                   (update :history (fn [h] (cons payment h)))
                                   (update :rows (fn [h] (cons row h))))))]
                 (-> (.put wealth-db-disk "db" (str new-db))
                     (.then (fn [] (into (list) (reverse (take 10 (:rows @db))))))))))))

(defn receive-fake-payment! []
  (-> (load-db!)
      (.then (fn [^js db]
               (js/console.log "wrong here?" db)
               (let [domain (rand-nth ["com" "xyz" "org"])
                     agent (str "agent_void@null." domain)
                     id (str (js/crypto.randomUUID))
                     ^js date (new js/Date)
                     credit (rand-int 200)
                     payment {:type "debit"
                              :agent agent
                              :amount credit
                              :info (str "amount: " credit "\n" "date: " date "\n" "balance: " (+ (:balance @db) credit))
                              :value (+ (:balance @db) credit)
                              :date (.getTime date)}
                     row [:tr.credit
                          [:td  {:data-col-header "id"} [:code.id id]]
                          [:td  {:data-col-header "amount"} [:code "+ " credit]]
                          [:td agent]
                          [:td (str (.toDateString date) " - " (.getHours date) ":" (.getMinutes date) ":" (.getSeconds date))]]
                     new-db (swap!
                             wealth-db
                             (fn [db]
                               (-> db
                                   (update :balance (fn [b] (+ b credit)))
                                   (update :history (fn [h] (cons payment h)))
                                   (update :rows (fn [h] (cons row h))))))]
                 (-> (.put wealth-db-disk "db" (str new-db))
                     (.then (fn []
                              (js/console.log "TAKING THOSE ROWS:")
                              (into (list) (reverse (take 10 (:rows @db))))))))))))

(defn wealth-example [db]
  [:div#wealth
   {:hx-indicator "#load-wealth-indicator"}
   [:h1
    [:code "Wealth App example"]
    [:button {:hx-get "/api/wealth"
              :hx-indicator "#load-wealth-indicator"
              :hx-swap "outerHTML"
              :hx-vals "js:{width: document.getElementById(\"chart-container\").clientWidth}"
              :hx-target "#wealth"
              :style "margin:5px"}
     "reload"]]
   [:p "Data are persisted in indexed db"]
   [:div {:style "display:flex; gap:30px; padding-left:40px;padding-right:40px"}
    [:div.controller
     [:div#balance-value
      [:b [:code.value (:balance @db)]]]
     (components/htmx-indicator
      {:id "load-wealth-indicator" :class "wealth-indicator"})
     [:div.buttons
      [:button
       {:style "height: 50px"
        :hx-target "#payments"
        :hx-ext "json-enc"
        :hx-indicator "#load-wealth-indicator"
        :hx-swap "outerHTML"
        :hx-vals "js:{width: includeComputedWidth('chart-container'), payment: 'credit'}"
        :hx-put "/api/wealth"}
       [:code "ask money"]]
      [:button
       {:style "height: 50px"
        :hx-target "#payments"
        :hx-ext "json-enc"
        :hx-indicator "#load-wealth-indicator"
        :hx-swap "outerHTML"
        :hx-vals "js:{width: includeComputedWidth('chart-container'), payment: 'debit'}"
        :hx-put "/api/wealth"}
       [:code "make payment"]]]]
    [:div.svg-wrapper
     [:svg#chart-container
      {:style "width:100%"
       :hx-get "/api/wealth"
       :hx-indicator "#load-wealth-indicator"
       :hx-trigger "load"
       :hx-target "#chart-container"
       :hx-vals "js:{width: includeComputedWidth('chart-container')}"
       :hx-swap "outerHTML"}]]]
   [:div.table-section
    [:table
     [:thead
      [:tr
       [:th {:data-col-header "id"} "id"]
       [:th {:data-col-header "amount"} "amount"]
       [:th {:data-col-header "agent"} "agent"]
       [:th {:data-col-header "date"} "date"]]]
     (into
      [:tbody#payments
       (doall (take 10 (:rows @db)))])]]])