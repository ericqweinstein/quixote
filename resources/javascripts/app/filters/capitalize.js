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
