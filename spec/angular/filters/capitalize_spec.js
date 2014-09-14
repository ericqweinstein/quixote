describe('Capitalize', function() {
  'use strict';

  // Instantiate the application
  // before each test run.
  beforeEach(function() {
    module('CityShelf');
  });

  it('exists', inject(function($filter) {
    expect($filter('capitalize')).toBeDefined();
  }));

  it('capitalizes lowercase searches', inject(function(capitalizeFilter) {
    expect(capitalizeFilter('omon ra')).toEqual('Omon Ra');
  }));

  it('correctly capitalizes the first words of sentences', inject(function(capitalizeFilter) {
    expect(capitalizeFilter('the lion, the witch, and the wardrobe')).toEqual('The Lion, the Witch, and the Wardrobe');
  }));

  it('correctly capitalizes sentences with \'the\', \'of\', and so on', inject(function(capitalizeFilter) {
    expect(capitalizeFilter('house of leaves')).toEqual('House of Leaves');
  }));
});
