describe('SearchCtrl', function() {
  'use strict';

  var scope, SearchCtrl;

  beforeEach(function() {
    module('CityShelf');
  });

  beforeEach(inject(function($controller, $rootScope) {
    scope = $rootScope.$new();

    SearchCtrl = $controller('SearchCtrl', {
      $scope: scope
    });
  }));

  describe('search options', function() {
    it('searches by proximity or price', function() {
      expect(scope.options[0]).toBe('proximity');
      expect(scope.options[1]).toBe('price');
    });

    it('defaults to searching by proximity', function() {
      expect(scope.selected).toBe('proximity');
    });

    it('displays search criteria by default', function() {
      expect(scope.showSearch).toBe(true);
    });
  });

  describe('.select', function() {
    it('changes the selection criterion', function() {
      scope.select('price');
      expect(scope.selected).toBe('price');

      scope.select('proximity');
      expect(scope.selected).toBe('proximity');
    });
  });

  describe('map', function() {
    it('hides the map by default', function() {
      expect(scope.showMap).toBe(false);
    });
  });
});
