(ns uap-clj.os
  "Useragent o/s lookup"
  (:require [uap-clj.common :refer [regexes-all first-match field]]
            [clj-yaml.core :refer [parse-string]]
            [clojure.java.io :as io :refer [resource]]
            [clojure.string :as s :refer [join trim]]))

(def regexes (:os_parsers regexes-all))

(def os
  (memoize
    (fn
      [ua]
      (try
        (let [match (first-match ua regexes)
             result (first (flatten (vector (:result match))))]
          (if (= "Other" result)
            {:family "Other" :major nil :minor nil :patch nil :patch_minor nil}
            (let [family (field match :os_replacement 1)
                  major (field match :os_v1_replacement 2)
                  minor (field match :os_v2_replacement 3)
                  patch (field match :os_v3_replacement 4)
                  patch-minor (field match :os_v4_replacement 5)]
              {:family family
               :major major
               :minor minor
               :patch patch
              :patch_minor patch-minor})))
      (catch java.lang.IndexOutOfBoundsException e
        {:family "Other"
         :major nil
         :minor nil
         :patch nil
         :patch_minor nil})))))
