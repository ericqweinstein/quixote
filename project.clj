(defproject quixote "1.0.0"
  :description "Quixote: the search service for http://www.cityshelf.com/"
  :url "https://github.com/ericqweinstein/quixote"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [liberator "0.13"]
                 [compojure "1.4.0"]
                 [cheshire "5.5.0"]
                 [clj-http "2.0.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring-cors "0.1.7"]
                 [enlive "1.1.6"]
                 [hiccup "1.0.5"]]
  :main ^:skip-aot quixote.core
  :target-path "target/%s"
  :profiles {:dev {
                   :plugins [[lein-midje "3.1.1"]
                             [codox "0.8.10"]
                             [lein-ancient "0.5.5"]
                             [lein-kibit "0.0.8"]
                             [lein-bikeshed "0.1.8"]]
                   :dependencies [[midje "1.7.0"]]}
             :uberjar {:aot :all}}
  :aliases {"lint" ^{:doc "Lint and test all the things"}
            ["do" "ancient," "kibit," ["bikeshed", "-m135", "-v"]]}
  :min-lein-version "2.0.0")
