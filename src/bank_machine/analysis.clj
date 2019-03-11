;; Data Processing & Evaluation:    -----------------------------------------------------
(ns bank-machine.analysis
  (:require [clojure.string :as str :refer [includes?]])
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


;; Functions:    ------------------------------------------------------------------------

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

  (def count-of-account-digit-parts-collections (count all-account-digit-parts-collections))
  (loop [
          i 0
          all-account-numbers []
        ]
    (if (< i count-of-account-digit-parts-collections)
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
