(ns test-project.modules.sys.sys-user-db
  (:require [conman.core :as conman]
            [test-project.db.core :refer [*db*]]))

(conman/bind-connection *db* 
                        "sql/sys_user.sql"
                        "sql/sys_role.sql"
                        "sql/sys_menu.sql")
