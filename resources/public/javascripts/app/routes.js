/**
 * @file Manage routing for the application.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
  'use strict';

  $routeProvider.when('/', {
    controller: 'MainCtrl'
  , templateUrl: 'templates/main.html'
  }).
  when('/search', {
    controller: 'SearchCtrl'
  , templateUrl: 'templates/search.html'
  }).
  when('/geolocation', {
    controller: 'GeolocationCtrl'
  , templateUrl: 'templates/geolocation.html'
  }).
  otherwise({
    redirectTo: 'templates/404.html'
  });

  $locationProvider.html5Mode(true);
  $locationProvider.hashPrefix('!');
}]);
