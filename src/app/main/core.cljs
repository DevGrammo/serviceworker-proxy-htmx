(ns main.core)

(defn init [] (js/console.log "main.core started")
  (set! (.. js/htmx -config -useTemplateFragments) true))
