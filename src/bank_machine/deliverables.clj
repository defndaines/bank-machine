;; Deliverable Construction & Exportation:    -------------------------------------------
(ns bank-machine.deliverables)

;; Functions:    ------------------------------------------------------------------------

(defn export-to-file-account-numbers-with-validation [account-numbers-with-validation]
  "Prints account numbers and their validation results out to the file,
  /results/results_file.txt, one account per line.
  @param seq(vector[string])
  @return nil"
  
  (def results-file "results/results_file.txt")
  (spit
    results-file
    "The account numbers and their respective validation results are as follows:\n"
  )
  (doseq [i (range 0 (count account-numbers-with-validation))]
    (def account-numbers-with-validation-item-to-export (nth account-numbers-with-validation i))
    (spit
      results-file
      (str (get account-numbers-with-validation-item-to-export 0) " " (get account-numbers-with-validation-item-to-export 1) "\n")
      :append true
    )
  )
)
