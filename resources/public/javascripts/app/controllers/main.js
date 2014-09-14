/**
 * @file Main controller for the application.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.controller('MainCtrl', ['$scope', '$location', 'Search', function StoresCtrl($scope, $location, Search) {
  'use strict';

  /**
   * The number of stores to query.
   * @type {Number}
   * @todo Move this into the service layer.
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
   * Kick off requests to the API for book
   * availability data.
   * @return {Array} JSON describing the
   * search results for the book.
   * @method
   */
  $scope.search = function() {
    $scope.loading = true;

    $location.path('/geolocation');

    for (var i = 0; i < NUMBER_OF_STORES; i++) {
      Search.execute($scope.form.book, i);
    }
  };
}]);
