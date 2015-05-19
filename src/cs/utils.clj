(ns ^{:doc "CityShelf utility functions."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  cs.utils
  (:require [net.cgrand.enlive-html :as html]))

(defn fetch-url
  "Creates an Enlive HTML resource from the provided URL string."
  [url]
  (html/html-resource (java.net.URL. url)))
