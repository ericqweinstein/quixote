; @file Scrapes indie bookstore sites that leverage Solr /search/apachesolr_search.
; @author Eric Weinstein <eric.q.weinstein@gmail.com>

(ns cs.solr
  (:require [clojure.string :as string]
            [net.cgrand.enlive-html :as html]))

(defn fetch-url
  "Creates an Enlive HTML resource from the provided URL string."
  [url]
  (html/html-resource (java.net.URL. url)))

(defn search
  "Generates a JSON payload from sites powered by Solr /search/apachesolr_search."
  [store query]
  (let [url (fetch-url (str (:storeLink store) "/search/apachesolr_search/" query))
        title        (pmap html/text (html/select url [:.abaproduct-title :a]))
        author       (pmap #(string/trim %) (map html/text (html/select url [:.abaproduct-authors])))
        price        (pmap #(subs % 1) (map html/text (html/select url [:.abaproduct-price])))
        image        (pmap #(get-in % [:attrs :src]) (html/select url [:.abaproduct-image :img]))
        link         (pmap #(str (:storeLink store) %) (map #(get-in % [:attrs :href]) (html/select url [:.abaproduct-title :a])))
        availability (pmap #(string/trim (second (re-find #"Availability: ([a-zA-Z0-9 ]+)" %)))
                           (map html/text (html/select url [:.abaproduct-more-details])))]
    (map #(merge store {:title %1
                        :author %2
                        :price %3
                        :img %4
                        :bookLink %5
                        :availability %6}) title author price image link availability)))
