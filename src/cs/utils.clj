(ns ^{:doc "CityShelf utility functions."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  cs.utils
  (:require [net.cgrand.enlive-html :as html]))

(declare mapify update-values)

;; TODO: Remove as soon as testing is complete. (EW 21 May 2015)
(def data
  [
   {:isbn "978000000001"
    :availability {
                   :store "A"
                   :available true
                   :price 11.99
                   }
    }
   {:isbn "978000000002"
    :availability {
                   :store "A"
                   :available true
                   :price 11.99
                   }
    }
   {:isbn "978000000003"
    :availability {
                   :store "A"
                   :available true
                   :price 11.99
                   }
    }
   {:isbn "978000000001"
    :availability {
                   :store "B"
                   :available true
                   :price 10.99
                   }
    }
   {:isbn "978000000002"
    :availability {
                   :store "B"
                   :available true
                   :price 12.99
                   }
    }
   {:isbn "978000000003"
    :availability {
                   :store "B"
                   :available true
                   :price 13.99
                   }
    }
  ]
)

(defn fetch-url
  "Creates an Enlive HTML resource from the provided URL string."
  [url]
  (html/html-resource (java.net.URL. url)))

(defn munge!
  "Converts a list of search results into a de-duped list
  of books with the availability at each store as a field
  on each book."
  [d]
  (update-values
    (apply merge-with concat
      (into [] (map #(apply hash-map %) (map reverse (map vals d)))))
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
  (into []
        (map #(apply hash-map %)
             (partition 6 (flatten coll)))))

