(ns test-project.modules.app.app-db
  (:require [conman.core :as conman]
            [test-project.db.core :refer [*db*]]))

(conman/bind-connection *db*
                        "sql/app.sql")
