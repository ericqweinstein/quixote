/**
 * @file Store service.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.factory('Store', ['$resource', function($resource) {
  'use strict';

  return $resource('/api/stores/:id/?query=:query', { id: '@id' });
}]);
