(ns service-worker.examples.ping)

(defn pings-example []
  [:section
   [:h1 [:code "Pings example"]]
   [:div
    [:div.call-to-action
     [:p "Ping the " [:code "proxy-server"] " with a " [:code "GET"] " at " [:code "/api/ping"]]
     [:button
      {:id "ping"
       :hx-target "#pings"
       :hx-get "/api/ping"
       :hx-swap "beforeend"}
     "ping"]]
    [:div.call-to-action
     [:p "Clear pings"]
     [:button
      {:_ "on click transition <div.removable-ping/> opacity to 0 then remove <div.removable-ping/>"}
      "clear"]]]
   [:div#pings]])