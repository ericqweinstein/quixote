(ns ^{:doc "Filters and normalizes book metadata
           returned from the various bookstores."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  cs.filter)

(declare available? normalize)

(defn update
  "Updates book availability metadata with normalized text."
  [store-data]
  (map #(update-in % [:availability] normalize) store-data))

(defn remove-unavailable
  "Removes a book from search results when it is unavailable."
  [store-data]
  (filter available? store-data))

(defn- available?
  "Checks whether a book is available."
  [book]
  (not= (:availability book) "Unavailable"))

(defn- normalize
  "Normalizes availability language across bookstores."
  [text]
  (cond
    (re-find #"(?ix) not" text) "Unavailable"
    (re-find #"(?ix) warehouse|distributor|special|usually|currently" text) "Out of stock"
    (re-find #"(?ix) in\s+stock|available|table|section|shelves|in\s+the\s+store" text) "On shelves now"
    :else "Unavailable"))
