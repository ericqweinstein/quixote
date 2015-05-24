(ns ^{:doc "Tests for utils.clj"
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  cs.utils-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [cs.utils :refer :all]))

(def fixture-data
  [
   {:isbn "978000000001"
    :search-result {
                    :title "Book A"
                    :author "Author A"
                    :img "http://www.example.com/A"
                    :store 0
                    :available true
                    :price 11.99
                   }
    }
   {:isbn "978000000002"
    :search-result {
                    :title "Book B"
                    :author "Author B"
                    :img "http://www.example.com/B"
                    :store 0
                    :available false
                    :price 11.99
                   }
    }
   {:isbn "978000000003"
    :search-result {
                    :title "Book C"
                    :author "Author C"
                    :img "http://www.example.com/C"
                    :store 0
                    :available true
                    :price 11.99
                   }
    }
   {:isbn "978000000001"
    :search-result {
                    :title "Book A"
                    :author "Author A"
                    :img "http://www.example.com/A"
                    :store 1
                    :available false
                    :price 10.99
                   }
    }
   {:isbn "978000000002"
    :search-result {
                    :title "Book B"
                    :author "Author B"
                    :img "http://www.example.com/B"
                    :store 1
                    :available true
                    :price 12.99
                   }
    }
  ]
)

(facts "About manipulating book data"
       (fact "It converts data from search results
             to books with search result fields")
       ;; TODO: Figure out why Midje reverses the data structure here.
       ;; The keys/values are swapped in the test, but are delivered
       ;; correctly by the API. (EW 23 May 2015)
       (pivot fixture-data) => {
                                {:store 1
                                 :title "Book A"
                                 :author "Author A"
                                 :available false
                                 :price 10.99
                                 :img "http://www.example.com/A"}
                                ["978000000001"]
                                {:store 0
                                 :title "Book B"
                                 :author "Author B"
                                 :available false
                                 :price 11.99
                                 :img "http://www.example.com/B"}
                                ["978000000002"]
                                {:store 0
                                 :title "Book C"
                                 :author "Author C"
                                 :available true
                                 :price 11.99
                                 :img "http://www.example.com/C"}
                                ["978000000003"]
                                {:store 0
                                 :title "Book A"
                                 :author "Author A"
                                 :available true
                                 :price 11.99
                                 :img "http://www.example.com/A"}
                                ["978000000001"]
                                {:store 1
                                 :title "Book B"
                                 :author "Author B"
                                 :available true
                                 :price 12.99
                                 :img "http://www.example.com/B"}
                                ["978000000002"]})
