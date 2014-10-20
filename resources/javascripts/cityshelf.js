/**
 * @file Creates and configures the client-side application.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 * @copyright CityShelf, 2014
 * @version 0.1.0
 */

/* exported CityShelf */
var CityShelf = angular.module('CityShelf', ['ngResource'
                                           , 'ngRoute'
                                           , 'ionic'
                                           , 'google-maps']);

/**
 * @file Geolocation controller (used to determine
 * a user's proximity to various bookstores).
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.controller('GeolocationCtrl', ['$scope', '$location', 'Geolocation', function GeolocationCtrl($scope, $location, Geolocation) {
  'use strict';

  /**
   * Initializes the form data object we'll
   * use to submit the user's location to
   * Google's Geocoding service if the user
   * doesn't want to share his/her location
   * or his/her browser is too old.
   * @type {Object}
   */
  $scope.form = {};

  /**
   * Retrieves a latitude and longitude
   * from Google's Geocoding service.
   * @method
   */
  $scope.geolocate = function() {
    Geolocation.geolocate($scope.form.location);

    $location.path('/search');
  };
}]);

/**
 * @file Main controller for the application.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.controller('MainCtrl', ['$scope', '$location', '$route', 'Search', 'Geolocation', function StoresCtrl($scope, $location, $route, Search, Geolocation) {
  'use strict';

  /**
   * The number of stores to query.
   * @type {Number}
   * @todo Move this into the service layer.
   */
  var NUMBER_OF_STORES = 8;

  /**
   * Set timeout for geolocation request (in ms).
   * @type {Number}
   */
  var TIMEOUT = 3000;

  /**
   * Form data we'll use when searching for a book.
   * @type {Object}
   */
  $scope.form = {};

  /**
   * Determines whether we show the loading spinner.
   * @type {Boolean}
   */
  $scope.loading = false;

  /**
   * Error handling function for geolocation.
   */
  var handleError = function() {
    // Go to geolocation for now.
    $location.path('/geolocation');
  };

  /**
   * Retrieves the user's location via the
   * HTML5 Geolocation API and sets it on
   * the Geolocation service.
   * @method
   */
  $scope.setLocation = function() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(function(position) {
        Geolocation.set(position.coords.latitude
                      , position.coords.longitude);

        $location.path('/search');
      }, handleError, { timeout: TIMEOUT });
    } else {
      // Geolocation is not available.
      $location.path('/geolocation');
    }
  };

  // This is a SERIOUS hack and should be
  // removed as soon as possible. (EW 30 Sep 2014)
  function sleep(millis, cb) {
    setTimeout(function() { cb(); }, millis);
  }

  /**
   * Kick off requests to the API for book
   * availability data.
   * @return {Array} JSON describing the
   * search results for the book.
   * @method
   */
  $scope.search = function() {
    $scope.loading = true;

    if (Geolocation.fetch().length) {
      // See above comment re: hack. (EW 30 Sep 2014)
      sleep(1000, function() {
        $location.path('/search');
        $route.reload();
      });
    } else {
      // Attempt geolocation
      $scope.setLocation();
    }

    Search.flush();

    for (var i = 0; i < NUMBER_OF_STORES; i++) {
      Search.execute($scope.form.book, i);
    }
  };
}]);

/**
 * @file Search controller (fetches store search results from the API).
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.controller('SearchCtrl', ['$scope', '$http', 'Search', 'Geolocation', function SearchCtrl($scope, $http, Search, Geolocation) {
  'use strict';

  /**
   * The results of our search.
   * @type {Object}
   */
  $scope.results = Search.fetch();

  /**
   * The options by which we may search
   * (i.e. price, proximity).
   * @type {Array}
   */
  $scope.options = ['proximity', 'price'];

  /**
   * Determines the order in which results appear.
   * Defaults to item availability, but can also be
   * set to price or proximity to the user.
   * @param {Object} predicate The predicate from
   * the search template. We actually do need to pass
   * the argument in order to properly get a hold on
   * the result object, hence the JSHint magic comment.
   * @return {String|Number} A string in the case of price
   * or availability; a number in the case of proximity
   * (describing the distance between the user and the
   * various bookstores).
   * @method
   */

  /* jshint unused: false */
  $scope.sortBy = function(predicate) {
    return function(result) {
      if ($scope.selected === 'price') {
        return result.price;
      } else if ($scope.selected === 'proximity') {
        return Geolocation.proximity(Geolocation.fetch()
                                   , [result.map.center.latitude
                                     , result.map.center.longitude]);

      } else {
        return result.availability;
      }
    };
  };

  /**
   * Initializes the selection.
   * @member {String}
   */
  $scope.selected = $scope.options[0];

  /**
   * Handles the UI change for changing
   * search options (i.e. by price, by proximity).
   * @param {String} option The search option.
   * @method
   */
  $scope.select = function(item) {
    $scope.selected = item;
  };

  /**
   * Determines whether we show the search criteria.
   * @type {Boolean}
   */
  $scope.showSearch = true;

  /**
   * Determines whether we show the map.
   * @type {Boolean}
   */
  $scope.showMap = false;

  /**
   * The original query string.
   * @type {String}
   */
  $scope.searchQuery = Search.getQuery();
}]);

