;; Rachelle Pinckney
;; COMPLETED SOLUTION FOR PARTS 1, 2, & 3

(ns bank-machine.core
  (:gen-class)
  (:require [bank-machine.content :refer :all]
            [bank-machine.analysis :refer :all]
            [bank-machine.deliverables :refer :all]
            [clojure.java.io :as io :refer [resource]]
            [clojure.string :as str :refer [join]]
  )
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

(defn print-account-numbers [account-numbers]
  "Prints the account numbers out to the console, one per each line.
  @param vector[string]
  @return nil"
  
  (println (str/join "\n" account-numbers))
)

(defn print-account-numbers-with-validation [account-numbers-with-validation]
  "Prints account numbers and their validation results out to the console, one account per line.
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
  (print-account-numbers all-account-numbers)
  
  (println "The validation results for each account number are:")
  (def all-account-numbers-with-validation (validate-batch-of-account-numbers all-account-numbers))
  (print-account-numbers-with-validation all-account-numbers-with-validation)
  
  (export-to-file-account-numbers-with-validation all-account-numbers-with-validation)
  (println "These results have also been exported to /results/results_file.txt for your convenience. :)")
  
)
