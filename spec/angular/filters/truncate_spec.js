describe('Truncate', function() {
  'use strict';

  beforeEach(function() {
    module('CityShelf');
  });

  it('exists', inject(function($filter) {
    expect($filter('truncate')).toBeDefined();
  }));

  it('does not truncate short text', inject(function(truncateFilter) {
    expect(truncateFilter('foo')).toEqual('foo');
  }));

  it('truncates long text', inject(function(truncateFilter) {
    expect(truncateFilter('These are the voyages of the USS Enterprise')).toEqual('These are the voyages of the USS...');
  }));
});
