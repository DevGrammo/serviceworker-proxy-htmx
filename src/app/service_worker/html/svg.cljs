(ns service-worker.html.svg
  (:require [clojure.string :as string]))

(def payments
  [{:agent "agentVoid@null.dev"
    :amount 20
    :pos 3
    :type "debit"
    :previous-balance 120
    :current-balance 100}
   {:agent "agentVoid@null.eth"
    :amount 20
    :pos 2
    :type "credit"
    :previous-balance 100
    :current-balance 120}
   {:agent "agentVoid@null.xyz"
    :amount 10
    :type "credit"
    :pos 1
    :previous-balance 90
    :current-balance 100}])

(defn random-payments []
  (->>
   (for [i (range 20)]
     {:agent "agentVoid@null.xyz"
      :pos i
      :value (rand-int 300)
      :info (str "message:" (rand))})
   (reverse)
   (into [])))

(defn make-graph [values]
  (let [max-value (->> values (map :value) (apply max))
        max-pos  (->> values (map :pos) (apply max))
        with-x-scale (fn [v] (+ (/ (* 80 v) max-pos) 10))
        with-y-scale (fn [v] (- 200 (+ (/ (* 80 v) max-value) 10)))
        get-line (fn [{:keys [value pos]}] (str (with-x-scale pos) "," (with-y-scale value)))]
    {:max-value max-value
     :max-pos max-pos
     :points (->>
              (for [{:keys [value pos]} values]
                [:svg {:preserveAspectRatio "meet"}
                 [:g
                  [:circle
                   {:cx (with-x-scale pos)
                    :cy (with-y-scale value)

                    :r "1",
                    :_  (str "on mouseenter toggle .visible on #" (str "p-tooltip-" pos) " until mouseleave")
                    :fill "red"
                    :viewbox "0 0 100 100"}]]])
              (into (list)))
     :tooltips (->>
                (for [{:keys [info pos value]} values]
                  [:text.svg-tooltip
                   {:x (with-x-scale pos)
                    :y (with-y-scale value)
                    :id (str "p-tooltip-" pos)
                    :fill "black"
                    :font-size "2px"
                    :viewbox "0 0 10 10"}
                   info])
                (into (list)))
     :line [:polyline
            {:points (->> values (map get-line) (string/join " "))
             :fill "none",
             :stroke-linejoin "round"
             :stroke "grey"}]}))

(defn test-2 []
  (let [graph (make-graph (random-payments))]
    [:svg {:viewbox "0 0 100 100",
           :xmlns "http://www.w3.org/2000/svg"
           :id "output"}
     [:defs
      "<!-- Arrowhead marker definition -->"
      [:marker {:id "arrow",
                :viewbox "0 0 10 10",
                :refx "5",
                :refy "5",
                :markerwidth "6",
                :markerheight "6",
                :orient "auto-start-reverse"}
       [:path {:d "M 0 0 L 10 5 L 0 10 z"}]]]

     "<!-- Coordinate axes with an arrowhead in both directions -->"
     [:polyline.cartesian-axes {:points "10,10 10,90 90,90",
                                :fill "none",
                                :stroke "black",
                                :marker-start "url(#arrow)",
                                :marker-end "url(#arrow)"}]
     "<!-- Data line with polymarkers -->"
     (:line graph)
     (:points graph)
     (doall (:tooltips graph))]))

(defn price-chart [rows-data]
  (let [graph (make-graph rows-data)]
    [:svg.price-chart
     {:viewbox "0 0 100 100",
      :xmlns "http://www.w3.org/2000/svg"
      :id "output"
      :preserveAspectRatio "none"}
     [:defs
      "<!-- Arrowhead marker definition -->"
      [:marker {:id "arrow",
                :viewbox "0 0 10 10",
                :refx "5",
                :refy "5",
                :markerwidth "6",
                :markerheight "6",
                :orient "auto-start-reverse"}
       [:path {:d "M 0 0 L 10 5 L 0 10 z"}]]]

     "<!-- Coordinate axes with an arrowhead in both directions -->"
     [:polyline.cartesian-axes {:points "10,10 10,90 90,90",
                                :fill "none",
                                :stroke "black",
                                :marker-start "url(#arrow)",
                                :marker-end "url(#arrow)"}]
     "<!-- Data line with polymarkers -->"
     (:line graph)
     (:points graph)
     (doall (:tooltips graph))]))


(defn zen-svg []
  (test-2))


(defn make-graph-dev [values]
  (let [max-value (->> values (map :value) (apply max))
        max-pos  (->> values (map :pos) (apply max))

        with-x-scale (fn [v] (+ 5 (* 45 v)))
        with-y-scale (fn [v] (+ 5 (- 190 (/ (* 190 v) max-value))))

        get-line (fn [{:keys [value pos]}] (str (with-x-scale pos) "," (with-y-scale value)))]
    {:max-value max-value
     :max-pos max-pos
     :points (->>
              (for [{:keys [value pos]} values]
                [:g
                 [:circle
                  {:cx (with-x-scale pos)
                   :cy (with-y-scale value)
                   :r "4",
                   :_  (str "on mouseenter toggle .visible on #" (str "p-tooltip-" pos) " until mouseleave")
                   :fill "red"
                   :viewbox "0 0 10 10"}]])
              (into (list)))
     :tooltips (->>
                (for [{:keys [info pos value]} values]
                  [:text.svg-tooltip
                   {:x (+ (with-x-scale pos) 5)
                    :y (- (with-y-scale value) 7.5)
                    :id (str "p-tooltip-" pos)
                    :fill "black"
                    :font-size "20px"
                    :viewbox "0 0 10 10"}
                   (str info value)])
                (into (list)))
     :line
     [:polyline
      {:points (->> values (map get-line) (string/join " "))
       :fill "none",
       :stroke-linejoin "round"
       :stroke "grey"}]}))

(defn create-price-chart [width rows-data]
  (let [graph (make-graph-dev
               (->>
                rows-data
                (take (inc (js/Math.floor (/ width 45))))
                (reverse)
                (map-indexed (fn [idx item] (assoc item :pos idx)))))]
    [:svg#chart-container
     {:viewbox (str "0 0 " width " " 205),
      :xmlns "http://www.w3.org/2000/svg"}
     "<!-- Data line with polymarkers -->"
     (:line graph)
     (:points graph)
     (doall (:tooltips graph))]))


