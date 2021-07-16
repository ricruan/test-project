(defproject test-project "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :java-source-paths ["src/java"]
  :dependencies [[ch.qos.logback/logback-classic "1.2.3"]
                 [cheshire "5.8.1"]
                 [clojure.java-time "0.3.2"]
                 [com.google.protobuf/protobuf-java "3.8.0"]
                 [conman "0.8.3"]
                 [cprop "0.1.14"]
                 [expound "0.7.2"]
                 [funcool/struct "1.4.0"]
                 [luminus-immutant "0.2.5"]
                 [luminus-jetty "0.1.7"]
                 [luminus-migrations "0.6.5"]
                 [luminus-transit "0.1.1"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.10.0"]
                 [metosin/muuntaja "0.6.4"]
                 [metosin/reitit "0.3.9"]
                 [metosin/ring-http-response "0.9.1"]
                 [mount "0.1.16"]
                 [mysql/mysql-connector-java "8.0.16"]
                 [nrepl "0.6.0"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.5.0"]
                 [org.webjars.npm/bulma "0.7.5"]
                 [org.webjars.npm/material-icons "0.3.0"]
                 [org.webjars/webjars-locator "0.36"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-cors "0.1.13"]
                 [selmer "1.12.14"]
                 [clj-jwt "0.1.1"]
                 [com.rpl/specter "1.1.2"]
                 [com.googlecode.log4jdbc/log4jdbc "1.2"]
                 [clj-http "3.9.1"]
                 [com.taoensso/carmine "2.19.1"]
                 [org.clojure/data.json "0.2.6"]
                 [clj.qiniu "0.2.1"]
                 [com.littlenb/snowflake "1.0.4"]
                 [org.clojure/tools.logging "0.4.1"]
                 [buddy/buddy-auth "2.2.0"]
                 [buddy/buddy-core "1.6.0"]
                 [metosin/reitit-http "0.3.9"]
                 [metosin/reitit-sieppari "0.3.9"]
				 [org.clojure/data.xml "0.0.8"]]

  :min-lein-version "2.0.0"
  
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main ^:skip-aot test-project.core

  :plugins [[lein-cloverage "1.1.2"]]
  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    [:cljsbuild :builds :app :compiler :output-to]]
  
  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile"]
             :aot :all
             :uberjar-name "test-project.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}
   
   :uberjar-test  {:omit-source    true
                   :aot            :all
                   :uberjar-name   "test-project.jar"
                   :source-paths   ["env/test/clj"]
                   :resource-paths ["env/test/resources"]
                   :jvm-opts       ["-Dconf=test-config.edn"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[binaryage/devtools "0.9.10"]
                                 [cider/piggieback "0.4.1"]
                                 [pjstadig/humane-test-output "0.9.0"]
                                 [prone "2019-07-08"]
                                 [re-frisk "0.5.4.1"]
                                 [ring/ring-devel "1.7.1"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]]
                  
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}
   
  :project/prod {:jvm-opts ["-Dconf=prod-config.edn"]
                  :resource-paths ["env/prod/resources"]}
   :profiles/dev {}
   :profiles/test {}})
