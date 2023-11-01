(ns dockerfile.stevia
  (:refer-clojure :exclude [comment format])
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

(def add (cons-args-fn :add))
(def arg (cons-args-fn :arg))
(def cmd (cons-args-fn :cmd))
(def comment (cons-args-fn :#))
(def copy (cons-args-fn :copy))
(def entrypoint (cons-args-fn :entrypoint))
(def env (cons-args-fn :env))
(def expose (cons-args-fn :expose))
(def from (cons-args-fn :from))
(def healthcheck (cons-args-fn :healthcheck))
(def label (cons-args-fn :label))
(def onbuild (cons-args-fn :onbuild))
(def run (cons-args-fn :run))
(def shell (cons-args-fn :shell))
(def stopsignal (cons-args-fn :stopsignal))
(def user (cons-args-fn :user))
(def volume (cons-args-fn :volume))
(def workdir (cons-args-fn :workdir))

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
