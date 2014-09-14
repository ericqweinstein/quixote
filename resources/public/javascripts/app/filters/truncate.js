/**
 * @file Truncate text that is too long.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

CityShelf.filter('truncate', function() {
  'use strict';

  return function(text, length, end) {
    if (isNaN(length)) {
      length = 25;
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
