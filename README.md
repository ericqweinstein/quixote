Quixote
=======

<p align='center'>
  <img src='http://upload.wikimedia.org/wikipedia/en/5/5f/Donquixote.JPG' width='300' />
</p>

## About
Quixote is the search service that powers [CityShelf](http://www.cityshelf.com/), a web application that makes searching for books through local and independent booksellers quick and easy.

## API Endpoints
Content-Type for all requests/responses is application/JSON.

| Method  | Path                 | Response                   |
| ------- | -------------------- | -------------------------- |
| GET     | /books/?field=value  | Books where field = value  |
| GET     | /stores/?field=value | Stores where field = value |

For example,

```bash
λ curl http://localhost:8080/books/?query=Omon+Ra&latitude=40.805135&longitude=-73.964991
```

will return a JSON representation of all books matching the query "Omon Ra" from independent bookstores in New York City, and

```bash
λ curl http://localhost:8080/stores/?latitude=40.805135&longitude=-73.964991
```

will return a JSON representation of bookstores near 40.805135˚N 73.964991˚W.

We currently support Boston, Chicago, Minneapolis, New York, Portland, and Seattle.

## Running Locally
Quixote is a Clojure API (generated via [Enlive](https://github.com/cgrand/enlive), [Liberator](http://clojure-liberator.github.io/liberator/), and [Compojure](https://github.com/weavejester/compojure)). The overall CityShelf application is an isomorphic Clojure(Script) stack; the [Cityshelf repository](https://github.com/ericqweinstein/cityshelf) contains the ClojureScript web client. To run this service locally, you'll need:

* Clojure 1.7
* Leiningen 2

## Installation
1. Clone the repository (`λ git clone git@github.com:ericqweinstein/quixote.git`)
2. Install dependencies (`λ lein deps`)
3. Start the web server on port 8080 (`λ lein run`)

## Deploying
Quixote currently lives on Heroku at http://cityshelf-api.herokuapp.com/.

1. Test (`λ lein midje`)
2. Deploy (`λ git push heroku master`)

## Contributing
1. Branch (`λ git checkout -b fancy-new-feature`)
2. Commit (`λ git commit -m "Fanciness!"`)
3. Lint (`λ lein lint`)
4. Test (`λ lein midje`)
5. Push (`λ git push origin fancy-new-feature`)
6. Ye Olde Pulle Request

## Miscellaneous
You can generate documentation with `λ lein doc`.

## License
Copyright © 2015 CityShelf. All rights reserved.
