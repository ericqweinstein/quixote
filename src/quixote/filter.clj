(ns ^{:doc "Filters and normalizes book metadata
           returned from the various bookstores."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  quixote.filter)

(declare normalize)

(defn has?
  "Checks whether a particular store has the given book in stock."
  [availability-text]
  (not= (normalize availability-text) "Unavailable"))

(defn- normalize
  "Normalizes availability language across bookstores."
  [text]
  (cond
    (re-find #"(?ix) not|button_notify" text) "Unavailable"
    (re-find #"(?ix) warehouse|distributor|special|usually|currently" text) "Out of stock"
    (re-find #"(?ix) in\s+stock|available|table|section|shelves|in\s+the\s+store|addtocart" text) "On shelves now"
    :else "Unavailable"))
