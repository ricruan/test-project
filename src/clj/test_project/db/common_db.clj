(ns test-project.db.common-db
  (:require [conman.core :as conman]
            [test-project.db.core :refer [*db*]]))

(conman/bind-connection *db*
                         "sql/common_utils.sql")
