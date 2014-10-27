/**
 * @file Geolocation controller (used to determine
 * a user's proximity to various bookstores).
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.controller('GeolocationCtrl', ['$scope', '$location', 'Geolocation', function GeolocationCtrl($scope, $location, Geolocation) {
  'use strict';

  /**
   * Initializes the form data object we'll
   * use to submit the user's location to
   * Google's Geocoding service if the user
   * doesn't want to share his/her location
   * or his/her browser is too old.
   * @type {Object}
   */
  $scope.form = {};

  /**
   * Sets any errors we may have gotten
   * from the geolocation API.
   * @type {String}
   */
  $scope.error = Geolocation.getError();

  /**
   * Retrieves a latitude and longitude
   * from Google's Geocoding service.
   * @method
   */
  $scope.geolocate = function() {
    Geolocation.geolocate($scope.form.location);

    $location.path('/search');
  };
}]);
