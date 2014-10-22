/**
 * @file Main controller for the application.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.controller('MainCtrl', ['$scope', '$location', '$route', 'Search', 'Geolocation', function StoresCtrl($scope, $location, $route, Search, Geolocation) {
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
  var TIMEOUT = 3000;

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
      }, handleError, { timeout: TIMEOUT });
    } else {
      // Geolocation is not available.
      $location.path('/geolocation');
    }
  };

  // This is a SERIOUS hack and should be
  // removed as soon as possible. (EW 30 Sep 2014)
  function sleep(millis, cb) {
    setTimeout(function() { cb(); }, millis);
  }

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
      // See above comment re: hack. (EW 30 Sep 2014)
      sleep(4000, function() {
        $location.path('/search');
        $route.reload();
      });
    } else {
      // Attempt geolocation
      $scope.setLocation();
    }

    Search.flush();

    for (var i = 0; i < NUMBER_OF_STORES; i++) {
      Search.execute($scope.form.book, i);
    }
  };
}]);
