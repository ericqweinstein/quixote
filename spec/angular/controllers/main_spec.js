describe('MainCtrl', function() {
  'use strict';

  var scope, MainCtrl;

  beforeEach(function() {
    module('CityShelf');
  });

  beforeEach(inject(function($controller, $rootScope) {
    scope = $rootScope.$new();

    MainCtrl = $controller('MainCtrl', {
      $scope: scope
    });
  }));

  describe('loading spinner', function() {
    it('is not shown by default', function() {
      expect(scope.loading).toBe(false);
    });
  });
});
