(ns ^{:doc "Quixote's web server."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  quixote.core
  (:require [clojure.java.io :as io]
            [liberator.core :refer [resource defresource]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.util.codec :as codec]
            [ring.util.response :as resp]
            [compojure.core :refer [defroutes routes GET POST ANY]]
            [compojure.route :as route]
            [compojure.handler]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [quixote.location :as location]
            [quixote.site :as site]
            [quixote.utils :refer [pivot]]))

(defn config
  "Loads the configuration file."
  [filename]
  (with-open [r (io/reader filename)]
    (read (java.io.PushbackReader. r))))

(def store-data
  "Store data."
  (config "config/stores.clj"))

(def service-data
  "Service data."
  (config "config/service.clj"))

(defn scrape
  "Generates a JSON payload from the scraped URL."
  [store query]
  (site/search store query))

(defresource stores [latitude longitude]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (fn [_]
               (let [city (location/nearest
                            (Float/parseFloat latitude)
                            (Float/parseFloat longitude))]
                 (filter #(= city (:city %)) store-data))))

(defresource indies [stores query latitude longitude]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (fn [_]
               (let [city (location/nearest
                            (Float/parseFloat latitude)
                            (Float/parseFloat longitude))
                     data (flatten (map #(scrape % query)
                            (filter #(= city (:city %)) stores)))]
                  (vector (pivot data)))))

(defroutes quixote-routes
  "Quixote routes."

  (GET "/"
    []
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string service-data)})

  (GET "/books/"
    {params :query-params}
       (let [latitude  (get params "latitude")
             longitude (get params "longitude")
             book      (get params "query")]
         (indies store-data (codec/url-encode book)
                            (codec/url-encode latitude)
                            (codec/url-encode longitude))))

  (GET "/stores/"
       {params :query-params}
       (let [latitude  (get params "latitude")
             longitude (get params "longitude")]
         (stores (codec/url-encode latitude)
                 (codec/url-encode longitude))))

  (route/not-found
    {:status 404
     :body "Not found"}))

(def handler
  "API handler."
  (compojure.handler/api
    (wrap-cors quixote-routes :access-control-allow-origin [#".*"]
                              :access-control-allow-methods [:get])))

(defn -main
  "Starts the web server."
  [& args]
  (let [port (Integer. (get (System/getenv) "PORT" "8080"))]
    (run-jetty #'handler {:port port})))
