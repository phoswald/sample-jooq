# sample-jooq

Experiments with JOOQ and Docker, featuring:

- Static web content
- Dynamic web content using Thymeleaf
- REST endpoints
- H2 database using JOOQ

## Run Standalone

~~~
$ mvn clean verify
$ export APP_SAMPLE_CONFIG=ValueFromShell
$ java \
  -cp $(echo target/sample-jooq-*-dist/lib)/"*" \
  -Dapp.http.port=8080 \
  -Dapp.jdbc.url=jdbc:h2:./databases/task-db \
  com.github.phoswald.sample.Application
~~~

## Run with Docker

~~~
$ mvn clean verify -P docker
$ docker run -it --name sample-jooq --rm \
  -p 8080:8080 \
  -e APP_SAMPLE_CONFIG=ValueFromDockerRun \
  -e APP_JDBC_URL=jdbc:h2:/databases/task-db \
  -v ./databases/:/databases \
  sample-jooq:0.1.0-SNAPSHOT
~~~

# URLs

- http://localhost:8080/

~~~
$ curl 'http://localhost:8080/app/rest/sample/time' -i
$ curl 'http://localhost:8080/app/rest/sample/config' -i
$ curl 'http://localhost:8080/app/rest/sample/echo-xml' -i -X POST \
  -H 'content-type: text/xml' \
  -d '<echoRequest><input>This is CURL</input></echoRequest>'
$ curl 'http://localhost:8080/app/rest/sample/echo-json' -i -X POST \
  -H 'content-type: application/json' \
  -d '{"input":"This is CURL"}'
$ curl 'http://localhost:8080/app/rest/tasks' -i
$ curl 'http://localhost:8080/app/rest/tasks' -i -X POST \
  -H 'content-type: application/json' \
  -d '{"title":"Some task","description":"This is CURL","done":true}'
$ curl 'http://localhost:8080/app/rest/tasks/5b89f266-c566-4d1f-8545-451bc443cf26' -i
$ curl 'http://localhost:8080/app/rest/tasks/5b89f266-c566-4d1f-8545-451bc443cf26' -i -X PUT \
  -H 'content-type: application/json' \
  -d '{"title":"Some updated task","description":"This is still CURL","done":false}'
$ curl 'http://localhost:8080/app/rest/tasks/5b89f266-c566-4d1f-8545-451bc443cf26' -i -X DELETE
~~~

# Database setup

1.  Start H2 with `$ java -jar target/lib/h2-2.1.214.jar` and open UI in browser.
2.  Open database `jdbc:h2:./databases/task-db` with username `sa` password `sa`.
3.  Execute script `src/main/resources/schema.sql`.

