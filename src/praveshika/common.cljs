(ns praveshika.common)
(defn get-todays-date []
  (let [today (js/Date.)
        year (.getFullYear today)
        month (str (inc (.getMonth today))) ; JavaScript months are 0-indexed
        day (.getDate today)
        formatted-month (if (< (count month) 2) (str "0" month) month)
        formatted-day (if (< (count (str day)) 2) (str "0" day) day)]
    (str year "-" formatted-month "-" formatted-day)))