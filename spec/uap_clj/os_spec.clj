(ns uap-clj.os-spec
  "Test suite for o/s lookup functionality"
  (:require [speclj.core :refer :all]
            [uap-clj.os :refer :all]
            [uap-clj.common-spec :refer [unknown-ua]]
            [clj-yaml.core :refer [parse-string]]
            [clojure.java.io :as io :refer [resource]]
            [clojure.template :refer [do-template]]))

(def tests (:test_cases (parse-string
                          (slurp (io/resource "tests/test_os.yaml")))))

(defn run-os-fixture
  "Assert match between fixture test data:
     OS: family/major/minor/patch/patch_minor
   and output of parser function
  "
  [fixture]
  (let [line (:user_agent_string fixture)
        expected (select-keys fixture
                   [:family :major :minor :patch :patch_minor])
        os (os line)]
  (do-template [family major minor patch patch-minor]
               (describe
                 (format "a user agent '%s' in the '%s' O/S family"
                         line (str family))
                 (it (format "is in the '%s' O/S family" (str family))
                   (should= (str family) (str (:family os))))
                 (it (format "has '%s' as its major number" (str major))
                   (should= (str major) (str (:major os))))
                 (it (format "has '%s' as its minor number" (str minor))
                   (should= (str minor) (str (:minor os))))
                 (it (format "has '%s' as its patch number" (str patch))
                   (should= (str patch) (str (:patch os))))
                 (it (format "has '%s' as its patch_minor number"
                             (str patch-minor))
                   (should= (str patch-minor) (str (:patch_minor os)))))
               (get expected :family "")
               (get expected :major "")
               (get expected :minor "")
               (get expected :patch "")
               (get expected :patch_minor ""))))

(context "Known O/S:"
  (map #(run-os-fixture %) tests))

;;;
;;; The ua-parser core specification requires setting o/s family to "Other"
;;;   and major, minor, patch, and patch minor numbers to nothing if
;;;   an unfamiliar (i.e. not in regexes.yaml) useragent string is encountered.
;;;
(context "Unknown o/s"
  (let [os (os unknown-ua)]
    (describe (format "An as-yet uncataloged o/s '%s'" unknown-ua)
      (it "is categorized as family 'Other'"
        (should= "Other" (str (:family os))))
      (it "has '' as its major number"
        (should= "" (str (:major os))))
      (it "has '' as its minor number"
        (should= "" (str (:minor os))))
      (it "has '' as its patch number"
        (should= "" (str (:patch os))))
      (it "has '' as its patch-minor number"
        (should= "" (str (:patch_minor os)))))))
