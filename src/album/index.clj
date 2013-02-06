(ns album.index
  (:require [net.cgrand.enlive-html :as html]))

(html/defsnippet thumbnail-model "index.html" [:div.thumbnail]
                 [photo]        
                 [:a](html/set-attr :href (:link photo))
                 [:img] (html/set-attr :src (:thumbnail photo) :title (:name photo) :alt (:name photo)))

(html/deftemplate index-page "index.html"
                  [album photos year]
                  [:title] (html/content album)
                  [[:span (html/attr= :rel "self")]] (html/content album)
                  [[:a (html/attr= :href "..")]](html/content (str year))
                  [:div.copyright](html/content (str "&copy; Copyright 2006 - " (str year) ". Ashish Shrestha"))
                  [:div.thumbnail] (html/clone-for [photo photos] (html/substitute (thumbnail-model photo))))

(defn write-index [filename album-name photos year]
  (spit filename (apply str (index-page album-name photos year))))

; TODO convert to proper tests and move to test files
(def test-photos
  [{:name "photo1.jpg" :link "http://server/photo1.html" :thumbnail "http://server/thumbnail1.jpg"}
   {:name "photo2.jpg" :link "http://server/photo2.html" :thumbnail "http://server/thumbnail2.jpg"}
   {:name "photo3.jpg" :link "http://server/photo3.html" :thumbnail "http://server/thumbnail3.jpg"}
   {:name "photo4.jpg" :link "http://server/photo4.html" :thumbnail "http://server/thumbnail4.jpg"}])

(defn test-index[]
  (println (apply str (index-page "my-album" test-photos 2013))))


