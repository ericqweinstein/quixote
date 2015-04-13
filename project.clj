; @file Project definition file.
; @author Eric Weinstein <eric.q.weinstein@gmail.com>

(defproject cityshelf "0.1.0"
  :description "CityShelf: A search aggregator for independent bookstores."
  :url "http://www.cityshelf.com"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [liberator "0.12.2"]
                 [compojure "1.3.3"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [enlive "1.1.5"]
                 [hiccup "1.0.5"]]
  :main ^:skip-aot cs.core
  :target-path "target/%s"
  :profiles {:dev {
                   :plugins [[lein-midje "3.1.1"]
                             [codox "0.8.10"]
                             [lein-ancient "0.5.5"]
                             [lein-kibit "0.0.8"]
                             [lein-bikeshed "0.1.8"]]
                   :dependencies [[midje "1.6.3"]]}
             :uberjar {:aot :all}}
  :aliases {"lint" ^{:doc "Lint and test all the things"}
            ["do" "ancient," "kibit," ["bikeshed", "-m135", "-v"]]}
  :min-lein-version "2.0.0"
  :java-agents [[com.newrelic.agent.java/newrelic-agent "3.11.0"]])
