; @file Tests for CityShelf's API filters.
; @author Eric Weinstein <eric.q.weinstein@gmail.com>

(ns cs.filter-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [cs.filter :refer :all]))

(facts "About book availability"
  (fact "It knows when a book is available")
    (available? {:title "Mother Night" :author "Kurt Vonnegut" :availability "On Shelves Now!"}) => true
    (available? {:title "Battlefield Earth" :author "L. Ron Hubbard" :availability "Unavailable"}) => false

  (fact "It normalizes availability text")
   (normalize "On Our Shelves Now") => "On shelves now!"
   (normalize "In the Warehouse Now") => "Not in store"
   (normalize "Not on hand") => "Unavailable"
   (normalize "Not in stock") => "Unavailable"
   (normalize "Not currently on our shelves") => "Unavailable"
   (normalize "Not Currently In Stock") => "Unavailable"

  (fact "It updates book availability")
    (update [{:title "Hamlet" :availability "On Our Shelves Now"}]) => [{:title "Hamlet" :availability "On shelves now!"}]
    (update [{:title "Omon Ra" :availability "In the Warehouse Now"}]) => [{:title "Omon Ra" :availability "Not in store"}]
    (update [{:title "Ulysses" :availability "Not on hand"}]) => [{:title "Ulysses" :availability "Unavailable"}]

  (fact "It removes search results when the book is unavailable")
    (let [test-data [{:title "Mother Night" :availability "Unavailable"}
                     {:title "Hamlet" :availability "On shelves now!"}
                     {:title "Omon Ra" :availability "Not in store"}
                     {:title "Ulysses" :availability "Unavailable"}]]
      (remove-unavailable test-data) => [{:title "Hamlet" :availability "On shelves now!"}
                                         {:title "Omon Ra" :availability "Not in store"}]))
