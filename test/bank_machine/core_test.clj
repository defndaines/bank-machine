;; Test Display Helper Functions:    ----------------------------------------------------
;;  - Note: These tests are using the test file, /test/files/test_machine_file.txt,
;;      because these tests depend on a consistent unchanged resource so that known expected 
;;      values can be utilized during evaluation.
(ns bank-machine.core-test
  (:require [clojure.test :refer :all]
            [bank-machine.content :refer :all]
            [bank-machine.analysis :refer :all]
            [bank-machine.core :refer :all]
            [clojure.java.io :as io :refer [file]]
  )
)

(deftest test-print-machine-version-of-accounts
  (testing "Make sure the machine version of accounts displays in the proper layout in the console:"
    (def test-path (io/file "test/files/test_machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (is
      (=  (str  " _  _  _  _  _  _  _  _  _ \n"
                "| || || || || || || || || |\n"
                "|_||_||_||_||_||_||_||_||_|\n"
                "\n"
                "                           \n"
                "  |  |  |  |  |  |  |  |  |\n"
                "  |  |  |  |  |  |  |  |  |\n"
                "\n"
                " _  _  _  _  _  _  _  _  _ \n"
                " _| _| _| _| _| _| _| _| _|\n"
                "|_ |_ |_ |_ |_ |_ |_ |_ |_ \n"
                "\n"
          )
          (with-out-str (print-machine-version-of-accounts (subvec test-accounts 0 3)))
      )
    )
  )
)

(deftest test-print-account-numbers
  (testing ""
    (def test-path (io/file "test/files/test_machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (def test-all-account-digit-parts-collections (make-batch-of-account-digit-parts-collections test-accounts))
    (def test-all-account-numbers (determine-account-numbers-from-batch-of-digit-part-collections test-all-account-digit-parts-collections))
    (is
      (=  (str  "000000000\n"
                "111111111\n"
                "222222222\n"
                "333333333\n"
                "444444444\n"
                "555555555\n"
                "666666666\n"
                "777777777\n"
                "888888888\n"
                "999999999\n"
                "123456789\n"
                "000000051\n"
                "49006771?\n"
                "1234?678?\n"
                "????5???9\n"
          )
          (with-out-str (print-account-numbers test-all-account-numbers))
      )
    )
  )
)

(deftest test-print-account-numbers-with-validation
  (testing "Make sure the account numbers and their respective validation status display in the
            proper layout in the console:"
    (def test-path (io/file "test/files/test_machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (def test-all-account-digit-parts-collections (make-batch-of-account-digit-parts-collections test-accounts))
    (def test-all-account-numbers (determine-account-numbers-from-batch-of-digit-part-collections test-all-account-digit-parts-collections))
    (def test-all-account-numbers-with-validation (validate-batch-of-account-numbers test-all-account-numbers))
    (is
      (=  (str  "000000000 Valid\n"
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
          (with-out-str (print-account-numbers-with-validation test-all-account-numbers-with-validation))
      )
    )
  )
)
