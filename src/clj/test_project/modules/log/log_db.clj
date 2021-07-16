(ns test-project.modules.log.log-db
  (:require [conman.core :as conman]
            [test-project.db.core :refer [*db*]]))

(conman/bind-connection *db*
                        "sql/log.sql")
