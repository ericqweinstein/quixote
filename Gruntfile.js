/**
 * @file Task runner for linting, minification, etc.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

module.exports = function(grunt) {
  'use strict';

  /* jshint camelcase: false */
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json')
  , jshint: {
      options: {
        jshintrc: 'config/jshint.json'
      }
    , files: [
        'Gruntfile.js'
      , 'resources/public/javascripts/**/*.js'
      , '!resources/public/javascripts/vendor/**/*.js'
      ]
    }
  , jasmine: {
      src: ['resources/public/javascripts/app/app.js'
          , 'resources/public/javascripts/app/routes.js'
          , 'resources/public/javascripts/app/filters/capitalize.js'
          , 'resources/public/javascripts/app/filters/truncate.js'
          , 'resources/public/javascripts/app/services/store.js'
          , 'resources/public/javascripts/app/services/search.js'
          , 'resources/public/javascripts/app/services/geolocation.js'
          , 'resources/public/javascripts/app/controllers/main.js'
          , 'resources/public/javascripts/app/controllers/search.js'
          , 'resources/public/javascripts/app/controllers/geolocation.js']
    , options: {
        specs: 'spec/angular/**/*.js'
      , summary: true
      , vendor: ['https://maps.googleapis.com/maps/api/js?key=AIzaSyBmimHj60eXII2ZAc7VY1pzqs2ANJqdwZI'
               , 'resources/public/javascripts/vendor/ionic/ionic.bundle.js'
               , 'resources/public/javascripts/vendor/angular/angular-resource.min.js'
               , 'resources/public/javascripts/vendor/angular/angular-route.min.js'
               , 'resources/public/javascripts/vendor/lodash.underscore.min.js'
               , 'resources/public/javascripts/vendor/angular/angular-google-maps.min.js'
               , 'resources/public/javascripts/vendor/angular/angular-mocks.js']
      }
    }
  , protractor: {
      options: {
        configFile: 'spec/features/protractor_conf.js'
      , keepAlive: true
      , noColor: false
      }
    , e2e: {}
    }
  , jsdoc: {
      dist: {
        src: ['resources/public/javascripts/app/**/*.js']
      , options: {
          destination: 'js_doc'
        }
      }
    }
  , sass: {
      dist: {
        files: {
          'resources/public/stylesheets/cs-style.css': 'resources/public/stylesheets/scss/cs-style.scss'
        }
      }
    }
  });

  grunt.loadNpmTasks('grunt-contrib-jasmine');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-protractor-runner');
  grunt.loadNpmTasks('grunt-jsdoc');
  grunt.loadNpmTasks('grunt-contrib-sass');

  // Lint, test, build documentation, and compile SCSS to CSS with `grunt`.
  grunt.registerTask('default', ['jshint', 'jasmine', 'jsdoc', 'sass']);
};
