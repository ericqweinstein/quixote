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

  it('has an example spec', function() {
    expect(true).toBe(true);
  });
});
