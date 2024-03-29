(ns locator.main
  (:require [locator.qaplib :as qaplib]
            [locator.sheet :as sheet]
            [locator.sim-anneal :as sim-anneal]
            [locator.solution :as solution]
            [duratom.core :as duratom]))


(defn -main []
  (println "hello!"))


(defn run-once []
  (let [problem (sheet/read-problem)
        solution (sim-anneal/run problem 1000 0.001)
        cost #_(solution/cost solution problem)
        (solution/objects-locations-preferences-cost solution problem)]
    {:best solution
     :cost cost
     :names (:a-labels problem)
     :spots (:b-labels problem)
     :interpreted (solution/interpret-solution solution problem)}))

(comment
  #_(-> (let [problem (qaplib/parse-qaplib-problem "qapdata/nug20.dat")
            solution (sim-anneal/run problem 1000 0.001)
            cost (solution/cost solution problem)]
        {:best solution
         :cost cost})
      (println))

  #_(-> (let [problem (qaplib/parse-qaplib-problem "lekta/hardcoded.dat")
            solution (sim-anneal/run problem 1000 0.001)
            cost (solution/cost solution problem)]
        {:best solution
         :cost cost})
      (println))


  #_(clojure.pprint/pprint (run-once))

  (def results-store (duratom/duratom :local-file
                                      :file-path "results.edn"
                                      :init []))

  (dotimes [_ 30]
    (let [result (run-once)]
      (clojure.pprint/pprint (select-keys result [:cost :interpreted]))
      (swap! results-store conj result)))

  (do
    (println "best results:")
    (clojure.pprint/pprint
     (->> (sort-by :cost @results-store)
          (take 4)
          (map (fn [result] (select-keys result [:interpreted :cost]))))))

  )
