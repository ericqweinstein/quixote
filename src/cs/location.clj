(ns ^{:doc "Geolocation services."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  cs.location
  (:require [clojure.math.numeric-tower :as math]))

(declare square distance)

(def cities
  {:nyc {:latitude 40.7127 :longitude -74.0059}
   :chi {:latitude 41.8369 :longitude -87.6847}
   :msp {:latitude 44.9778 :longitude -93.2650}
   :sea {:latitude 47.6097 :longitude -122.3331}
   :bos {:latitude 42.3601 :longitude -71.0589}
   :pdx {:latitude 45.5200 :longitude -122.6819}})

(defn nearest
    "Returns the label for the point nearest
    the provided latitude and longitude."
    [latitude longitude]
    (key (apply min-key val (zipmap (keys cities)
                                    (map #(distance latitude %1 longitude %2)
                                         (map :latitude  (vals cities))
                                         (map :longitude (vals cities)))))))

(defn- distance
    "Calculates Euclidean distance between two points."
    [x1 x2 y1 y2]
    (math/sqrt (+
                 (square  (- x1 x2))
                 (square  (- y1 y2)))))

(defn- square
  "Squares the provided number."
  [n]
  (* n n))
