describe('GeolocationCtrl', function() {
  'use strict';

  var scope, GeolocationCtrl;

  beforeEach(function() {
    module('CityShelf');
  });

  beforeEach(inject(function($controller, $rootScope) {
    scope = $rootScope.$new();

    GeolocationCtrl = $controller('GeolocationCtrl', {
      $scope: scope
    });
  }));

  it('has an example spec', function() {
    expect(true).toBe(true);
  });
});
