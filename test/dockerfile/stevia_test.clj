(ns dockerfile.stevia-test
  (:require [dockerfile.stevia :as d]
            [clojure.test :refer [deftest testing is]]))

(deftest basic-test
  (let [expected "FROM eclipse-temurin:17
ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update
ADD target/my_app.jar version.properties* /data/
EXPOSE 9000
CMD cd /data/ && java -cp /data/ -jar my_app.jar"]

    (testing "Hiccup-like syntax"
      (is (= expected
             (d/format
               [[:from "eclipse-temurin:17"]
                [:env "DEBIAN_FRONTEND" "noninteractive"]
                [:run "apt-get update"]
                [:add "target/my_app.jar" "version.properties*" "/data/"]
                [:expose 9000]
                [:cmd
                 ["cd" "/data/"]
                 ["java -cp /data/ -jar my_app.jar"]]]))))

    (testing "Functional syntax"
      (is (= expected
             (-> (d/from "eclipse-temurin:17")
                 (d/env "DEBIAN_FRONTEND" "noninteractive")
                 (d/run "apt-get update")
                 (d/add "target/my_app.jar" "version.properties*" "/data/")
                 (d/expose 9000)
                 (d/cmd ["cd" "/data/"]
                        ["java -cp /data/ -jar my_app.jar"])
                 (d/format)))))))
