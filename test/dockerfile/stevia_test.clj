(ns dockerfile.stevia-test
  (:require [clojure.test :refer [deftest is testing]]
            [dockerfile.stevia :as s]))

(deftest basic-test
  (let [expected "FROM eclipse-temurin:17
ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update
ADD target/my_app.jar version.properties* /data/
EXPOSE 9000
CMD cd /data/ && java -cp /data/ -jar my_app.jar"]

    (testing "Hiccup-like syntax"
      (is (= expected
             (s/format
               [[:from "eclipse-temurin:17"]
                [:env :DEBIAN_FRONTEND :noninteractive]
                [:run "apt-get update"]
                [:add "target/my_app.jar" "version.properties*" "/data/"]
                [:expose 9000]
                [:cmd
                 ["cd" "/data/"]
                 ["java -cp /data/ -jar my_app.jar"]]]))))

    (testing "Functional syntax"
      (is (= expected
             (-> (s/from "eclipse-temurin:17")
                 (s/env :DEBIAN_FRONTEND :noninteractive)
                 (s/run "apt-get update")
                 (s/add "target/my_app.jar" "version.properties*" "/data/")
                 (s/expose 9000)
                 (s/cmd ["cd" "/data/"]
                        ["java -cp /data/ -jar my_app.jar"])
                 (s/format)))))))

(deftest here-document
  (is (= "RUN <<EOF\necho hello\necho world\nEOF"
         (s/format (s/run "echo hello\necho world")))))

(deftest exec-form
  (is (= "RUN [\"/bin/bash\", \"-c\", \"echo hello\"]"
         (s/format (s/run ["/bin/bash" "-c" "echo hello"])))))

(deftest map-argument
  (is (= "ADD --chown=myuser:mygroup --chmod=655 files* /somedir/"
         (s/format (s/add {:chown :myuser:mygroup :chmod 655} "files*" "/somedir/")))))
