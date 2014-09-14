; @file Tests for CityShelf's web server.
; @author Eric Weinstein <eric.q.weinstein@gmail.com>

(ns cs.core-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [cs.core :refer :all]))

(facts "About CityShelf"
 (fact "It has an example test")
  (+ 1 1) => 2)
