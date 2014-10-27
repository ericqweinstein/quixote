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
