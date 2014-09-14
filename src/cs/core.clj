; @file CityShelf's web server.
; @author Eric Weinstein <eric.q.weinstein@gmail.com>
; @copyright CityShelf, 2014

(ns cs.core
  (:require [clojure.java.io :as io]
            [liberator.core :refer [resource defresource]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.codec :as codec]
            [compojure.core :refer [defroutes routes GET ANY]]
            [compojure.route :as route]
            [compojure.handler]
            [cs.solr :as solr]
            [cs.strand :as strand]
            [cs.filter :refer [update remove-unavailable]]
            [cs.views.index :as home]))

(defn config
  "Loads the configuration file."
  [filename]
  (with-open [r (io/reader filename)]
    (read (java.io.PushbackReader. r))))

(def store-data
  (config "conf.clj"))

(defn scrape
  "Generates a JSON payload from the scraped URL."
  [store query]

  ; The Strand is a special snowflake; the rest of our bookstores
  ; all use the same ABA-based Apache Solr search solution. (EW 5 Sep 2014)
  (if (re-find #"strandbooks" (:storeLink store))
    (remove-unavailable (update (strand/search store query)))
    (remove-unavailable (update (solr/search store query)))))

(defresource indie [store query]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (fn [_] (scrape store query)))

(defroutes cs-routes
  ; Static assets.
  (route/files "/" {:root "resources/public"})

  ; The home page.
  (GET "/" [] (home/index "CityShelf"))

  ; API routes.
  (apply routes
    (map #(GET (str "/api/stores/" (:id %) "/")
               {params :query-params}
               (indie % (codec/url-encode (get params "query"))))
         store-data))

  ; Angular handles all routing, including 404s. If a 404
  ; gets to Clojure, it's a legitimate route that Angular
  ; should handle, but Clojure received (e.g. as a result
  ; of a page refresh).
  (route/not-found (home/index "CityShelf")))

(def handler
  "Handler helper function."
  (-> cs-routes compojure.handler/api))

(defn -main
  "Starts the web server."
  [& args]
  (let [port (Integer. (get (System/getenv) "PORT" "8080"))]
    (run-jetty #'handler {:port port})))
