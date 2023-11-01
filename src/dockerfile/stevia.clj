(ns dockerfile.stevia
  (:refer-clojure :exclude [comment format])
  (:require [clojure.string :as str]))

(defn- format-arg
  [arg]
  (cond
    (and (string? arg)
         (str/includes? arg "\n"))
    (str "<<EOF\n" arg "\nEOF")

    (keyword? arg)
    (name arg)

    :else
    arg))

(defn map-arg
  [m]
  (->> m
       (map (fn [[k v]] (str "--" (name k) "=" (format-arg v))))
       (str/join " ")))

(defn- and-if
  [args]
  (->> args
       (map #(str/join " " (map format-arg %)))
       (str/join " && ")))

(defn- exec-form
  [args]
  (->> (first args)
       (map format-arg)
       (map pr-str)
       (str/join ", ")
       (#(str \[ % \]))))

(defn- format-args
  [args]
  (cond
    (map? (first args)) (str (-> args first map-arg) " " (-> args rest format-args))
    (coll? (second args)) (and-if args)
    (coll? (first args)) (exec-form args)
    :else (str/join " " (map format-arg args))))

(defn- format-line
  [[cmd & args :as _line]]
  (str
    (some-> cmd name str/upper-case)
    " "
    (format-args args)))

(defn format
  [lines]
  (str/join "\n" (map format-line lines)))

(defn- cons-args-fn
  [cmd]
  (fn [fst & args]
    (if (and (coll? (first fst))
             (not (map? fst)))
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
