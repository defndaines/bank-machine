;; File Content Manipulation:    --------------------------------------------------------
(ns bank-machine.content
  (:require [clojure.java.io :as io :refer [reader]])
)

;; Functions:    ------------------------------------------------------------------------

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
