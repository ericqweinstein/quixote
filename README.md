Quixote
=======

<p align='center'>
  <img src='http://upload.wikimedia.org/wikipedia/en/5/5f/Donquixote.JPG' width='300' />
</p>

## About
Quixote is the search service that powers [CityShelf](http://www.cityshelf.com/), a web application that makes searching for books through local and independent booksellers quick and easy.

## API Version 1 Endpoints
Content-Type for all requests/responses is application/JSON.

| Method  | Path                          | Response                                     |
| ------- | ----------------------------- | -------------------------------------------- |
| GET     | /api/stores/:id/?field=value  | Books where store ID = id and field = value  |

For example,

```bash
λ curl http://localhost:8080/api/stores/0/?query=Omon+Ra
```

will return a JSON representation of all books matching the query "Omon Ra" from Astoria Bookshop (ID #0).

## API Version 2 Endpoints
Content-Type for all requests/responses is application/JSON.

| Method  | Path                 | Response                   |
| ------- | -------------------- | -------------------------- |
| GET     | /books/?field=value  | Books where field = value  |

For example,

```bash
λ curl http://localhost:8080/books/?query=Omon+Ra&latitude=40.805135&longitude=-73.964991
```

will return a JSON representation of all books matching the query "Omon Ra" from independent bookstores in New York City.

Version 1 endpoints were designed for New York City; version 2 endpoints enable search across the country. We currently support Boston, Chicago, Minneapolis, New York, Portland, and Seattle.

## Running Locally
CityShelf is currently an Angular SPA with a Clojure API (generated via [Enlive](https://github.com/cgrand/enlive), [Liberator](http://clojure-liberator.github.io/liberator/), and [Compojure](https://github.com/weavejester/compojure)). You'll need the following:

### Client Application
* NodeJS + NPM (Node Package Manager)
* Grunt (`λ npm install`)

### API
* Clojure 1.5+
* Leiningen 2

## Installation
1. Clone the repository (`λ git clone git@github.com:ericqweinstein/cityshelf.git`)
2. Install client dependencies (`λ npm install`)
3. Install API dependencies (`λ lein deps`)
4. Start the web server on port 8080 (`λ lein run`)

## Deploying
CityShelf currently lives on Heroku at http://cityshelf.herokuapp.com/.

1. Lint/test the client application, build/minify CSS & JS, &c (`λ grunt`)
2. Test the API (`λ lein midje`)
3. Deploy (`λ git push heroku master`)

## Contributing
1. Branch (`λ git checkout -b fancy-new-feature`)
2. Commit (`λ git commit -m "Fanciness!"`)
3. Lint and test the client (`λ grunt`)
4. Lint the API (`λ lein lint`)
5. Test the API (`λ lein midje`)
6. Push (`λ git push origin fancy-new-feature`)
7. Ye Olde Pulle Request

## Miscellaneous
You can generate documentation with `λ grunt jsdoc` (JavaScript) and `λ lein doc` (Clojure).

## License
Copyright © 2014 - 2015 CityShelf. All rights reserved.
