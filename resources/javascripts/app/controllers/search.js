/**
 * @file Search controller (fetches store search results from the API).
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.controller('SearchCtrl', ['$scope', '$http', 'Search', 'Geolocation', function SearchCtrl($scope, $http, Search, Geolocation) {
  'use strict';

  /**
   * The results of our search.
   * @type {Object}
   */
  var allResults = $scope.results = Search.fetch();

  /**
   * The options by which we may search
   * (i.e. price, proximity).
   * @type {Array}
   */
  $scope.options = ['proximity', 'price'];

  /**
   * Determines the order in which results appear.
   * Defaults to item availability, but can also be
   * set to price or proximity to the user.
   * @param {Object} predicate The predicate from
   * the search template. We actually do need to pass
   * the argument in order to properly get a hold on
   * the result object, hence the JSHint magic comment.
   * @return {String|Number} A string in the case of
   * availability; a number in the case of price or
   * proximity (the latter describing the distance
   * between the user and the various bookstores).
   * @method
   */

  /* jshint unused: false */
  $scope.sortBy = function(predicate) {
    return function(result) {
      if ($scope.selected === 'price') {
        return +result.price;
      } else if ($scope.selected === 'proximity') {
        return Geolocation.proximity(Geolocation.fetch()
                                   , [result.map.center.latitude
                                     , result.map.center.longitude]);

      } else {
        return result.availability;
      }
    };
  };

  /**
   * Initializes the selection.
   * @member {String}
   */
  $scope.selected = $scope.options[0];

  /**
   * Handles the UI change for changing
   * search options (i.e. by price, by proximity).
   * @param {String} option The search option.
   * @method
   */
  $scope.select = function(item) {
    $scope.selected = item;
  };

  /**
   * Keeps track of whether we're filtering
   * results.
   * @member {Boolean}
   */
  var isFiltered = false;

  /**
   * Filters the list of results by the
   * passed in the criterion (e.g. filtering
   * by ISBN will only show titles with
   * that ISBN).
   * @param {String} field The field on which
   * to filter.
   * @param {String} value The value to use
   * when filtering based on the field.
   * @return {Undefined}
   */
  $scope.filter = function(field, value) {
    if (isFiltered) {
      $scope.results = allResults;
      isFiltered = false;
    } else {
      $scope.results = $scope.results.filter(function(element) {
        return element[field] === value;
      });
      isFiltered = true;
    }
  };

  /**
   * Determines whether we show the search criteria.
   * @type {Boolean}
   */
  $scope.showSearch = true;

  /**
   * Determines whether we show the map.
   * @type {Boolean}
   */
  $scope.showMap = false;

  /**
   * The original query string.
   * @type {String}
   */
  $scope.searchQuery = Search.getQuery();
}]);
