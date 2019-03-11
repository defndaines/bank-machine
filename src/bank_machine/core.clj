;; Rachelle Pinckney
;; COMPLETED SOLUTION FOR PARTS 1, 2 & 3
;; Note: For Part 3, the system pipes out to the console instead of to a file.

(ns bank-machine.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
  )
)


;; Entities:    -------------------------------------------------------------------------
(def zero [
            [" _ "]
            ["| |"]
            ["|_|"]
          ]
)
(def one  [
            ["   "]
            ["  |"]
            ["  |"]
          ]
)
(def two  [
            [" _ "]
            [" _|"]
            ["|_ "]
          ]
)
(def three  [
              [" _ "]
              [" _|"]
              [" _|"]
            ]
)
(def four [
            ["   "]
            ["|_|"]
            ["  |"]
          ]
)
(def five [
            [" _ "]
            ["|_ "]
            [" _|"]
          ]
)
(def six  [
            [" _ "]
            ["|_ "]
            ["|_|"]
          ]
)
(def seven  [
              [" _ "]
              ["  |"]
              ["  |"]
            ]
)
(def eight  [
              [" _ "]
              ["|_|"]
              ["|_|"]
            ]
)
(def nine [
            [" _ "]
            ["|_|"]
            [" _|"]
          ]
)


;; File Content Manipulation Functions:    ----------------------------------------------

(defn get-file-contents [path]
  (with-open [reader (io/reader path)]
    (binding [*in* reader]
      (doall
        (reduce conj [] (line-seq reader))
      )
    )
  )
)

(defn group-contents-into-account-line-collections [contents]
  "Each account is represented by 4 consecutive lines in the machine generated file.
  This method organizes the contents provided into groups of 4 lines in order to mimic
  this structure.
  @param vector[string]
  @return vector[vector[string]]"

  (def count-of-lines-of-content (count contents))
  (loop [
          i 0
          account-line-collection []
        ]
    (if (< i count-of-lines-of-content)
      (do
        (def row1 (get contents i))
        (def row2 (get contents (+ i 1)))
        (def row3 (get contents (+ i 2)))
        (def row4 (get contents (+ i 3)))
        (recur (+ i 4)(conj account-line-collection [row1 row2 row3 row4]))
      )
      account-line-collection
    )
  )
)

(defn group-account-line-into-digit-parts [account-line]
  "In an account line, each group of 3 chars represents a portion of a digit.
  This method organizes an account line into groups of 3 chars in order to mimic
  this structure.
   - Note: As per directives for this project, it is safe to assume 27 chars per line, i.e.
     9 digits per account number.
  @param string
  @return vector[vector[string]]"


  (def count-of-chars-in-account-line 27)
  (loop [
          i 0
          account-line-digit-parts-collection []
        ]
    (if (< i count-of-chars-in-account-line)
      (do
        (def char1 (str (get account-line i)))
        (def char2 (str (get account-line (+ i 1))))
        (def char3 (str (get account-line (+ i 2))))
        (recur (+ i 3)(conj account-line-digit-parts-collection [(str char1 char2 char3)]))
      )
      account-line-digit-parts-collection
    )
  )
)

(defn make-account-digit-parts-collection [account]
  "An account consists of 4 lines, such that each group of 3 chars in a line represents
  a portion of a digit. This method organizes the lines of an account into lines
  containing groups of 3 chars in order to mimic this structure.
   - Note: The 4th line can be ignored in the organization processs since it is empty.
  @param vector[string]
  @return vector[vector[vector[string]]]"

  (loop [
          i 0
          account-digit-parts-collection []
        ]
    (if (< i 3) ;; skip the 4th line
      (do
        (def account-line-digit-parts-collection (group-account-line-into-digit-parts (get account i)))
        (recur (inc i)(conj account-digit-parts-collection account-line-digit-parts-collection))
      )
      account-digit-parts-collection
    )
  )
)

(defn make-batch-of-account-digit-parts-collections [accounts]
  "An account consists of 4 lines, such that each group of 3 chars in a line represents
  a portion of a digit. This method organizes the lines for a group of accounts into lines
  containing groups of 3 chars in order to mimic this structure.
   - Note: The 4th line can be ignored in the organization processs since it is empty.
  @param vector[vector[string]]
  @return vector[vector[vector[vector[string]]]]"

  (def count-of-accounts (count accounts))
  (loop [
          i 0
          all-account-digit-parts-collections []
        ]
    (if (< i count-of-accounts)
      (do
        (def account-digit-parts-collection (make-account-digit-parts-collection (get accounts i)))
        (recur (inc i)(conj all-account-digit-parts-collections account-digit-parts-collection))
      )
      all-account-digit-parts-collections
    )
  )
)


;; Data Processing & Evaluation Functions:    -------------------------------------------

