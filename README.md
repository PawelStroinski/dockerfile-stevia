# dockerfile-stevia 🍃

Write your Dockerfiles in Clojure (or Babashka).

The idea is that sometimes it might be beneficial to write [Dockerfiles](https://docs.docker.com/engine/reference/builder/) programmatically to gain composability and code reuse.

Here are some trivial examples of both Hiccup-like and functional syntaxes.

    (require '[dockerfile.stevia :as s])

    (s/format
      [[:from "eclipse-temurin:17"]
       [:env :DEBIAN_FRONTEND :noninteractive]
       [:run "apt-get update"]
       [:add "target/my_app.jar" "version.properties*" "/data/"]
       [:expose 9000]
       [:cmd
        ["cd" "/data/"]
        ["java -cp /data/ -jar my_app.jar"]]])
    =>
    "FROM eclipse-temurin:17
     ENV DEBIAN_FRONTEND noninteractive
     RUN apt-get update
     ADD target/my_app.jar version.properties* /data/
     EXPOSE 9000
     CMD cd /data/ && java -cp /data/ -jar my_app.jar"
    
    # Note how the CMD was automatically &&-ed. 
    
    (-> (s/from "eclipse-temurin:17")
        (s/env :DEBIAN_FRONTEND :noninteractive)
        (s/run "apt-get update")
        (s/add "target/my_app.jar" "version.properties*" "/data/")
        (s/expose 9000)
        (s/cmd ["cd" "/data/"]
               ["java -cp /data/ -jar my_app.jar"])
        (s/format))
    =>
    (same as above)

## Other features

### [Here-Document](https://docs.docker.com/engine/reference/builder/#here-documents)

    (-> (s/run "echo hello
    echo world")
        s/format
        println)
    =>
    RUN <<EOF
    echo hello
    echo world
    EOF

### [Exec form](https://docs.docker.com/engine/reference/builder/#exec-form-entrypoint-example)

    (-> (s/run ["/bin/bash" "-c" "echo hello"])
        s/format
        println)
    =>
    RUN ["/bin/bash", "-c", "echo hello"]

This library has been heavily inspired by [Honey SQL](https://github.com/seancorfield/honeysql).

## License

Copyright © 2023 Paweł Stroiński

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
