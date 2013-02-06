(ns album.page
  (:require [net.cgrand.enlive-html :as html]))


(html/deftemplate photo-page "page.html"
                  [year album photo prev-photo next-photo]
                  [:title] 
                  (html/content (str (:name photo) " - " album))
                  [[:a (html/attr= :href "..")]]
                  (html/content (str year))
                  [:div.copyright]
                  (html/html-content (str "&copy; Copyright 2006 - " (str year) ". Ashish Shrestha"))
                  [[:link (html/attr= :rel "next")]] 
                  (when (contains? next-photo :link) (html/set-attr :href (:link next-photo)))
                  [[:link (html/attr= :rel "prev")]] 
                  (when (contains? prev-photo :link) (html/set-attr :href (:link prev-photo)))
                  [[:a (html/attr= :rel "up")]] 
                  (html/content album)
                  [[:a (html/attr= :rel "next")]] 
                  (when (contains? next-photo :link) 
                    (html/set-attr :href (:link next-photo) :title (:name next-photo)))
                  [[:img (html/attr= :alt "Next")]]
                  (when (contains? next-photo :link)
                    (html/set-attr :src (:thumbnail next-photo))) 
                  [[:a (html/attr= :rel "prev")]] 
                  (when (contains? prev-photo :link) 
                    (html/set-attr :href (:link prev-photo) :title (:name prev-photo)))
                  [[:img (html/attr= :alt "Previous")]]
                  (when (contains? prev-photo :link)
                    (html/set-attr :src (:thumbnail prev-photo))) 
                  [[:img (html/attr= :rel "self")]]
                  (html/set-attr :src (:image photo) :alt (:name photo) :title (:name photo))
                  [:a.image] 
                  (if (contains? next-photo :link) 
                    (html/set-attr :href (:link next-photo)) 
                    (html/set-attr :href "index.html")))

(defn generate-page 
  [year album prev-photo current-photo next-photo]
  (apply str (photo-page year album current-photo prev-photo next-photo)))

(defn write-page [year album photos]
  (let [pp (first photos)
        cp (nth photos 1)
        np (last photos)]
    (spit (str "target/" (:link cp)) (generate-page year album pp cp np))))

(defn write-pages [year album photos]
  (for [grouped-photos (partition 3 1 (flatten (list {} photos {})))]
    (write-page year album grouped-photos)))

;TODO move these to test files and convert them into proper tests
(defn test-page[]
  (println (generate-page 2013
                          "album"
                          {}
                          {:name "photo.jpg" :image "http://server/img.jpg"} 
                          {:link "http://server/next-img.html" :name "next-img.jpg" :thumbnail "http://server/next-img.jpg"} )))
