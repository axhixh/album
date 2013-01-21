# album-enlive

A Clojure script used to generate photo albums at http://www.axhixh.com.np

## Usage
Used inside repl.

lein repl

(load "album/core")
(in-ns 'album.core)
(write-album "albumid" "album title" "email@gmail.com" "password")

Generates HTML files in target directory.

## License

Copyright © 2012 Ashish Shrestha

Distributed under the Eclipse Public License, the same as Clojure.
