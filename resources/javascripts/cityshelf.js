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
   * Sets any errors we may have gotten
   * from the geolocation API.
   * @type {String}
   */
  $scope.error = Geolocation.getError();

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
   * @todo Move this into the service layer. (EW 02 Feb 2015)
   */
  var NUMBER_OF_STORES = 7;

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
   * @param {PositionError} err The error thrown by
   * the geolocation API.
   */
  var handleError = function(err) {
    var msg;

    if (err.code === 1) {
      // PERMISSION_DENIED: The user did not wish to share location.
      msg = 'Okay!';
    } else {
      // POSITION_UNAVAILABLE or TIMEOUT: We could not find the user.
      msg = 'Oops, we can\'t find you.';
    }

    Geolocation.setError(msg);
    $location.path('/geolocation');
  };

  // This is a SERIOUS hack and should be
  // removed as soon as possible. (EW 02 Feb 2015)
  function sleep(millis, cb) {
    setTimeout(function() { cb(); }, millis);
  }

  /**
   * Retrieves the user's location via the
   * HTML5 Geolocation API and sets it on
   * the Geolocation service.
   * @method
   */
  $scope.setLocation = function() {
    sleep(3000, function() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
          Geolocation.set(position.coords.latitude
                        , position.coords.longitude);

          $location.path('/search');
          $route.reload();
        }
        , handleError
        , { maximumAge: 600000, timeout: 3000, enableHighAccuracy: false });
      } else {
        // Geolocation is not available.
        $location.path('/geolocation');
        $route.reload();
      }
    });
  };

  /**
   * Kick off requests to the API for book
   * availability data.
   * @return {Array} JSON describing the
   * search results for the book.
   * @method
   */
  $scope.search = function() {
    $scope.loading = true;

    if (!!Geolocation.fetch().length) {
      // See above comment re: hack. (EW 30 Sep 2014)
      sleep(3000, function() {
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

CityShelf.controller('SearchCtrl', [
    '$scope'
  , '$http'
  , '$location'
  , '$ionicScrollDelegate'
  , 'Search'
  , 'Geolocation'
  , function SearchCtrl(
      $scope
    , $http
    , $location
    , $ionicScrollDelegate
    , Search
    , Geolocation) {

  'use strict';

  /**
   * The results of our search.
   * @type {Object}
   */
  var allResults = $scope.results = Search.fetch();

  /**
   * The options by which we may search
   * (i.e. price, proximity).
   * @type {Array}
   */
  $scope.options = ['proximity', 'price'];

  /**
   * Scrolls the user to the top of the view.
   * @method
   */
  var scrollTop = function() {
    console.log('Scrolling to top!');
    $ionicScrollDelegate.scrollTop();
  };

  /**
   * Determines the order in which results appear.
   * Defaults to item availability, but can also be
   * set to price or proximity to the user.
   * @param {Object} predicate The predicate from
   * the search template. We actually do need to pass
   * the argument in order to properly get a hold on
   * the result object, hence the JSHint magic comment.
   * @return {String|Number} A string in the case of
   * availability; a number in the case of price or
   * proximity (the latter describing the distance
   * between the user and the various bookstores).
   * @method
   */

  /* jshint unused: false */
  $scope.sortBy = function(predicate) {
    return function(result) {
      if ($scope.selected === 'price') {
        return +result.price;
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
   * Keeps track of whether we're filtering
   * results.
   * @member {Boolean}
   */
  var isFiltered = false;

  /**
   * Filters the list of results by the
   * passed in the criterion (e.g. filtering
   * by ISBN will only show titles with
   * that ISBN).
   * @param {String} field The field on which
   * to filter.
   * @param {String} value The value to use
   * when filtering based on the field.
   * @return {Undefined}
   */
  $scope.filter = function(field, value) {
    if (isFiltered) {
      $scope.results = allResults;
      isFiltered = false;
    } else {
      $scope.results = $scope.results.filter(function(element) {
        return element[field] === value;
      });
      isFiltered = true;
    }

    scrollTop();
  };

  /**
   * Handles back button behavior (i.e. goes back to
   * the home page if we're not filtered, returns to
   * filtered results if we are).
   * @param {String} url The location to go back to.
   * @method
   */
  $scope.back = function(url) {
    // @todo DRY this up. (EW 06 Dec 2014)
    if (isFiltered) {
      $scope.results = allResults;
      isFiltered = false;
      scrollTop();
    } else {
      $location.path(url);
    }
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
    * Initializes any errors we may have gotten
    * from the geolocation API.
    * @type {String}
    * @private
    */
   var geolocationError = '';

   /**
    * Sets the geolocation error string as needed.
    * @param {String} msg The error message.
    * @method
    */
   var setError = function(msg) {
     geolocationError = msg;
   };

   /**
    * Gets the geolocation error string.
    * @return {String} The error string.
    * @method
    */
   var getError = function() {
     return geolocationError;
   };

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
    * @private
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
   , getError: getError
   , proximity: proximity
   , set: set
   , setError: setError
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
