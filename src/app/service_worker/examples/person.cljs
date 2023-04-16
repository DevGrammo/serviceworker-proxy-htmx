(ns service-worker.examples.person
  (:require [service-worker.html.hiccup-components :as components]
            [clojure.string :as string]))

(def person-loader
  [:div#person-loader
   {:style "display:hidden"
    :hx-target "#person-summary"
    :hx-get ""
    :hx-trigger "loadPerson"
    :hx-swap "outerHTML"
    :hx-indicator "#load-person-indicator"
    :hx-select "#person-summary"}])

"on click set API to @data-api 
          then call document.getElementById(\"person\")
          then if result.className is 'hidden' toggle .displayPerson on #person end
          then get #person-loader then set it @hx-get to API
          then call htmx.process(it)
          then call htmx.trigger('#person-loader', 'loadPerson')"

(defn trigger-button [api text id]
  [:button
   {:id id
    :_ "on click call document.getElementById('person')
          then if result.className is 'hidden' wait 0.1s then toggle .displayPerson on #person else 
          remove .displayPerson from #person then wait 0.1s then toggle .displayPerson on #person end
          "
    :hx-indicator "#load-person-indicator"
    :hx-get api
    :hx-target "#person-summary"
    :hx-select "#person-summary"
    :hx-swap "outerHTML"}
   text])

(def close-button
  [:svg.close-menu
   {:viewbox "0 0 100 100",
    :xmlns "http://www.w3.org/2000/svg"
    :width 24
    :height 24
    :_ "on click remove .displayPerson from #person
          then get #person-loader then set it @hx-get to null"}
   [:circle
    {:cx 50
     :cy 50
     :r 50
     :fill "inherit"}]
   [:g.close-symbol
    [:polyline {:points "25,25 75,75"}]
    [:polyline {:points "25,75 75,25"}]]])

(defn persons-example []
  [:section.person
   [:h1 [:code "Persons example"] (components/htmx-indicator {:id "load-person-indicator" :class "zen-indicator"})]
   [:p "Serving static API from " [:code "static/persons/"] " through the " [:code "web-server"]]

   [:div.call-to-action
    [:p "Choose a person:"]
    (trigger-button "/static/persons/a.html" "Thasup" "A")
    (trigger-button "/static/persons/b.html" "Mara Sattei" "B")]

   [:div#person.hidden
    [:div.close close-button]
    [:summary#person-summary]]])

