/**
 * @file Main controller for the application.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.controller('MainCtrl', ['$scope', '$location', 'Search', 'Geolocation', function StoresCtrl($scope, $location, Search, Geolocation) {
  'use strict';

  /**
   * The number of stores to query.
   * @type {Number}
   * @todo Move this into the service layer.
   */
  var NUMBER_OF_STORES = 8;

  /**
   * Set timeout for geolocation request (in ms).
   * @type {Number}
   */
  var TIMEOUT = 500;

  /**
   * Form data we'll use when searching for a book.
   * @type {Object}
   */
  $scope.form = {};

  /**
   * Determines whether we show the loading spinner.
   * @type {Boolean}
   */
  $scope.loading = false;

  /**
   * Error handling function for geolocation.
   */
  var handleError = function() {
    // Go to geolocation for now.
    $location.path('/geolocation');
  };

  /**
   * Retrieves the user's location via the
   * HTML5 Geolocation API and sets it on
   * the Geolocation service.
   * @method
   */
  $scope.setLocation = function() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(function(position) {
        Geolocation.set(position.coords.latitude
                      , position.coords.longitude);

        $location.path('/search');
      }, handleError, TIMEOUT);
    } else {
      // Geolocation is not available.
      $location.path('/geolocation');
    }
  };

  /**
   * Kick off requests to the API for book
   * availability data.
   * @return {Array} JSON describing the
   * search results for the book.
   * @method
   */
  $scope.search = function() {
    $scope.loading = true;

    if (Geolocation.fetch().length) {
      $location.path('/search');
    } else {
      // Attempt geolocation
      $scope.setLocation();
    }

    for (var i = 0; i < NUMBER_OF_STORES; i++) {
      Search.execute($scope.form.book, i);
    }
  };
}]);
