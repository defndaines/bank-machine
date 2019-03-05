;; Rachelle Pinckney
;; COMPLETED SOLUTION FOR PARTS 1, 2 & 3 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Note: It pipes out to the console for Part 3.

(ns bank-machine.core
  (:gen-class)
  (:require [clojure.java.io :as io])
)


;; Entities:
(def zero [[\space \_ \space][\| \space \|][\| \_ \|]])
(def one [[\space \space \space][\space \space \|][\space \space \|]])
(def two [[\space \_ \space][\space \_ \|][\| \_ \space]])
(def three [[\space \_ \space][\space \_ \|][\space \_ \|]])
(def four [[\space \space \space][\| \_ \|][\space \space \|]])
(def five [[\space \_ \space][\| \_ \space][\space \_ \|]])
(def six [[\space \_ \space][\| \_ \space][\| \_ \|]])
(def seven [[\space \_ \space][\space \space \|][\space \space \|]])
(def eight [[\space \_ \space][\| \_ \|][\| \_ \|]])
(def nine [[\space \_ \space][\| \_ \|][\space \_ \|]])


;; Functions:
(defn get-file-contents [path]
  (with-open [reader (io/reader path)]
    (binding [*in* reader]
      (doall
        (reduce conj [] (line-seq reader))
      )
    )
  )
)

(defn make-account-collection [contents]
  (def count-of-lines-of-content (count contents))
  (loop [
          i 0
          account-collection []
        ]
    (if (< i count-of-lines-of-content)
      (do
        (def row1 (get contents i))
        (def row2 (get contents (+ i 1)))
        (def row3 (get contents (+ i 2)))
        (def row4 (get contents (+ i 3)))
        (recur (+ i 4)(conj account-collection [row1 row2 row3 row4]))
      )
      account-collection
    )
  )
)

(defn break-account-line-into-digit-parts [account-line]
  (def count-of-chars-in-account-line (count account-line))
  (loop [
          i 0
          account-line-digit-parts-collection []
        ]
    (if (< i count-of-chars-in-account-line)
      (do
        (def part1 (get account-line i))
        (def part2 (get account-line (+ i 1)))
        (def part3 (get account-line (+ i 2)))
        (recur (+ i 3)(conj account-line-digit-parts-collection [part1 part2 part3]))
      )
      account-line-digit-parts-collection
    )
  )
)

(defn make-account-digit-parts-collection [account]
  (loop [
          i 0
          account-digit-parts-collection []
        ]
    (if (< i 3) ;; skip the 4th line
      (do
        (def account-line-digit-parts-collection (break-account-line-into-digit-parts (get account i)))
        (recur (inc i)(conj account-digit-parts-collection account-line-digit-parts-collection))
      )
      account-digit-parts-collection
    )
  )
)

(defn make-all-account-digit-parts-collections [accounts]
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

(defn determine-digit [digit-part1 digit-part2 digit-part3]
  (let [digit [digit-part1 digit-part2 digit-part3]]
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

(defn process-account-number [account-digit-part-collection]
  (def count-of-digits (count (get account-digit-part-collection 1)))
  (loop [
          i 0
          digits []
        ]
    (if (< i count-of-digits)
      (do
        (def digit 
          (determine-digit 
            (get (get account-digit-part-collection 0) i)
            (get (get account-digit-part-collection 1) i)
            (get (get account-digit-part-collection 2) i)
          )
        )
        (recur (inc i)(conj digits digit))
      )
      digits
    )
  )
)

(defn process-all-account-numbers [all-account-digit-parts-collections]
  (def count-of-accounts (count all-account-digit-parts-collections))
  (loop [
          i 0
          all-account-numbers []
        ]
    (if (< i count-of-accounts)
      (do
        (def account-number 
          (process-account-number (get all-account-digit-parts-collections i))
        )
        (recur (inc i)(conj all-account-numbers account-number))
      )
      all-account-numbers
    )
  )
)

(defn check-for-illegible-digits-in-account-number [account-number]
  (count (filter (fn [x] (= x "?")) account-number))
)

(defn check-checksum [account-number]
  (if (= (check-for-illegible-digits-in-account-number account-number) 0)
    (= 
      (mod 
        (+ 
          (* (get account-number 0) 9)
          (* (get account-number 1) 8)
          (* (get account-number 2) 7)
          (* (get account-number 3) 6)
          (* (get account-number 4) 5)
          (* (get account-number 5) 4)
          (* (get account-number 6) 3)
          (* (get account-number 7) 2)
          (* (get account-number 8) 1)
        ) 
        11
      ) 
      0
    )
    "ILL"
  )
)

(defn validate-all-account-numbers [all-account-numbers]
  (map check-checksum all-account-numbers)
)

(defn print-accounts [accounts]
  (def count-of-accounts (count accounts))
  (loop [i 0]
    (if (< i count-of-accounts)
      (do
        (println (get (get accounts i) 0))
        (println (get (get accounts i) 1))
        (println (get (get accounts i) 2))
        (println "")
        (recur (inc i))
      )
    )
  )
)

(defn print-all-account-numbers [all-account-numbers]
  (def count-of-account-numbers (count all-account-numbers))
  (loop [i 0]
    (if (< i count-of-account-numbers)
      (do
        (println (apply str (get all-account-numbers i)))
        (recur (inc i))
      )
    )
  )
)

(defn print-account-number-validation [all-account-numbers all-account-numbers-validation]
  (def count-of-account-numbers (count all-account-numbers))
  (loop [
          i 0
          list-items all-account-numbers-validation
        ]
    (if (< i count-of-account-numbers)
      (do
        (let [x (first list-items)]
          (cond
            (= x "ILL") (println (str (apply str (get all-account-numbers i)) " Illegible"))
            (= x false) (println (str (apply str (get all-account-numbers i)) " Invalid Checksum"))
            (= x true) (println (str (apply str (get all-account-numbers i)) " Valid"))
            :else (println (str (apply str (get all-account-numbers i)) x " Unknown Status"))
          )
        )
        (recur (inc i)(rest list-items))
      )
    )
  )
)

;; Main:
(defn -main []
  (println "Hello! Thanks for using the Bank Machine Translator :)")
  (def path (io/resource "files/machine_file.txt"))
  (def contents (get-file-contents path))
  (def accounts (make-account-collection contents))
  (println "The bank machine's file looks like this:")
  (print-accounts accounts)
  (def all-account-digit-parts-collections (make-all-account-digit-parts-collections accounts))
  (def all-account-numbers (process-all-account-numbers all-account-digit-parts-collections))
  (println "The translated account numbers are:")
  (print-all-account-numbers all-account-numbers)
  (def all-account-numbers-validation (validate-all-account-numbers all-account-numbers))
  (println "The validation results for each account number are:")
  (print-account-number-validation all-account-numbers all-account-numbers-validation)
)
