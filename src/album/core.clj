(ns album.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html])
  (:use [clojure.string :only [split-lines split]]
	[clojure.tools.cli :only [cli]]
        [album.index :only [write-index]]
        [album.page :only [write-pages]])
  (:gen-class :main true))

(defn get-auth-key
  [email passwd]
  (let [response (client/post "https://www.google.com/accounts/ClientLogin" 
                              {:form-params 
                               {:Email email :Passwd passwd :accountType "GOOGLE" :source "clojure album" :service "lh2"}})]
    (if (not= (:status response) 200)
      (throw (Exception. "Unable to get authorization key"))
      (last (split (last (split-lines (:body response))) #"=")))))

(defn replace-end [s old replacement]
  (let [end-index (- (.length s) (.length old))
        part (subs s 0 (if (>= end-index 0) end-index 0))]
    (str part replacement)))

(defn entry->photo [entry]
  (let [name (html/text (first (html/select entry [:title])))
        link (replace-end name "jpg" "html") 
        base-url (:src (:attrs (first (html/select entry [:content]))))
        image (replace-end base-url name (str "s0/" name))
        thumbnail (replace-end base-url name (str "s96-c/" name))]
    (assoc {} :name name :link link :thumbnail thumbnail :image image)))

(defn xml->photos [xml]
  (map entry->photo (html/select (html/xml-resource (java.io.StringReader. xml)) [:entry])))

(defn get-photos-xml
  [album-id auth-key]
  (let [response (client/get (str "https://picasaweb.google.com/data/feed/api/user/default/albumid/" album-id)
                             {:headers {"Authorization" (str "GoogleLogin auth=" auth-key)}})]
    (if (not= (:status response) 200)
      (throw (Exception. "Unable to get photos for album"))
      (:body response))))

(defn write-album [year album-id album-name email password]
  (let [photos (xml->photos (get-photos-xml album-id (get-auth-key email password)))]
    (write-index "target/index.html" album-name photos year)
    (write-pages year album-name photos)))

(defn get-password
  "if password isn't set will ask for it"
  [password]
  (if (nil? password)
    (let [console (. System console)
          pwd (.readPassword console "[%s]" (object-array (list "Password: ")))]
      pwd)
    password))

(defn -main
  "the entry point of the application"
  [& args]
  (let [[opts args banner] (cli args
                    ["-e" "--email" "Email"]
                    ["-p" "--password" "Password"]
        
        
                    ["-y" "--year" "Year" :default "2013"]
                    ["-a" "--album" "Album Id"]
                    ["-d" "--description" "Desription for the album"]
                    ["-h" "--help" "Show help" :flag true :default false])]
    (when (:help opts)
      (println banner)
      (System/exit 0))
    (if (and (:year opts)
             (:album opts)
             (:description opts)
             (:email opts))
        (write-album (:year opts)
               (:album opts)
               (:description opts)
               (:email opts)
               (get-password (:password opts)))
        (println banner))))


