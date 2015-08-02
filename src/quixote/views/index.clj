(ns ^{:doc "Provides the base CityShelf view."
      :author "Eric Weinstein <eric.q.weinstein@gmail.com>"}
  quixote.views.index
  (:use [hiccup.page :only (html5 include-css include-js)]
        [hiccup.element :only (javascript-tag)]))

(defn index
  "Provides the base CityShelf view."
  [title]
  (html5 {:lang "en" :data-ng-app "CityShelf"}
         [:head
          [:title title]
          (include-css "stylesheets/vendor/ionic/ionic.css")
          (include-css "stylesheets/vendor/ionic/ionicons.css")
          (include-css "stylesheets/cityshelf.min.css")
          [:link {:rel "apple-touch-icon" :href "apple-touch-icon.png/apple-touch-icon.png"}]
          [:link {:rel "apple-touch-icon" :sizes "57x57" :href "apple-touch-icon.png/apple-touch-icon-57x57-precomposed.png"}]
          [:link {:rel "apple-touch-icon" :sizes "72x72" :href "apple-touch-icon.png/apple-touch-icon-72x72-precomposed.png"}]
          [:link {:rel "apple-touch-icon" :sizes "114x114" :href "apple-touch-icon.png/apple-touch-icon-114x114-precomposed.png"}]
          [:link {:rel "apple-touch-icon" :sizes "144x144" :href "apple-touch-icon.png/apple-touch-icon-144x144-precomposed.png"}]
          [:meta {:charset "UTF-8"}]
          [:meta {:name "description" :content "CityShelf: Go Local for Books"}]
          [:meta {:name "keywords" :content "CityShelf, indie, bookstore, local, books"}]
          [:meta {:name "viewport"
                  :content "user-scalable=no, width=device-width, height=device-height, initial-scale=1.0, maximum-scale=1.0, minimal-ui"}]
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
          (include-js "javascripts/cityshelf.min.js")
          (javascript-tag
            "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
             (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
             m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
             })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

             ga('create', 'UA-56171518-1', 'auto');
             ga('send', 'pageview');")]))
