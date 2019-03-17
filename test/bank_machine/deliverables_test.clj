;; Test Deliverable Construction & Exportation Functions:    ---------------------------------
;;  - Note: These tests are using the test file, /test/files/test_machine_file.txt,
;;      because these tests depend on a consistent unchanged resource so that known expected 
;;      values can be utilized during evaluation.
(ns bank-machine.deliverables-test
  (:require [clojure.test :refer :all]
            [bank-machine.content :refer :all]
            [bank-machine.analysis :refer :all]
            [bank-machine.deliverables :refer :all]
            [clojure.java.io :as io :refer [file]]
  )
)

(deftest test-export-to-file-account-numbers-with-validation
  (testing "Make sure each account number and it's validation result get exported into the results file properly:"
    (def test-path (io/file "test/files/test_machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (def test-all-account-digit-parts-collections (make-batch-of-account-digit-parts-collections test-accounts))
    (def test-all-account-numbers (determine-account-numbers-from-batch-of-digit-part-collections test-all-account-digit-parts-collections))
    (def test-all-account-numbers-with-validation (validate-batch-of-account-numbers test-all-account-numbers))
    (is (= (export-to-file-account-numbers-with-validation test-all-account-numbers-with-validation) nil) "Did the export run successfully?")
    (is (not= (io/file "results/results_file.txt") nil) "Is the results file there?")
    
    (def test-results-file "results/results_file.txt")
    (def test-results-content (slurp test-results-file))
    (is
      (=  (str  "The account numbers and their respective validation results are as follows:\n"
                "000000000 Valid\n"
                "111111111 Invalid Checksum\n"
                "222222222 Invalid Checksum\n"
                "333333333 Invalid Checksum\n"
                "444444444 Invalid Checksum\n"
                "555555555 Invalid Checksum\n"
                "666666666 Invalid Checksum\n"
                "777777777 Invalid Checksum\n"
                "888888888 Invalid Checksum\n"
                "999999999 Invalid Checksum\n"
                "123456789 Valid\n"
                "000000051 Valid\n"
                "49006771? Illegible\n"
                "1234?678? Illegible\n"
                "????5???9 Illegible\n"
          )
          test-results-content
      )
    )
  )
)
