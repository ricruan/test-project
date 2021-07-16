(ns test-project.modules.queries.queries-db
  (:require [conman.core :as conman]
            [test-project.db.core :refer [*db*]]))

(conman/bind-connection *db*
                        "sql/queries.sql")
