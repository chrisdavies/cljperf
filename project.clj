(defproject cljperf "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-beta4"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [org.postgresql/postgresql "42.1.4.jre7"]
                 [com.layerware/hugsql "0.4.8"]
                 [ring "1.6.3"]
                 [bidi "2.1.2"]
                 [cheshire "5.8.0"]
                 [metosin/reitit "0.1.0-SNAPSHOT"]
                 [cljs-router "0.1.1"]
                 [metosin/jsonista "0.1.0-SNAPSHOT"]
                 [com.rpl/specter "1.0.4"]
                 [migratus "1.0.0"]
                 [environ "1.1.0"]
                 [hikari-cp "1.8.1"]
                 [camel-snake-kebab "0.4.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [org.clojure/core.async "0.3.443"]
                 [timbre-ns-pattern-level "0.1.2"]
                 [com.fzakaria/slf4j-timbre "0.3.7"]]
  :plugins [[lein-ring "0.12.1"]]
  :ring {:handler cljperf.core/app})
