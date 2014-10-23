; @file Tests for CityShelf's API filters.
; @author Eric Weinstein <eric.q.weinstein@gmail.com>

(ns cs.filter-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [cs.filter :refer :all]))

(facts "About book availability"
  (fact "It knows when a book is available")
    (#'cs.filter/available? {:title "Mother Night" :author "Kurt Vonnegut" :availability "On shelves now!"}) => true
    (#'cs.filter/available? {:title "Battlefield Earth" :author "L. Ron Hubbard" :availability "Unavailable"}) => false

  (fact "It normalizes availability text")
   (#'cs.filter/normalize "On Our Shelves Now") => "On shelves now!"
   (#'cs.filter/normalize "In the Warehouse Now") => "Not in store"
   (#'cs.filter/normalize "Not on hand") => "Unavailable"
   (#'cs.filter/normalize "Not in stock") => "Unavailable"
   (#'cs.filter/normalize "Not currently on our shelves") => "Unavailable"
   (#'cs.filter/normalize "Not Currently In Stock") => "Unavailable"

  (fact "It updates book availability")
    (update [{:title "Hamlet" :availability "On Our Shelves Now"}]) => [{:title "Hamlet" :availability "On shelves now!"}]
    (update [{:title "Omon Ra" :availability "In the Warehouse Now"}]) => [{:title "Omon Ra" :availability "Not in store"}]
    (update [{:title "Ulysses" :availability "Not on hand"}]) => [{:title "Ulysses" :availability "Unavailable"}]

  (fact "It removes search results when the book is unavailable")
    (let [test-data [{:title "Mother Night" :availability "Unavailable"}
                     {:title "Hamlet" :availability "On shelves now!"}
                     {:title "Omon Ra" :availability "Not in store"}
                     {:title "Ulysses" :availability "Unavailable"}]]
      (remove-unavailable test-data) => [{:title "Hamlet" :availability "On shelves now!"}]))
