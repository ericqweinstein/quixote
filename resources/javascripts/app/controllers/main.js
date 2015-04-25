/**
 * @file Main controller for the application.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.controller('MainCtrl', ['$scope', '$location', '$route', 'Search', 'Geolocation', function StoresCtrl($scope, $location, $route, Search, Geolocation) {
  'use strict';

  /**
   * The number of stores to query.
   * @type {Number}
   * @todo Move this into the service layer. (EW 02 Feb 2015)
   */
  var NUMBER_OF_STORES = 8;

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
   * @param {PositionError} err The error thrown by
   * the geolocation API.
   */
  var handleError = function(err) {
    var msg;

    if (err.code === 1) {
      // PERMISSION_DENIED: The user did not wish to share location.
      msg = 'Okay!';
    } else {
      // POSITION_UNAVAILABLE or TIMEOUT: We could not find the user.
      msg = 'Oops, we can\'t find you.';
    }

    Geolocation.setError(msg);
    $location.path('/geolocation');
  };

  // This is a SERIOUS hack and should be
  // removed as soon as possible. (EW 02 Feb 2015)
  function sleep(millis, cb) {
    setTimeout(function() { cb(); }, millis);
  }

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
      }
      , handleError
      , { maximumAge: 600000, timeout: 3000, enableHighAccuracy: false });
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

    if (!!Geolocation.fetch().length) {
      // See above comment re: hack. (EW 30 Sep 2014)
      sleep(3000, function() {
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
