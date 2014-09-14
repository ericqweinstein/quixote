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

  it('has an example spec', function() {
    expect(true).toBe(true);
  });
});
