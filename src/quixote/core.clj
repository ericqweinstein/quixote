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
            [quixote.filter :refer [remove-unavailable update-metadata]]
            [quixote.location :as location]
            [quixote.old-site :as old-site]
            [quixote.site :as site]
            [quixote.utils :refer [pivot]]
            [quixote.views.index :as home]))

;; DEPRECATED: To be moved to separate client codebase.
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

(defn new-scrape
  "Generates a JSON payload from the scraped URL."
  [store query]
  (site/search store query))

;; DEPRECATED: API v1 method.
(defn scrape
  "Generates a JSON payload from the scraped URL."
  [store query]
  (remove-unavailable (update-metadata (old-site/search store query))))

;; DEPRECATED: API v1 resource.
(defresource indie [store query]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (fn [_] (scrape store query)))

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
                     data (flatten (map #(new-scrape % query)
                            (filter #(= city (:city %)) stores)))]
                  (vector (pivot data)))))

(defroutes quixote-routes
  "Quixote routes."
  ;; DEPRECATED: To be moved to separate client codebase.
  (route/files "/" {:root "resources/public"})

  ;; DEPRECATED: To be moved to separate client codebase.
  (GET "/"
    {:keys [headers params body] :as request}
    (if (mobile? (get headers "user-agent"))
      ;; The mobile web application...
      (home/index "CityShelf")
      ;;... or the landing page for non-mobile devices.
      (resp/file-response "landing.html" {:root "resources/public"})))

  ;; Handles subscribing users to the e-mail list via MailChimp.
  ;; When we separate the API, landing page, and mobile website,
  ;; this route will be moved to the landing page app. (EW 26 Apr 2015)
  ;; DEPRECATED: To be moved to separate client codebase.
  (POST "/subscribe"
    {:keys [headers params body] :as request}
     (let [api-req {:apikey "49f5b85310aff273db3b5a8bf497b524-us9"
                    :id "c2f178f901"
                    :email {:email (:email params)}}
           api-rsp (client/post "https://us9.api.mailchimp.com/2.0/lists/subscribe.json"
                                {:body (json/generate-string api-req)
                                 :content-type :json
                                 :accept :json})]))

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

  ;; DEPRECATED: API v1 routes.
  (apply routes
    (pmap #(GET (str "/api/stores/" (:id %) "/")
               {params :query-params}
               (indie % (codec/url-encode (get params "query"))))
         store-data))

  ;; Angular handles all routing, including 404s. If a 404
  ;; gets to Clojure, it's a legitimate route that Angular
  ;; should handle, but Clojure received (e.g. as a result
  ;; of a page refresh).
  ;; DEPRECATED: To be moved to separate client codebase.
  (route/not-found (home/index "CityShelf")))

(def handler
  "Handler helper function."
  (compojure.handler/api
    (wrap-cors quixote-routes :access-control-allow-origin [#".*"]
                              :access-control-allow-methods [:get])))

(defn -main
  "Starts the web server."
  [& args]
  (let [port (Integer. (get (System/getenv) "PORT" "8080"))]
    (run-jetty #'handler {:port port})))