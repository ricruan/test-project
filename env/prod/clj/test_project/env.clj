(ns test-project.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[test-project started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[test-project has shut down successfully]=-"))
   :middleware identity})
