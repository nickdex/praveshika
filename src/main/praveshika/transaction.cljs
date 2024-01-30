(ns praveshika.transaction)

(defn make-posting
  ([{:keys [account amount currency comment]}]
   (make-posting account amount currency comment))
  ([account amount currency comment]
   (merge {:account account}
          (when (number? amount)
            {:amount amount})
          (when (and
                 (number? amount)
                 (not-empty currency))
            {:currency currency})
          (when (not-empty comment)
            {:comment comment}))))

(defn make-transaction
  ([transaction]
   (let [{:keys [date payee tag postings]} transaction]
     (make-transaction date payee tag (when postings (map make-posting postings)))))
  ([date payee tag postings]
   {:date date
    :payee payee
    :tag (if (empty? tag) nil tag)
    :postings (vec postings)}))
