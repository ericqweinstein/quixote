(ns ^{:doc "Tests for the core.clj"
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  cs.core-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [cs.core :refer :all]))

(facts "About CityShelf"
 (fact "It detects mobile devices")
  (mobile? "iPhone") => ["iPhone" "iPhone"]
  (mobile? "iPod") => ["iPod" "iPod"]
  (mobile? "Android") => ["Android" "Android"]
  (mobile? "BlackBerry") => ["BlackBerry" "BlackBerry"]
  (mobile? "Windows Phone") => ["Windows Phone" "Windows Phone"]
  (mobile? "Macintosh") => nil)
