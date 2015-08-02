(ns ^{:doc "Tests for location.clj"
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  quixote.location-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [quixote.location :refer :all]))

(facts "About location"
       (fact "It knows which cities are nearest")
       (nearest 40.7 -74.1) => :nyc
       (nearest 42.1 -118.0) => :pdx
       (nearest 47.6 -122.9) => :sea)

(facts "About distance"
       (fact "It correctly computes Euclidean distance")
       (#'quixote.location/distance 40 45 70 100) => 30.4138126514911)
