(ns ^{:doc "Scrapes indie bookstores that leverage BookSite."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  cs.booksite
  (:require [clojure.string :as string]
            [cs.utils :refer [fetch-url]]
            [net.cgrand.enlive-html :as html]))

(declare get-field)

(defn search
  "Generates a JSON payload from sites powered by BookSite."
  [store query]
  (let [url (fetch-url (str (:storeLink store) "/search/?q=" query))
        title        (get-field "bookname" url)
        author       (get-field "author" url)
        price        (get-field "bookprice" url)
        image        (pmap #(get-in % [:attrs :src]) (html/select url [[:td (html/attr= :width "127")] :img]))
        link         (pmap #(str (:storeLink store) %) (map #(get-in % [:attrs :href]) (html/select url [:td :a])))
        availability (pmap html/text (html/select url [[:td (html/attr= :width "81")] :strong :font]))
        isbn         (get-field "sku" url)]
    (map #(merge store {:title %1
                        :author %2
                        :price %3
                        :img %4
                        :bookLink %5
                        :availability %6
                        :isbn %7
                        }) title author price image link availability isbn)))

(defn- get-field
  "Extracts the provided field from the provided URL."
  [field url]
  (pmap #(get-in % [:attrs :value]) (html/select url [:form (html/attr= :name field)])))
