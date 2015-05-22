(ns ^{:doc "Tests for utils.clj"
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  cs.utils-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [cs.utils :refer :all]))

(def fixture-data
  [
   {:isbn "978000000001"
    :availability {
                   :store 0
                   :available true
                   :price 11.99
                   }
    }
   {:isbn "978000000002"
    :availability {
                   :store 0
                   :available true
                   :price 11.99
                   }
    }
   {:isbn "978000000003"
    :availability {
                   :store 0
                   :available true
                   :price 11.99
                   }
    }
   {:isbn "978000000001"
    :availability {
                   :store 1
                   :available true
                   :price 10.99
                   }
    }
   {:isbn "978000000002"
    :availability {
                   :store 1
                   :available true
                   :price 12.99
                   }
    }
   {:isbn "978000000003"
    :availability {
                   :store 1
                   :available true
                   :price 13.99
                   }
    }
  ]
)

(facts "About manipulating book data"
       (fact "It converts data from search results
             to books with availability fields")
       (pivot fixture-data) => {"978000000001"
                                [{:available true, :price 11.99, :store 0}
                                 {:available true, :price 10.99, :store 1}],
                                "978000000002"
                                [{:available true, :price 11.99, :store 0}
                                 {:available true, :price 12.99, :store 1}],
                                "978000000003"
                                [{:available true, :price 11.99, :store 0}
                                 {:available true, :price 13.99, :store 1}]})
