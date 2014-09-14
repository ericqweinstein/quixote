describe('Search', function() {
  'use strict';

  var search, httpBackend;

  beforeEach(function() {
    module('CityShelf');
  });

  // Mock Angular's interaction with the API.
  beforeEach(inject(function(Search, _$httpBackend_) {
    search      = Search;
    httpBackend = _$httpBackend_;
    httpBackend.expectGET('/api/stores/0/?query=test').respond([{ availability: 'On Shelves Now!' }]);
  }));

  // Ensure all requests complete successfully.
  afterEach(function() {
    httpBackend.verifyNoOutstandingExpectation();
    httpBackend.verifyNoOutstandingRequest();
  });

  it('gets search data from the API', function() {
    search.execute('test', 0);

    httpBackend.flush();

    expect(search.fetch()[0].availability).toEqual('On Shelves Now!');
    expect(search.getQuery()).toEqual('test');
  });
});
