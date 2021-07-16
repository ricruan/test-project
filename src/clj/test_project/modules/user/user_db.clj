(ns test-project.modules.user.user-db
  (:require [conman.core :as conman]
            [test-project.db.core :refer [*db*]]))

(conman/bind-connection *db*
                        "sql/user.sql")