(defn determine-number-from-digit-parts [digit-top digit-middle digit-bottom]
  "From the provided digit-part's (top, middle, & bottom), determine which
  number they correspond to, as per the Entities defined above.
  @params vector[string] vector[string] vector[string]
  @return integer|string"
  
  (let [digit [digit-top digit-middle digit-bottom]]
    (cond
      (= digit zero) 0
      (= digit one) 1
      (= digit two) 2
      (= digit three) 3
      (= digit four) 4
      (= digit five) 5
      (= digit six) 6
      (= digit seven) 7
      (= digit eight) 8
      (= digit nine) 9
      :else "?"
    )
  )
)

(defn determine-account-number-from-digit-part-collection [account-digit-part-collection]
  "Translate an account-digit-part-collection into an actual account number
   - Note: As per directives for this project, it is safe to assume 27 chars per line, i.e.
     9 digits per account number.
  @param vector[vector[vector[string]]]
  @return string"

  (def count-of-digits 9)
  (loop [
          i 0
          account-number ""
        ]
    (if (< i count-of-digits)
      (do
        (def number 
          (determine-number-from-digit-parts 
            (get (get account-digit-part-collection 0) i)
            (get (get account-digit-part-collection 1) i)
            (get (get account-digit-part-collection 2) i)
          )
        )
        (recur (inc i)(str account-number number))
      )
      account-number
    )
  )
)

(defn determine-account-numbers-from-batch-of-digit-part-collections [all-account-digit-parts-collections]
  "Translate all account-digit-part-collection's in the batch into actual account numbers.
  @param vector[vector[vector[vector[string]]]]
  @return vector[string]"

  (def count-of-accounts (count all-account-digit-parts-collections))
  (loop [
          i 0
          all-account-numbers []
        ]
    (if (< i count-of-accounts)
      (do
        (def account-number 
          (determine-account-number-from-digit-part-collection (get all-account-digit-parts-collections i))
        )
        (recur (inc i)(conj all-account-numbers account-number))
      )
      all-account-numbers
    )
  )
)

(defn illegible-digits-in-account-number? [account-number]
  "@param string
  @return boolean"

  (str/includes? account-number "?")
)

(defn check-checksum [account-number]
  "If the checksum can be calculated, determine whether the checksum is valid or not.
  @param vector[string]
  @return string"
 
  (def validation-result (conj [] account-number))
  (if (not (illegible-digits-in-account-number? account-number))
    (if (= 
          (mod 
            (+ 
              (* (Character/digit (get account-number 0) 10) 9)
              (* (Character/digit (get account-number 1) 10) 8)
              (* (Character/digit (get account-number 2) 10) 7)
              (* (Character/digit (get account-number 3) 10) 6)
              (* (Character/digit (get account-number 4) 10) 5)
              (* (Character/digit (get account-number 5) 10) 4)
              (* (Character/digit (get account-number 6) 10) 3)
              (* (Character/digit (get account-number 7) 10) 2)
              (* (Character/digit (get account-number 8) 10) 1)
            ) 
            11
          ) 
          0
        )
      (conj validation-result "Valid")
      (conj validation-result "Invalid Checksum")
    )
    (conj validation-result "Illegible")
  )
)

(defn validate-batch-of-account-numbers [all-account-numbers]
  "Determine whether each account number in the batch is valid or not.
  @param vector[vector[string]]
  @return seq(string)"
    
  (map check-checksum all-account-numbers)
)


;; Display Helper Functions:    ---------------------------------------------------------

(defn print-machine-version-of-accounts [accounts]
  "Prints the machine display version accounts out to the console.
  @param vector[vector[string]]
  @return nil"

  (doseq [i (range 0 (count accounts))]
    (println (get (get accounts i) 0))
    (println (get (get accounts i) 1))
    (println (get (get accounts i) 2))
    (println "")
  )
)

(defn print-account-numbers-with-validation [account-numbers-with-validation]
  "Prints account numbers and their validation results out to the console.
  @param seq(vector[string])
  @return nil"
  
  (doseq [i (range 0 (count account-numbers-with-validation))]
    (def account-numbers-with-validation-item (nth account-numbers-with-validation i))
    (println (get account-numbers-with-validation-item 0) (get account-numbers-with-validation-item 1))
  )
)


;; Main:    ---------------------------------------------------------------------------------------
(defn -main []
  (println "Hello! Thanks for using the Bank Machine Translator :)")
  
  (println "The bank machine's file looks like this:")
  (def path (io/resource "files/machine_file.txt"))
  (def contents (get-file-contents path))
  (def accounts (group-contents-into-account-line-collections contents))
  (print-machine-version-of-accounts accounts)
  
  (println "The translated account numbers are:")
  (def all-account-digit-parts-collections (make-batch-of-account-digit-parts-collections accounts))
  (def all-account-numbers (determine-account-numbers-from-batch-of-digit-part-collections all-account-digit-parts-collections))
  (println (str/join "\n" all-account-numbers))
  
  (println "The validation results for each account number are:")
  (def all-account-numbers-with-validation (validate-batch-of-account-numbers all-account-numbers))
  (print-account-numbers-with-validation all-account-numbers-with-validation)
)
