(ns ^{:doc "Tests for filter.clj"
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  quixote.filter-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [quixote.filter :refer :all]))

(facts "About book availability"
  (fact "It knows when a book is available")
    (#'quixote.filter/has? "In stock") => true
    (#'quixote.filter/has? "Not currently on shelves") => false

  (fact "It normalizes availability text")
   (#'quixote.filter/normalize "On Our Shelves Now") => "On shelves now"
   (#'quixote.filter/normalize "In the Warehouse Now") => "Out of stock"
   (#'quixote.filter/normalize "Not on hand") => "Unavailable"
   (#'quixote.filter/normalize "Not in stock") => "Unavailable"
   (#'quixote.filter/normalize "Not currently on our shelves") => "Unavailable"
   (#'quixote.filter/normalize "Not Currently In Stock") => "Unavailable")
