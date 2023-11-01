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

(defn- map-arg
  [m]
  (->> m
       (map (fn [[k v]] (str "--" (name k)
                             (when-not (true? v) (str "=" (format-arg v))))))
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

(defn- make-cmd-fn
  [cmd]
  (fn [fst & args]
    (if (and (coll? (first fst))
             (not (map? fst)))
      (conj fst (into [cmd] args))
      [(into [cmd fst] args)])))

(def add (make-cmd-fn :add))
(def arg (make-cmd-fn :arg))
(def cmd (make-cmd-fn :cmd))
(def comment (make-cmd-fn :#))
(def copy (make-cmd-fn :copy))
(def entrypoint (make-cmd-fn :entrypoint))
(def env (make-cmd-fn :env))
(def expose (make-cmd-fn :expose))
(def from (make-cmd-fn :from))
(def healthcheck (make-cmd-fn :healthcheck))
(def label (make-cmd-fn :label))
(def onbuild (make-cmd-fn :onbuild))
(def run (make-cmd-fn :run))
(def shell (make-cmd-fn :shell))
(def stopsignal (make-cmd-fn :stopsignal))
(def user (make-cmd-fn :user))
(def volume (make-cmd-fn :volume))
(def workdir (make-cmd-fn :workdir))
