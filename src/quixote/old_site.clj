(ns ^{:doc "Tools for scraping different types
           of independent bookstore websites."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  quixote.old-site
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


    (structure store isbn title author price image link availability)))

(defmethod search :solr [store query]
  (let [url (fetch-url (str (:storeLink store) "/search/apachesolr_search/" query))
        title        (pmap html/text (html/select url [:.abaproduct-title :a]))
        author       (pmap #(string/trim %) (map html/text (html/select url [:.abaproduct-authors])))
        price        (pmap #(subs % 1) (map html/text (html/select url [:.abaproduct-price])))
        image        (pmap #(get-in % [:attrs :src]) (html/select url [:.abaproduct-image :img]))
        link         (pmap #(str (:storeLink store) %) (map #(get-in % [:attrs :href]) (html/select url [:.abaproduct-title :a])))
        availability (pmap #(string/trim (second (re-find #"Availability: ([a-zA-Z0-9 ]+)" %)))
                           (map html/text (html/select url [:.abaproduct-more-details])))
        isbn         (pmap #(string/trim (second (re-find #"ISBN-13: ([0-9 ]+)" %)))
                           (map html/text (html/select url [:.abaproduct-more-details])))]


    (structure store isbn title author price image link availability)))

(defmethod search :booksite [store query]
  (let [url (fetch-url (str (:storeLink store) "/search/?q=" query))
        title        (get-field "bookname" url)
        author       (get-field "author" url)
        price        (get-field "bookprice" url)
        image        (pmap #(get-in % [:attrs :src]) (html/select url [[:td (html/attr= :width "127")] :img]))
        link         (pmap #(str (:storeLink store) %) (map #(get-in % [:attrs :href]) (html/select url [:td :a])))
        availability (pmap html/text (html/select url [[:td (html/attr= :width "81")] :strong :font]))
        isbn         (get-field "sku" url)]

    (structure store isbn title author price image link availability)))

(defmethod search :powells [store query]
  (let [url (fetch-url (str (:storeLink store) "/s?kw=" query "&class="))
        title        (pmap html/text (html/select url [:.book-title]))
        author       (pmap html/text (html/select url [:.book-info :cite]))
        price        (pmap #(re-find (re-pattern "\\d+\\.\\d{2}") %) (map html/text (html/select url [:.price])))
        image        (pmap #(get-in % [:attrs :src]) (html/select url [:.bookcover]))
        link         (pmap #(str (:storeLink store) %) (map #(get-in % [:attrs :href]) (html/select url [:.book-title :a])))
        availability (pmap #(get-in % [:attrs :src]) (html/select url [:.add-to-cart-button]))
        isbn         (pmap #(re-find (re-pattern "\\d{13}") %) image)]

    (structure store isbn title author price image link availability)))

(defn- get-field
  "Extracts the provided field from the provided URL."
  [field url]
  (pmap #(get-in % [:attrs :value]) (html/select url [:form (html/attr= :name field)])))

(defn- structure
  "Old API."
  [store isbn title author price image link availability]
  (map #(merge store {:title %1
                      :author %2
                      :price %3
                      :img %4
                      :bookLink %5
                      :availability %6
                      :isbn %7}) title author price image link availability isbn))
