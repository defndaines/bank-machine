(ns bank-machine.core-test
  (:require [clojure.test :refer :all]
            [bank-machine.core :refer :all]
            [clojure.java.io :as io]
  )
)

;; Test File Content Manipulation Functions:    ----------------------------------------------
;;  - Note: These tests are using the live file, /resources/files/machine_file.txt.
;;          Therefore, changes made to the live file can be tested prior to running it to make
;;          sure the file structure is still maintained with the changes in place as well.

(deftest test-get-file-contents
  (testing "Make sure the contents of the machine file get ingested into the system properly:"
    (is (not= (io/resource "files/machine_file.txt") nil) "Is the machine file there?")
    (def test-path (io/resource "files/machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (is (vector? test-contents) "Are the contents read into a vector?")
    (is (not (empty? test-contents)) "Are the contents not empty?")
    (is (> (count test-contents) 3) "Are there at least 3 lines returned?")
  )
)

(deftest test-group-contents-into-account-line-collections
  (testing "Make sure the account line collections are created properly:"
    (def test-path (io/resource "files/machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (is (vector? test-accounts) "Are all the account line collections stored in a vector?")
    (doseq [i (range 0 (count test-accounts))]
      (is
        (vector? (get test-accounts i))
        (str "Is each account line collection stored in a vector as well? (account " i ")")
      )
      (is
        (every? string? (get test-accounts i))
        (str "Are each of the lines in a collection respresented by a string? (account " i ")") 
      )
      (is
        (= (count (get test-accounts i)) 4)
        (str "Are there 4 lines in a collection? (account " i ")") 
      )
      (doseq [j (range 0 3)]
        (is
          (= (count (get (get test-accounts i) j)) 27)
          (str "Are the first 3 lines in each account line collection 27 chars long? (account " i " line " j ")")
        )
      )
      (is
        (empty? (get (get test-accounts i) 3))
        (str "Is the 4th line empty in each account line collection? (account " i ")")
      )
    )
  )
)

(deftest test-group-account-line-into-digit-parts
  (testing "Make sure the account line digit-parts are created properly:"
    (def test-path (io/resource "files/machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (doseq [i (range 0 (count test-accounts))]
      (doseq [j (range 0 3)] ;; skip the 4th row
        (def test-account-line-digit-parts-collection
          (group-account-line-into-digit-parts (get (get test-accounts i) j))
        )
        (is
          (vector? test-account-line-digit-parts-collection)
          (str "Are all the account line digit-part collections stored in a vector? (account " i " line " j ")")
        )
        (is
          (= (count test-account-line-digit-parts-collection) 9)
          (str "Are there 9 digit-parts in the line? (account " i " line " j ")")
        )
        (is
          (every? vector? test-account-line-digit-parts-collection)
          (str "Is each account line digit-part stored in a vector as well? (account " i " line " j ")")
        )
        (doseq [k (range 0 9)]
          (is
            (every? string? (get test-account-line-digit-parts-collection k))
            (str "Is each digit-part respresented by a string? (account " i " line " j " part " k ")")
          )
          (is
            (= (count (get test-account-line-digit-parts-collection k)) 1)
            (str "Is each digit-part just one string? (account " i " line " j " part " k ")")
          )
          (is
            (= (count (get (get test-account-line-digit-parts-collection k) 0)) 3)
            (str "Is each digit-part 3 chars long? (account " i " line " j " part " k ")")
          )
        )
      )
    )
  )
)

(deftest test-make-account-digit-parts-collection
  (testing "Make sure the account digit-parts collection is created properly:"
    (def test-path (io/resource "files/machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (doseq [i (range 0 (count test-accounts))]
      (def test-account-digit-parts-collection (make-account-digit-parts-collection (get test-accounts i)))
      (is
        (vector? test-account-digit-parts-collection)
        (str "Is the account digit-parts collection stored in a vector? (account " i ")")
      )
      (is
        (= (count test-account-digit-parts-collection) 3)
        (str "Are there 3 lines of digit-parts? (account " i ")")
      )
      (doseq [j (range 0 3)]
        (is
          (every? vector? (get test-account-digit-parts-collection j))
          (str "Are the digit-parts in each line also stored in vectors? (account " i " line " j ")")
        )
        (is
          (= (count (get test-account-digit-parts-collection j)) 9)
          (str "Are there 9 digit-parts in the line? (account " i " line " j ")")
        )
        (doseq [k (range 0 9)]
          (is
            (every? string? (get (get test-account-digit-parts-collection j) k))
            (str "Are each of the digit-parts respresented by a string? (account " i " line " j " part " k ")")
          )
          (is
            (= (count (get (get test-account-digit-parts-collection j) k)) 1)
            (str "Is each digit-part just one string? (account " i " line " j " part " k ")")
          )
          (is
            (= (count (get (get (get test-account-digit-parts-collection j) k) 0)) 3)
            (str "Is each digit-part 3 chars long? (account " i " line " j " part " k ")")
          )
        )
      )
    )
  )
)

(deftest test-make-batch-of-account-digit-parts-collections
  (testing "Make sure the batch of account digit-parts collections is created properly:"
    (def test-path (io/resource "files/machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (def test-all-account-digit-parts-collections (make-batch-of-account-digit-parts-collections test-accounts))
    (is
      (vector? test-all-account-digit-parts-collections)
      "Are all of the account digit-parts collections stored in a vector?"
    )
    (is
      (= (count test-all-account-digit-parts-collections) (count test-accounts))
      "Are equally as many account digit-part collections as there are accounts?"
    )
    (is
      (every? vector? test-all-account-digit-parts-collections)
      "Is each account digit-part collection stored in a vector?"
    )
    (doseq [i (range 0 (count test-all-account-digit-parts-collections))]
      (is
        (= (count (get test-all-account-digit-parts-collections i)) 3)
        (str "Are there 3 lines of digit-parts in each collection? (account " i ")")
      )
      (doseq [j (range 0 3)]
        (is
          (every? vector? (get (get test-all-account-digit-parts-collections i) j))
          (str "Are the digit-parts in each line also stored in vectors? (account " i " line " j ")")
        )
        (is
          (= (count (get (get test-all-account-digit-parts-collections i) j)) 9)
          (str "Are there 9 digit-parts in the line? (account " i " line " j ")")
        )
        (doseq [k (range 0 9)]
          (is
            (every? string? (get (get (get test-all-account-digit-parts-collections i) j) k))
            (str "Are each of the digit-parts respresented by a string? (account " i " line " j " part " k ")")
          )
          (is
            (= (count (get (get (get test-all-account-digit-parts-collections i) j) k)) 1)
            (str "Is each digit-part just one string? (account " i " line " j " part " k ")")
          )
          (is
            (= (count (get (get (get (get test-all-account-digit-parts-collections i) j) k) 0)) 3)
            (str "Is each digit-part 3 chars long? (account " i " line " j " part " k ")")
          )
        )
      )
    )
  )
)


;; Test Data Processing & Evaluation Functions:    -------------------------------------------
;;  - Note: These tests are using the test file, /test/files/test_machine_file.txt,
;;      because these tests depend on a consistent unchanged resource so that known expected 
;;      values can be utilized during evaluation.

(deftest test-determine-number-from-digit-parts
  (testing "Make sure the right number is assigned to the 3 digit-parts that up a whole digit:"
    (def test-path (io/file "test/files/test_machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (def test-all-account-digit-parts-collections (make-batch-of-account-digit-parts-collections test-accounts))
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 0) 0) 0)
            (get (get (get test-all-account-digit-parts-collections 0) 1) 0)
            (get (get (get test-all-account-digit-parts-collections 0) 2) 0)
          )
          0
      )
      "Are the digit parts for zero translated to 0?"
    )
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 1) 0) 0)
            (get (get (get test-all-account-digit-parts-collections 1) 1) 0)
            (get (get (get test-all-account-digit-parts-collections 1) 2) 0)
          )
          1
      )
      "Are the digit parts for one translated to 1?"
    )
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 2) 0) 0)
            (get (get (get test-all-account-digit-parts-collections 2) 1) 0)
            (get (get (get test-all-account-digit-parts-collections 2) 2) 0)
          )
          2
      )
      "Are the digit parts for two translated to 2?"
    )
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 3) 0) 0)
            (get (get (get test-all-account-digit-parts-collections 3) 1) 0)
            (get (get (get test-all-account-digit-parts-collections 3) 2) 0)
          )
          3
      )
      "Are the digit parts for three translated to 3?"
    )
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 4) 0) 0)
            (get (get (get test-all-account-digit-parts-collections 4) 1) 0)
            (get (get (get test-all-account-digit-parts-collections 4) 2) 0)
          )
          4
      )
      "Are the digit parts for four translated to 4?"
    )
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 5) 0) 0)
            (get (get (get test-all-account-digit-parts-collections 5) 1) 0)
            (get (get (get test-all-account-digit-parts-collections 5) 2) 0)
          )
          5
      )
      "Are the digit parts for five translated to 5?"
    )
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 6) 0) 0)
            (get (get (get test-all-account-digit-parts-collections 6) 1) 0)
            (get (get (get test-all-account-digit-parts-collections 6) 2) 0)
          )
          6
      )
      "Are the digit parts for six translated to 6?"
    )
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 7) 0) 0)
            (get (get (get test-all-account-digit-parts-collections 7) 1) 0)
            (get (get (get test-all-account-digit-parts-collections 7) 2) 0)
          )
          7
      )
      "Are the digit parts for seven translated to 7?"
    )
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 8) 0) 0)
            (get (get (get test-all-account-digit-parts-collections 8) 1) 0)
            (get (get (get test-all-account-digit-parts-collections 8) 2) 0)
          )
          8
      )
      "Are the digit parts for eight translated to 8?"
    )
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 9) 0) 0)
            (get (get (get test-all-account-digit-parts-collections 9) 1) 0)
            (get (get (get test-all-account-digit-parts-collections 9) 2) 0)
          )
          9
      )
      "Are the digit parts for nine translated to 9?"
    )
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 13) 0) 4)
            (get (get (get test-all-account-digit-parts-collections 13) 1) 4)
            (get (get (get test-all-account-digit-parts-collections 13) 2) 4)
          )
          "?"
      )
      "Are ambiguous digit parts translated to '?' ? (ambiguous 3 or 5)"
    )
    (is
      (=  (determine-number-from-digit-parts
            (get (get (get test-all-account-digit-parts-collections 13) 0) 8)
            (get (get (get test-all-account-digit-parts-collections 13) 1) 8)
            (get (get (get test-all-account-digit-parts-collections 13) 2) 8)
          )
          "?"
      )
      "Are ambiguous digit parts translated to '?' ? (ambiguous 9)"
    )
  )
)

