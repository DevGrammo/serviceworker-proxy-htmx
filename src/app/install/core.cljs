(ns install.core
  "Install the service worker declared in index.html")

(defn init
  "Install the service worker"
  []
  (letfn [(get-service-worker-script []
            (.-content (js/document.head.querySelector "[name=service-worker][content]")))]
    (js/console.debug "install.core: CLIENT INSTALLING SERVICE WORKER:" (get-service-worker-script))
    (.addEventListener
     js/window.navigator.serviceWorker
     "controllerchange"
     (fn [_]
       (let [registration-completed? (.getItem js/localStorage "registration")]
         (js/console.debug "registration-completed:" registration-completed?)
         (when-not registration-completed?
           (.setItem js/localStorage "registration" true)
           (.reload js/location)))))
    (js/navigator.serviceWorker.register (get-service-worker-script))))
