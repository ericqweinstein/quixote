(ns ^{:doc "Scrapes indie bookstore sites that
           leverage Solr /search/site."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  cs.site
  (:require [clojure.string :as string]
            [net.cgrand.enlive-html :as html]))

(declare fetch-url)

(defn search
  "Generates a JSON payload from sites powered by Solr /search/site."
  [store query]
  (let [url (fetch-url (str (:storeLink store) "/search/site/" query))
        title        (pmap html/text (html/select url [:.search-result :.title :a]))
        author       (pmap html/text (html/select url [:.search-result :.abaproduct-details :p]))
        price        (pmap #(subs % 1) (map html/text (html/select url [:.abaproduct-details :h3 :strong])))
        image        (pmap #(get-in % [:attrs :src]) (html/select url [:.abaproduct-image :img]))
        link         (pmap #(str (:storeLink store) %) (map #(get-in % [:attrs :href]) (html/select url [:.search-result :.title :a])))
        availability (pmap #(subs % 14)
                           (filter #(re-find #"Availability: ([a-zA-Z0-9 ]+)" %)
                                   (map html/text (html/select url [:.abaproduct-details :span]))))
        isbn         (pmap #(subs % 9)
                           (filter #(re-find #"ISBN-13: ([0-9 ]+)" %)
                                   (map html/text (html/select url [:.abaproduct-details :span]))))]
    (map #(merge store {:title %1
                        :author %2
                        :price %3
                        :img %4
                        :bookLink %5
                        :availability %6
                        :isbn %7
                        }) title author price image link availability isbn)))

(defn- fetch-url
  "Creates an Enlive HTML resource from the provided URL string."
  [url]
  (html/html-resource (java.net.URL. url)))
