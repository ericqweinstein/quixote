; @file Filters and normalizes book metadata
; returned from the various bookstores.
; @author Eric Weinstein <eric.q.weinstein@gmail.com>

(ns cs.filter)

(defn available? [book]
  "Checks whether a book is available."
  (not (= (:availability book) "Unavailable")))

(defn normalize [text]
  "Normalizes availability language across bookstores."
  (cond
    (re-find #"(?ix) not" text) "Unavailable"
    (re-find #"(?ix) warehouse|distributor|special|usually|currently" text) "Order by phone"
    (re-find #"(?ix) in\s+stock|available|table|section|shelves" text) "On shelves now!"
    :else "Unavailable"))

(defn update [store-data]
  "Updates book availability metadata with normalized text."
  (map #(update-in % [:availability] normalize) store-data))

(defn remove-unavailable [store-data]
  "Removes a book from search results when it is unavailable."
  (filter #(available? %) store-data))
