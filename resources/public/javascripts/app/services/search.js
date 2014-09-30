/**
 * @file Search service (shares search information between
 * the main application and search controllers).
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.factory('Search', ['Store', function(Store) {
  'use strict';

  /**
   * The results of our search.
   * @type {Array}
   */
  var searchResults = [];

  /**
   * The search query.
   * @type {String}
   */
  var searchQuery = '';

  /**
   * Queries bookstores for data.
   * @param {String} query The query string.
   * @param {Number} storeNumber The store ID number.
   * @return {Object} The results of our search.
   * @method
   */
  var execute = function(query, storeNumber) {
    searchQuery = query;
    Store.query({ id: storeNumber, query: query }).$promise.then(function(results) {
      searchResults = searchResults.concat(results);
    });
  };

  /**
   * Retrieves search data from the service.
   * @return {Object} The stored search results.
   * @method
   */
  var fetch = function() {
    return searchResults;
  };

  var flush = function() {
    searchResults = [];
  };

  /**
   * Retrieves the original query string.
   * @return {String} The original query.
   * @method
   */
  var getQuery = function() {
    return searchQuery;
  };

  return {
    execute: execute
  , fetch: fetch
  , flush: flush
  , getQuery: getQuery
  };
}]);
