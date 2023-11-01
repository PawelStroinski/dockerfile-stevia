(ns dockerfile.stevia
  (:refer-clojure :exclude [format])
  (:require [clojure.string :as str]))

(defn- format-line
  [[cmd & args :as _line]]
  (str
    (some-> cmd name str/upper-case)
    " "
    (if (coll? (first args))
      (->> args
           (map (partial str/join " "))
           (str/join " && "))
      (str/join " " args))))

(defn format
  [lines]
  (str/join "\n" (map format-line lines)))

(defn- cons-args-fn
  [cmd]
  (fn [fst & args]
    (if (coll? fst)
      (conj fst (into [cmd] args))
      [(into [cmd fst] args)])))

(def from (cons-args-fn :from))
(def env (cons-args-fn :env))
(def run (cons-args-fn :run))
(def add (cons-args-fn :add))
(def expose (cons-args-fn :expose))
(def cmd (cons-args-fn :cmd))

(format
  [[:from "eclipse-temurin:17"]
   [:env "DEBIAN_FRONTEND" "noninteractive"]
   [:run "apt-get update"]
   [:add "target/my_app.jar" "version.properties*" "/data/"]
   [:expose 9000]
   [:cmd
    ["cd" "/data/"]
    ["java -cp /data/ -jar my_app.jar"]]])

(-> (from "eclipse-temurin:17")
    (env "DEBIAN_FRONTEND" "noninteractive")
    (run "apt-get update")
    (add "target/my_app.jar" "version.properties*" "/data/")
    (expose 9000)
    (cmd ["cd" "/data/"]
         ["java -cp /data/ -jar my_app.jar"])
    (format))