(deftest test-determine-account-number-from-digit-part-collection
  (testing "Make sure the right account number is assigned to the digit-part-collection:"
    (def test-path (io/file "test/files/test_machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (def test-all-account-digit-parts-collections (make-batch-of-account-digit-parts-collections test-accounts))
    (is
      (=  (determine-account-number-from-digit-part-collection
            (get test-all-account-digit-parts-collections 0)
          )
          "000000000"
      )
      "Are digit parts representing zeros placed into an account number properly?"
    )
    (is
      (=  (determine-account-number-from-digit-part-collection
            (get test-all-account-digit-parts-collections 10)
          )
          "123456789"
      )
      "Are digit parts representing one through nine placed into an account number properly?"
    )
    (is
      (=  (determine-account-number-from-digit-part-collection
            (get test-all-account-digit-parts-collections 13)
          )
          "1234?678?"
      )
      "Are ambiguous digit parts placed into an account number properly? (ambiguous 5 and 9)"
    )
    (is
      (=  (determine-account-number-from-digit-part-collection
            (get test-all-account-digit-parts-collections 14)
          )
          "????5???9"
      )
      "Are ambiguous digit parts placed into an account number properly? (multiple potential ambiguous numbers)"
    )
  )
)

(deftest test-determine-account-numbers-from-batch-of-digit-part-collections
  (testing "Make sure the right account number is assigned to all digit-part-collections in a batch:"
    (def test-path (io/file "test/files/test_machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (def test-all-account-digit-parts-collections (make-batch-of-account-digit-parts-collections test-accounts))
    (def test-all-account-numbers (determine-account-numbers-from-batch-of-digit-part-collections test-all-account-digit-parts-collections))
    (is
      (vector? test-all-account-numbers)
      "Are all of the translated account numbers stored in a vector?"
    )
    (is (= (get test-all-account-numbers 0) "000000000")
      "Are digit parts representing zeros placed into an account number properly
      and is the account number represented by a string?"
    )
    (is (= (get test-all-account-numbers 10) "123456789")
      "Are digit parts representing one through nine placed into an account number properly
      and is the account number represented by a string?"
    )
    (is (= (get test-all-account-numbers 13) "1234?678?")
      "Are ambiguous digit parts placed into an account number properly
      and is the account number represented by a string? (ambiguous 5 and 9)"
    )
    (is (= (get test-all-account-numbers 14) "????5???9")
      "Are ambiguous digit parts placed into an account number properly
      and is the account number represented by a string? (multiple potential ambiguous numbers)"
    )
  )
)

(deftest test-illegible-digits-in-account-number?
  (testing "Make sure true or false are accurately returned to indicate the presence of illegible digits in an account number:"
    (def test-path (io/file "test/files/test_machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (def test-all-account-digit-parts-collections (make-batch-of-account-digit-parts-collections test-accounts))
    (def test-all-account-numbers (determine-account-numbers-from-batch-of-digit-part-collections test-all-account-digit-parts-collections))
    (is (= (illegible-digits-in-account-number? (get test-all-account-numbers 10)) false) ;; => 123456789
      "Is false returned when no question marks are present in an account number stored in the vector
      produced by determine-account-numbers-from-batch-of-digit-part-collections?"
    )
    (is (= (illegible-digits-in-account-number? (get test-all-account-numbers 13)) true) ;; => 1234?678?
      "Is true returned when a couple question marks are present in an account number stored in the vector
      produced by determine-account-numbers-from-batch-of-digit-part-collections?"
    )
    (is (= (illegible-digits-in-account-number? (get test-all-account-numbers 14)) true) ;; => ????5???9
      "Is true returned when the majority of the chars are question marks in an account number stored in the vector
      produced by determine-account-numbers-from-batch-of-digit-part-collections?"
    )
    (testing "\nAdditional edge cases:"
      (is (= (illegible-digits-in-account-number? "?23456789") true) "Is true returned when a question mark is the first char?")
      (is (= (illegible-digits-in-account-number? "1234?6789") true) "Is true returned when a question mark is somewhere in the middle?")
      (is (= (illegible-digits-in-account-number? "12345678?") true) "Is true returned when a question mark is the last char?")
      (is (= (illegible-digits-in-account-number? "?234?678?") true) "Is true returned when question marks are at the beginning, middle, and end?")
      (is (= (illegible-digits-in-account-number? "1???????9") true) "Is true returned when question marks fill the middle?")
      (is (= (illegible-digits-in-account-number? "?????????") true) "Is true returned when all chars are question marks?")
    )
  )
)

(deftest test-check-checksum
  (testing "Make sure 'Valid', 'Invalid Checksum', or 'Illegible' are accurately assigned to an account number:"
    (def test-path (io/file "test/files/test_machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (def test-all-account-digit-parts-collections (make-batch-of-account-digit-parts-collections test-accounts))
    (def test-all-account-numbers (determine-account-numbers-from-batch-of-digit-part-collections test-all-account-digit-parts-collections))
    (doseq [i (range 0 (count test-all-account-numbers))]
      (def test-account-validation-result (check-checksum (get test-all-account-numbers i)))
      (is
        (vector? test-account-validation-result)
        (str "Is the account number & validation result pair stored in a vector? " test-account-validation-result)
      )
      (is
        (= (count test-account-validation-result) 2)
        (str "Are only 2 items in the vector - one for account number & one for validation result? " test-account-validation-result)
      )
      (is
        (every? string? test-account-validation-result)
        (str "Are both account number & validation result represented by a string? " test-account-validation-result)
      )
      (is
        (= (count (filter (fn [x] (or (Character/isDigit x) "?")) (get test-account-validation-result 0))) 9)
        (str "Is the first string in the vector an account number? " test-account-validation-result)
      )
      (is
        (not=
          (some
            #(= (get test-account-validation-result 1) %)
            ["Valid" "Invalid Checksum" "Illegible" "Status Unknown"]
          )
          nil
        )
        (str "Is the second string in the vector a valid validation result? " test-account-validation-result)
      )
    )
    (is (= (check-checksum (get test-all-account-numbers 13)) ["1234?678?" "Illegible"])
      "Is 'Illegible' returned if the account number contiains a couple illegible numbers
      that prevent the checksum from being calculated?"
    )
    (is (= (check-checksum (get test-all-account-numbers 14)) ["????5???9" "Illegible"]) 
      "Is 'Illegible' returned if the account number contiains a majority of illegible
      numbers that prevent the checksum from being calculated?"
    )
    (is (= (check-checksum (get test-all-account-numbers 10)) ["123456789" "Valid"])  
      "Is 'Valid' returned if the account number doesn't contain illegible numbers and
      has a checksum equal to 0?"
    )
    (is (= (check-checksum (get test-all-account-numbers 11)) ["000000051" "Valid"])  
      "Is 'Valid' returned if the account number doesn't contain illegible numbers,
      contains zeros, and has a checksum equal to 0?"
    )
    (is (= (check-checksum (get test-all-account-numbers 0)) ["000000000" "Valid"])  
      "Is 'Valid' returned if the account number doesn't contain illegible numbers,
      contains all zeros, and has a checksum equal to 0?"
    )
    (is (= (check-checksum (get test-all-account-numbers 1)) ["111111111" "Invalid Checksum"])  
      "Is 'Invalid Checksum' returned if the account number doesn't contain illegible numbers and
      has a checksum not equal to 0?"
    )
    (is (= (check-checksum (get test-all-account-numbers 2)) ["222222222" "Invalid Checksum"])  
      "Is 'Invalid Checksum' returned if the account number doesn't contain illegible numbers, contains
      2's, and has a checksum not equal to 0?"
    )
    (is (= (check-checksum (get test-all-account-numbers 3)) ["333333333" "Invalid Checksum"]) 
      "Is 'Invalid Checksum' returned if the account number doesn't contain illegible numbers , contains
      3's, and has a checksum not equal to 0?"
    )
    (is (= (check-checksum (get test-all-account-numbers 5)) ["555555555" "Invalid Checksum"]) 
      "Is 'Invalid Checksum' returned if the account number doesn't contain illegible numbers, contains
      5's, and has a checksum not equal to 0?"
    )
    (is (= (check-checksum (get test-all-account-numbers 7)) ["777777777" "Invalid Checksum"])
      "Is 'Invalid Checksum' returned if the account number doesn't contain illegible numbers, contains
      7's, and has a checksum not equal to 0?"
    )
  )
)

(deftest test-validate-batch-of-account-numbers
  (testing "Make sure all account numbers in the batch get evaluated properly:"
    (def test-path (io/file "test/files/test_machine_file.txt"))
    (def test-contents (get-file-contents test-path))
    (def test-accounts (group-contents-into-account-line-collections test-contents))
    (def test-all-account-digit-parts-collections (make-batch-of-account-digit-parts-collections test-accounts))
    (def test-all-account-numbers (determine-account-numbers-from-batch-of-digit-part-collections test-all-account-digit-parts-collections))
    (def test-all-account-numbers-with-validation (validate-batch-of-account-numbers test-all-account-numbers))
    (is
      (seq? test-all-account-numbers-with-validation)
      "Are all of the account number & validation result pairs stored in a sequence?"
    )
    (is
      (every? vector? test-all-account-numbers-with-validation)
      "Is each account number & validation result pair stored in a vector as well?"
    )
    (doseq [i (range 0 (count test-all-account-numbers-with-validation))]
      (def test-all-account-numbers-with-validation-item (nth test-all-account-numbers-with-validation i))
      (is
        (= (count test-all-account-numbers-with-validation-item) 2)
        (str "Are only 2 items in the vector - one for account number & one for validation result? " test-all-account-numbers-with-validation-item)
      )
      (is
        (every? string? test-all-account-numbers-with-validation-item)
        (str "Are both account number & validation result represented by a string? " test-all-account-numbers-with-validation-item)
      )
      (is
        (= (count (filter (fn [x] (or (Character/isDigit x) "?")) (get test-all-account-numbers-with-validation-item 0))) 9)
        (str "Is the first string in the vector an account number? " test-all-account-numbers-with-validation-item)
      )
      (is
        (not=
          (some
            #(= (get test-all-account-numbers-with-validation-item 1) %)
            ["Valid" "Invalid Checksum" "Illegible" "Status Unknown"]
          )
          nil
        )
        (str "Is the second string in the vector a valid validation result? " test-all-account-numbers-with-validation-item)
      )
    )
    (is
      (= (get (nth test-all-account-numbers-with-validation 13) 1) "Illegible")
      "Is the validation result correct for the account number, '1234?678?'?"
    )
    (is
      (= (get (nth test-all-account-numbers-with-validation 14) 1) "Illegible")
      "Is the validation result correct for the account number, '????5???9'?"
    )
    (is
      (= (get (nth test-all-account-numbers-with-validation 0) 1) "Valid")
      "Is the validation result correct for the account number, '000000000'?"
    )
    (is
      (= (get (nth test-all-account-numbers-with-validation 11) 1) "Valid")
      "Is the validation result correct for the account number, '000000051'?"
    )
    (is
      (= (get (nth test-all-account-numbers-with-validation 10) 1) "Valid")
      "Is the validation result correct for the account number, '123456789'?"
    )
    (is
      (= (get (nth test-all-account-numbers-with-validation 1) 1) "Invalid Checksum")
      "Is the validation result correct for the account number, '111111111'?"
    )
    (is
      (= (get (nth test-all-account-numbers-with-validation 2) 1) "Invalid Checksum")
      "Is the validation result correct for the account number, '222222222'?"
    )
    (is
      (= (get (nth test-all-account-numbers-with-validation 3) 1) "Invalid Checksum")
      "Is the validation result correct for the account number, '333333333'?"
    )
    (is
      (= (get (nth test-all-account-numbers-with-validation 5) 1) "Invalid Checksum")
      "Is the validation result correct for the account number, '555555555'?"
    )
    (is
      (= (get (nth test-all-account-numbers-with-validation 7) 1) "Invalid Checksum")
      "Is the validation result correct for the account number, '777777777'?"
    )
  )
)
