WEBAPI_JAR_FILE = target/weshare-mvc-exercise-1.0-SNAPSHOT-webapi-jar-with-dependencies.jar

.PHONY: all
all: clean build

.PHONY: clean
clean:
	mvn clean

.PHONY: build
build:
	mvn package

.PHONY: build-no-tests
build-no-tests:
	mvn clean package -DskipTests

.PHONY: tests
tests: build
	mvn test

.PHONY: webapi
webapi: build
	java -jar $(WEBAPI_JAR_FILE)

.PHONY: webapi-no-tests
webapi-no-tests: build-no-tests
	java -jar $(WEBAPI_JAR_FILE)

.PHONY: run-webapi
run-webapi: 
	java -jar $(WEBAPI_JAR_FILE) 7000

.PHONY: push
push:
	@read -p "Enter commit message: " msg; \
	git add .; \
	git commit -m "$$msg"; \
	git push