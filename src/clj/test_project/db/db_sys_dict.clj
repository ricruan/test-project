(ns test-project.db.db-sys-dict
  (:require [conman.core :as conman]
            [test-project.db.core :refer [*db*]]))

(conman/bind-connection *db* 
                           "sql/sys_dict.sql")
