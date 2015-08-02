(ns ^{:doc "Quixote utility functions."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  quixote.utils
  (:require [net.cgrand.enlive-html :as html]))

(declare mapify update-values)

(defn fetch-url
  "Creates an Enlive HTML resource from the provided URL string."
  [url]
  (html/html-resource (java.net.URL. url)))

(defn pivot
  "Converts a list of search results into a de-duped list
  of books with the availability at each store as a field
  on each book."
  [d]
  (update-values
    (apply merge-with concat
      (vec (map #(apply hash-map %) (map vals d))))
    mapify))

(defn- update-values
  "Applies the function f to the map m, thereby
  updating all its values.
  See: http://blog.jayfields.com/2011/08/
  clojure-apply-function-to-each-value-of.html"
  [m f & args]
  (reduce
    (fn [r [k v]]
      (assoc r k (apply f v args))) {} m))

(defn- mapify
  "Converts a list of form (:a foo :b bar :c baz
                            :a quux :b do :c re)
  into a vector of maps of form [{:a foo :b bar :c baz}
                                 {:a quux :b do :c re}]"
  [coll]
  (if (instance? clojure.lang.LazySeq coll)
    (vec (map #(apply hash-map %)
      (partition 12 (flatten coll))))
    (vector coll)))
