(ns test-project.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [test-project.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[test-project started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[test-project has shut down successfully]=-"))
   :middleware wrap-dev})
