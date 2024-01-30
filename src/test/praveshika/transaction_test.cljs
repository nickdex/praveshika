(ns praveshika.transaction-test
  (:require [clojure.test :refer [deftest is testing]]
            [praveshika.transaction :as t]))

(deftest make-posting-test
  (testing "Skip amount and currency if amount is not number"
    (is (= {:account "Assets:Cash"
            :comment "Some Comment"}
           (t/make-posting "Assets:Cash" "30" "INR" "Some Comment")))))

(deftest make-posting-amount-test
  (testing "Positive Test with amount as number type"
    (is (= {:account "Assets:Cash"
            :amount 30
            :currency "INR"
            :comment "Some Comment"}
           (t/make-posting "Assets:Cash" 30 "INR" "Some Comment")))))

(deftest make-posting-account-only-test
  (testing "Only account value is provided"
    (is (= {:account "Assets:Cash"}
           (t/make-posting "Assets:Cash" nil nil nil)))))

(deftest make-transaction-empty-data
  (testing "Positive Test"
    (is (= {:date "2024/01/03" ,
            :payee "Swiggy",
            :tag nil,
            :postings [{:account ""}]}
           (t/make-transaction "2024/01/03" "Swiggy" nil [(t/make-posting "" nil nil nil)])))))