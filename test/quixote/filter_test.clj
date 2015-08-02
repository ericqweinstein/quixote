(ns ^{:doc "Tests for filter.clj"
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  quixote.filter-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [quixote.filter :refer :all]))

(facts "About book availability"
  (fact "It knows when a book is available")
    (#'quixote.filter/available? {:title "Mother Night" :author "Kurt Vonnegut" :availability "On shelves now"}) => true
    (#'quixote.filter/available? {:title "Battlefield Earth" :author "L. Ron Hubbard" :availability "Unavailable"}) => false

  (fact "It normalizes availability text")
   (#'quixote.filter/normalize "On Our Shelves Now") => "On shelves now"
   (#'quixote.filter/normalize "In the Warehouse Now") => "Out of stock"
   (#'quixote.filter/normalize "Not on hand") => "Unavailable"
   (#'quixote.filter/normalize "Not in stock") => "Unavailable"
   (#'quixote.filter/normalize "Not currently on our shelves") => "Unavailable"
   (#'quixote.filter/normalize "Not Currently In Stock") => "Unavailable"

  (fact "It updates book availability")
    (update [{:title "Hamlet" :availability "On Our Shelves Now"}]) => [{:title "Hamlet" :availability "On shelves now"}]
    (update [{:title "Omon Ra" :availability "In the Warehouse Now"}]) => [{:title "Omon Ra" :availability "Out of stock"}]
    (update [{:title "Ulysses" :availability "Not on hand"}]) => [{:title "Ulysses" :availability "Unavailable"}]

  (fact "It removes search results when the book is unavailable")
    (let [test-data [{:title "Mother Night" :availability "Unavailable"}
                     {:title "Hamlet" :availability "On shelves now"}
                     {:title "Omon Ra" :availability "Out of stock"}
                     {:title "Ulysses" :availability "Unavailable"}]]
      (remove-unavailable test-data) => [{:title "Hamlet" :availability "On shelves now"}
                                         {:availability "Out of stock", :title "Omon Ra"}]))
