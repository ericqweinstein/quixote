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
