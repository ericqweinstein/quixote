; @file Provides the base CityShelf view.
; @author Eric Weinstein <eric.q.weinstein@gmail.com>

(ns cs.views.index
  (:use [hiccup.page :only (html5 include-css include-js)]))

(defn index
  "Provides the base CityShelf view."
  [title]
  (html5 {:lang "en" :data-ng-app "CityShelf"}
         [:head
          [:title title]
          (include-css "stylesheets/vendor/ionic/ionic.css")
          (include-css "stylesheets/vendor/ionic/ionicons.css")
          (include-css "stylesheets/cs-style.css")
          [:meta {:charset "UTF-8"}]
          [:meta {:name "description" :content "CityShelf: Go Local for Books"}]
          [:meta {:name "keywords" :content "CityShelf, indie, bookstore, local, books"}]
          [:meta {:name "viewport" :content "user-scalable=no, width=device-width, initial-scale=1.0, maximum-scale=1.0 minimal-ui"}]
          [:meta {:name "fragment" :content "!"}]
          [:meta {:property "og:image" :content "img/cs_logo.svg"}]]
          [:base {:href "/"}]
         [:body
          [:div {:id "main" :data-ng-view ""}]
          (include-js "https://maps.googleapis.com/maps/api/js?key=AIzaSyBmimHj60eXII2ZAc7VY1pzqs2ANJqdwZI")
          (include-js "javascripts/vendor/ionic/ionic.bundle.min.js")
          (include-js "javascripts/vendor/angular/angular-resource.min.js")
          (include-js "javascripts/vendor/angular/angular-route.min.js")
          (include-js "javascripts/vendor/lodash.underscore.min.js")
          (include-js "javascripts/vendor/angular/angular-google-maps.min.js")
          (include-js "javascripts/app/app.js")
          (include-js "javascripts/app/routes.js")
          (include-js "javascripts/app/filters/capitalize.js")
          (include-js "javascripts/app/filters/truncate.js")
          (include-js "javascripts/app/services/store.js")
          (include-js "javascripts/app/services/geolocation.js")
          (include-js "javascripts/app/services/search.js")
          (include-js "javascripts/app/controllers/main.js")
          (include-js "javascripts/app/controllers/search.js")
          (include-js "javascripts/app/controllers/geolocation.js")]))
