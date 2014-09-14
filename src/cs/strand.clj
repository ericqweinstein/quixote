; @file Scrapes The Strand.
; @author Eric Weinstein <eric.q.weinstein@gmail.com>

(ns cs.strand
  (:require [clojure.string :as string]
            [net.cgrand.enlive-html :as html]))

(defn fetch-url [url]
  "Creates an Enlive HTML resource from the provided URL string."
  (html/html-resource (java.net.URL. url)))

; The Strand doesn't provide availability on the search page,
; so we have to check each product detail page individually. (EW 5 Sep 2014)
(defn get-availability [link]
  "Visits a product detail page from the store search and retrieves the availability."
  (map #(string/trim %) (map html/text (html/select (fetch-url link) [:.odd :.column-location]))))

(defn search [store query]
  "Generates a JSON payload for The Strand."
  (let [url (fetch-url (str (:storeLink store) "/index.cfm?fuseaction=search.results&includeOutOfStock=0&searchString=" query))
        title        (map #(string/trim %) (map html/text (html/select url [:.info :h3 :a])))
        author       (map html/text (html/select url [:.product-author :a]))
        price        (map #(subs % 1) (map string/trim (map html/text (html/select url [:.price :span]))))
        image        (map #(get-in % [:attrs :src]) (html/select url [:.image :img]))
        link         (map #(get-in % [:attrs :href]) (html/select url [:.info :h3 :a]))
        availability (map #(first %) (map #(get-availability %) link))]
    (map #(merge store {:title %1
                        :author %2
                        :price %3
                        :img %4
                        :bookLink %5
                        :availability %6
                        }) title author price image link availability)))
