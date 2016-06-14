(ns ^{:doc "Tools for scraping different types
           of independent bookstore websites."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  quixote.site
  (:require [clojure.string :as string]
            [quixote.filter :refer [has?]]
            [quixote.utils :refer [fetch-url]]
            [net.cgrand.enlive-html :as html]))

(declare get-field structure)

(defmulti search
  "Generates a JSON payload from scraped sites,
  dispatching on the underlying search engine
  (e.g. /search/site/, /apachesolr_search/)."
  (fn [store query] (:type store)))

(defmethod search :site [store query]
  (let [url (fetch-url (str (:storeLink store) "/search/site/" query))
        title        (map html/text (html/select url [:.search-result :.title :a]))
        author       (map html/text (html/select url [:.search-result :.abaproduct-details :p]))
        price        (map #(subs % 1) (map html/text (html/select url [:.abaproduct-details :h3 :strong])))
        image        (map #(get-in % [:attrs :src]) (html/select url [:.abaproduct-image :img]))
        availability (map #(subs % 14)
                           (filter #(re-find #"Availability: ([a-zA-Z0-9 ]+)" %)
                                   (map html/text (html/select url [:.abaproduct-details :span]))))
        isbn         (map #(subs % 9)
                           (filter #(re-find #"ISBN-13: ([0-9 ]+)" %)
                                   (map html/text (html/select url [:.abaproduct-details :span]))))]

    (structure store isbn title author image availability price)))

(defmethod search :solr [store query]
  (let [url (fetch-url (str (:storeLink store) "/search/apachesolr_search/" query))
        title        (map html/text (html/select url [:.abaproduct-title :a]))
        author       (map #(string/trim %) (map html/text (html/select url [:.abaproduct-authors])))
        price        (map #(subs % 1) (map html/text (html/select url [:.abaproduct-price])))
        image        (map #(get-in % [:attrs :src]) (html/select url [:.abaproduct-image :img]))
        availability (map #(string/trim (second (re-find #"Availability: ([a-zA-Z0-9 ]+)" %)))
                           (map html/text (html/select url [:.abaproduct-more-details])))
        isbn         (map #(string/trim (second (re-find #"ISBN-13: ([0-9 ]+)" %)))
                           (map html/text (html/select url [:.abaproduct-more-details])))]

    (structure store isbn title author image availability price)))

(defmethod search :booksite [store query]
  (let [url (fetch-url (str (:storeLink store) "/search/?q=" query))
        title        (get-field "bookname" url)
        author       (get-field "author" url)
        price        (get-field "bookprice" url)
        image        (map #(get-in % [:attrs :src]) (html/select url [[:td (html/attr= :width "127")] :img]))
        availability (map html/text (html/select url [[:td (html/attr= :width "81")] :strong :font]))
        isbn         (get-field "sku" url)]

    (structure store isbn title author image availability price)))

(defmethod search :powells [store query]
  (let [url (fetch-url (str (:storeLink store) "/s?kw=" query "&class="))
        title        (map html/text (html/select url [:.book-title]))
        author       (map html/text (html/select url [:.book-info :cite]))
        price        (map #(re-find (re-pattern "\\d+\\.\\d{2}") %) (map html/text (html/select url [:.price])))
        image        (map #(get-in % [:attrs :src]) (html/select url [:.bookcover]))
        availability (map #(get-in % [:attrs :src]) (html/select url [:.add-to-cart-button]))
        isbn         (map #(re-find (re-pattern "\\d{13}") %) image)]

    (structure store isbn title author image availability price)))

(defn- get-field
  "Extracts the provided field from the provided URL."
  [field url]
  (map #(get-in % [:attrs :value]) (html/select url [:form (html/attr= :name field)])))

(defn- structure
  "Structures the scraped data into a usable map."
  [store isbn title author image availability price]
  (map #(merge (sorted-map) {:isbn %1
                             :search-result {
                                             :title %2
                                             :author %3
                                             :img %4
                                             :store (:id store)
                                             :available (has? %5)
                                             :price %6
                                            }
                   }) isbn title author image availability price))
