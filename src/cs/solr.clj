; @file Scrapes indie bookstore sites that leverage Apache Solr for search.
; @author Eric Weinstein <eric.q.weinstein@gmail.com>

(ns cs.solr
  (:require [clojure.string :as string]
            [net.cgrand.enlive-html :as html]))

(defn fetch-url [url]
  "Creates an Enlive HTML resource from the provided URL string."
  (html/html-resource (java.net.URL. url)))

(defn search [store query]
  "Generates a JSON payload from sites powered by Apache Solr."
  (let [url (fetch-url (str (:storeLink store) "/search/apachesolr_search/" query))
        title        (map html/text (html/select url [:.abaproduct-title :a]))
        author       (map #(string/trim %) (map html/text (html/select url [:.abaproduct-authors])))
        price        (map #(subs % 1) (map html/text (html/select url [:.abaproduct-price])))
        image        (map #(get-in % [:attrs :src]) (html/select url [:.abaproduct-image :img]))
        link         (map #(str (:storeLink store) %) (map #(get-in % [:attrs :href]) (html/select url [:.abaproduct-title :a])))
        availability (map #(string/trim (second (re-find #"Availability: ([a-zA-Z0-9 ]+)" %)))
                          (map html/text (html/select url [:.abaproduct-more-details])))]
    (map #(merge store {:title %1
                        :author %2
                        :price %3
                        :img %4
                        :bookLink %5
                        :availability %6}) title author price image link availability)))
