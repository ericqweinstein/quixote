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
      , 'resources/javascripts/**/*.js'
      , '!resources/javascripts/vendor/**/*.js'
      ]
    }
  , jasmine: {
      src: ['resources/javascripts/app/app.js'
          , 'resources/javascripts/app/routes.js'
          , 'resources/javascripts/app/filters/capitalize.js'
          , 'resources/javascripts/app/filters/truncate.js'
          , 'resources/javascripts/app/services/store.js'
          , 'resources/javascripts/app/services/search.js'
          , 'resources/javascripts/app/services/geolocation.js'
          , 'resources/javascripts/app/controllers/main.js'
          , 'resources/javascripts/app/controllers/search.js'
          , 'resources/javascripts/app/controllers/geolocation.js']
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
        src: ['resources/javascripts/app/**/*.js']
      , options: {
          destination: 'js_doc'
        }
      }
    }
  , sass: {
      dist: {
        files: {
          'resources/stylesheets/cs-style.css': 'resources/stylesheets/scss/cs-style.scss'
        }
      }
    }
  , concat: {
      css: {
        src: ['resources/stylesheets/*.css']
      , dest: 'resources/stylesheets/cityshelf.css'
      }
    , js: {
        src: ['resources/javascripts/app/**/*.js']
      , dest: 'resources/javascripts/cityshelf.js'
      }
    }
  , cssmin: {
      css: {
        src: 'resources/stylesheets/cityshelf.css'
      , dest: 'resources/public/stylesheets/cityshelf.min.css'
      }
    }
  , uglify: {
      options: {
        mangle: true
      }
    , js: {
        files: {
          'resources/public/javascripts/cityshelf.min.js': ['resources/javascripts/cityshelf.js']
        }
      }
    }
  });

  grunt.loadNpmTasks('grunt-contrib-jasmine');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-protractor-runner');
  grunt.loadNpmTasks('grunt-jsdoc');
  grunt.loadNpmTasks('grunt-contrib-sass');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-cssmin');
  grunt.loadNpmTasks('grunt-contrib-uglify');

  // Lint, test, build documentation, and compile SCSS to CSS with `grunt`.
  grunt.registerTask('default', ['jshint', 'jasmine', 'jsdoc', 'sass', 'concat', 'cssmin', 'uglify']);
};
