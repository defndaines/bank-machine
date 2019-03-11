;; Test File Content Manipulation Functions:    ----------------------------------------------
;;  - Note: These tests are using the live file, /resources/files/machine_file.txt.
;;          Therefore, changes made to the live file can be tested prior to running it to make
;;          sure the file structure is still maintained with the changes in place as well.
(ns bank-machine.content-test
  (:require [clojure.test :refer :all]
            [bank-machine.content :refer :all]
            [clojure.java.io :as io :refer [resource]]
  )
)

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
