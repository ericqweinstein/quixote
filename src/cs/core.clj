(ns ^{:doc "CityShelf's web server."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  cs.core
  (:require [clojure.java.io :as io]
            [liberator.core :refer [resource defresource]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.codec :as codec]
            [ring.util.response :as resp]
            [compojure.core :refer [defroutes routes GET ANY]]
            [compojure.route :as route]
            [compojure.handler]
            [cs.solr :as solr]
            [cs.site :as site]
            [cs.strand :as strand]
            [cs.filter :refer [update remove-unavailable]]
            [cs.views.index :as home]))

(defn mobile?
  "Determines whether a client is using a mobile device."
  [user-agent]
  (when (seq user-agent)
    (re-find #"(iPhone|iPod|Android|BlackBerry|Windows Phone)" user-agent)))

(defn config
  "Loads the configuration file."
  [filename]
  (with-open [r (io/reader filename)]
    (read (java.io.PushbackReader. r))))

(def store-data
  "Store data."
  (config "conf.clj"))

(defn scrape
  "Generates a JSON payload from the scraped URL."
  [store query]

  (cond
    (re-find #"strandbooks" (:storeLink store))
    (remove-unavailable (update (strand/search store query)))
    (re-find #"(culture|greenlight|word|stmarks)" (:storeLink store))
    (remove-unavailable (update (site/search store query)))
    :else
    (remove-unavailable (update (solr/search store query)))))

(defresource indie [store query]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (fn [_] (scrape store query)))

(defroutes cs-routes
  "CityShelf routes."
  ; Static assets.
  (route/files "/" {:root "resources/public"})

  (GET "/"
    {:keys [headers params body] :as request}
    (if (mobile? (get headers "user-agent"))
      ; The mobile web application...
      (home/index "CityShelf")
      ;... or the landing page for non-mobile devices.
      (resp/file-response "landing.html" {:root "resources/public"})))

  ; API routes.
  (apply routes
    (pmap #(GET (str "/api/stores/" (:id %) "/")
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
  (compojure.handler/api cs-routes))

(defn -main
  "Starts the web server."
  [& args]
  (let [port (Integer. (get (System/getenv) "PORT" "8080"))]
    (run-jetty #'handler {:port port})))
