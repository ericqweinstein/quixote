CityShelf
=========

## About
CityShelf is a web application that makes searching for books through local and independent booksellers quick and easy.

## Running Locally
CityShelf is an Angular SPA with a Clojure API (generated via [Enlive](https://github.com/cgrand/enlive), [Liberator](http://clojure-liberator.github.io/liberator/), and [Compojure](https://github.com/weavejester/compojure)). You'll need the following:

### Client Application
* NodeJS + NPM (Node Package Manager)
* Grunt (`npm install`)

### API
* Clojure 1.5+
* Leiningen 2

## Installation
1. Clone the repository (`$ git clone git@github.com:ericqweinstein/cityshelf.git`)
2. Install client dependencies (`npm install`)
3. Install API dependencies (`lein deps`)
4. Start the web server on port 8080 (`lein run`)

## Deploying
CityShelf currently lives on Heroku at http://cityshelf.herokuapp.com/.

1. Lint/test the client application, build/minify CSS & JS, &c (`grunt`)
2. Test the API (`lein midje`)
3. Deploy (`git push heroku master`)

## Contributing
1. Branch (`git checkout -b fancy-new-feature`)
2. Commit (`git commit -m "Fanciness!"`)
3. Lint and test the client (`grunt`)
4. Lint the API (`lein lint`)
5. Test the API (`lein midje`)
6. Push (`git push origin fancy-new-feature`)
7. Ye Olde Pulle Request

## Miscellaneous
You can generate documentation with `grunt jsdoc` (JavaScript) and `lein doc` (Clojure).

## License
Copyright © 2015 CityShelf. All rights reserved.
