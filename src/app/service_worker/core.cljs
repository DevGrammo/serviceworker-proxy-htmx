(ns service-worker.core
  (:require [service-worker.proxy-server.core :as proxy-server]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn init
  " - register service worker listeners"
  []
  (js/console.debug "SERVICE WORKER STARTED")

  (js/self.addEventListener
   "install"
   (fn []
     (js/console.debug "SERVICE WORKER Install: calling skipWaiting()")
     (.skipWaiting js/self)))

  (js/self.addEventListener
   "activate"
   (fn []
     (js/console.debug "SERVICE WORKER Activated: claiming clients")
     (.claim js/self.clients)
     (js/console.debug "SERVICE WORKER claimed clients. Fetch is active."))

   ;; PROXY SERVER HANDLER
   (js/self.addEventListener "fetch" proxy-server/handler)))