/**
 * @file Filter to capitalize book titles (searches will likely
 * be lowercase or present inconsistent capitalization).
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.filter('capitalize', function() {
  'use strict';

  var EXCLUDES = [
    'a'
  , 'an'
  , 'as'
  , 'at'
  , 'and'
  , 'but'
  , 'for'
  , 'from'
  , 'in'
  , 'is'
  , 'of'
  , 'on'
  , 'the'
  , 'to'
  ];

  /**
   * Determines whether an array contains
   * a particular element.
   * @param {Array} a An array.
   * @param {?} e An element to look for.
   * @return {Boolean} True if the element
   * is present, false otherwise.
   * @method
   */
  var contains = function(a, e) {
    var i = a.length;

    while (i--) {
      if (a[i] === e) {
        return true;
      }
    }
    return false;
  };

  return function(input) {
    if (input) {
      var words = input.split(' '),
          output = [];

      for (var i = 0; i < words.length; i++) {
        // Capitalize the word unless it's one we should leave alone.
        if (contains(EXCLUDES, words[i]) && i !== 0) {
          output.push(words[i]);
        } else {
          output.push(words[i].charAt(0).toUpperCase() + words[i].substr(1).toLowerCase());
        }
      }

      return output.join(' ');
    } else {
      return '';
    }
  };
});

/**
 * @file Truncate text that is too long.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.filter('truncate', function() {
  'use strict';

  return function(text, length, end) {
    if (isNaN(length)) {
      length = 35;
    }

    if (end === undefined) {
      end = '...';
    }

    if (text.length <= length || text.length - end.length <= length) {
      return text;
    } else {
      return String(text).substring(0, length - end.length) + end;
    }
  };
});

/**
 * @file Manage routing for the application.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
  'use strict';

  $routeProvider.when('/', {
    controller: 'MainCtrl'
  , templateUrl: 'templates/main.html'
  }).
  when('/search', {
    controller: 'SearchCtrl'
  , templateUrl: 'templates/search.html'
  }).
  when('/geolocation', {
    controller: 'GeolocationCtrl'
  , templateUrl: 'templates/geolocation.html'
  }).
  otherwise({
    templateUrl: 'templates/404.html'
  });

  $locationProvider.html5Mode(true);
  $locationProvider.hashPrefix('!');
}]);

/**
 * @file Geolocation service (shares user geolocation information
 * among controllers, as well as owns the logic for calculating a
 * user's proximity to any given store).
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

 CityShelf.factory('Geolocation', [function() {
   'use strict';

   /**
    * Calculates the distance between one point (the user's
    * location) and another (any given bookstore). Eventually
    * we'll need to use the haversine formula or the spherical
    * law of cosines, but for now, the area we're covering is
    * small enough that we can treat it as a Cartesian plane.
    * (EW 31 Aug 2014)
    * @param {Array<Number>} origin The user's location.
    * @param {Array<Number>} destination A particular bookstore.
    * Both parameters are two-element arrays of numbers
    * expressing latitude and longitude.
    * @method
    */
   var proximity = function(origin, destination) {
     return Math.sqrt(Math.pow((origin[0] - destination[0]), 2) + Math.pow((origin[1] - destination[1]), 2));
   };

   /**
    * Initializes the user's location.
    * @type {Array<Number>}
    */
   var coordinates = [];

   /**
    * Sets the user's location.
    * @param {Number} lat The user's latitude.
    * @param {Number} long The user's longitude.
    * @method
    */
   var set = function(lat, long) {
     coordinates = [lat, long];
   };

   /**
    * Gets the user's location.
    * @return {Array<Number>} The user's location.
    */
   var fetch = function() {
     return coordinates;
   };

   /**
    * Retrieves a latitude and longitude
    * from Google's Geocoding service.
    * @param {Number} zip A US ZIP code.
    * @method
    */
   var geolocate = function(zip) {
     var geocoder = new google.maps.Geocoder()
       , address  = zip;

     geocoder.geocode({ address: address }, function(results, status) {
       if (status === google.maps.GeocoderStatus.OK) {

         var lat  = results[0].geometry.location.k
           , long = results[0].geometry.location.B;

         set(lat, long);
       }
       // @todo Error handling via alerts service. (EW 08 Sep 2014)
     });
   };

   return {
     fetch: fetch
   , geolocate: geolocate
   , proximity: proximity
   , set: set
   };
 }]);

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

  /**
   * Flushes the previous search results.
   * @method
   */
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

/**
 * @file Store service.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.factory('Store', ['$resource', function($resource) {
  'use strict';

  return $resource('/api/stores/:id/?query=:query', { id: '@id' });
}]);