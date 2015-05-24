(ns ^{:doc "Tests for utils.clj"
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  cs.utils-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [cs.utils :refer :all]))

(def fixture-data
  [(sorted-map
    :isbn "978000000001"
    :search-result {
                    :title "Book A"
                    :author "Author A"
                    :img "http://www.example.com/A"
                    :store 0
                    :available true
                    :price 11.99
                   }
    )
   (sorted-map
     :isbn "978000000002"
     :search-result {
                     :title "Book B"
                     :author "Author B"
                     :img "http://www.example.com/B"
                     :store 0
                     :available false
                     :price 11.99
                    }
    )
   (sorted-map
     :isbn "978000000003"
     :search-result {
                     :title "Book C"
                     :author "Author C"
                     :img "http://www.example.com/C"
                     :store 0
                     :available true
                     :price 11.99
                   }
    )
   (sorted-map
     :isbn "978000000001"
     :search-result {
                     :title "Book A"
                     :author "Author A"
                     :img "http://www.example.com/A"
                     :store 1
                     :available false
                     :price 10.99
                    }
   )
   (sorted-map
     :isbn "978000000002"
     :search-result {
                     :title "Book B"
                     :author "Author B"
                     :img "http://www.example.com/B"
                     :store 1
                     :available true
                     :price 12.99
                    }
    )
  ]
)

(facts "About manipulating book data"
       (fact "It converts data from search results
             to books with search result fields")
       (pivot fixture-data) => {"978000000001"
                                [{:author "Author A"
                                  :available true
                                  :img "http://www.example.com/A"
                                  :price 11.99 :store 0 :title "Book A"}
                                 {:author "Author A"
                                  :available false
                                  :img "http://www.example.com/A"
                                  :price 10.99 :store 1 :title "Book A"}]
                                "978000000002"
                                [{:author "Author B"
                                  :available false
                                  :img "http://www.example.com/B"
                                  :price 11.99 :store 0 :title "Book B"}
                                 {:author "Author B"
                                  :available true
                                  :img "http://www.example.com/B"
                                  :price 12.99 :store 1 :title "Book B"}]
                                "978000000003"
                                [{:author "Author C"
                                  :available true
                                  :img "http://www.example.com/C"
                                  :price 11.99 :store 0 :title "Book C"}]})
