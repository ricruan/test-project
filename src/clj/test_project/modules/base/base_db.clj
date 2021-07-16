(ns test-project.modules.base.base-db
  (:require [conman.core :as conman]
            [test-project.db.core :refer [*db*]]))

(conman/bind-connection *db* 
                           "sql/company.sql")
