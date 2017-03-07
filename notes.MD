mvn -Dninja.jvmArgs="-DoutputToConsole=true -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" clean compile install  ninja:run -DskipTests -Dninja.mode=dev
sudo docker daemon -D -H tcp://0.0.0.0:2375
MAVEN_OPTS="-DoutputToConsole=true -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" mvn clean compile install  jetty:run -DskipTests
docker volume ls -qf dangling=true | xargs -r docker volume rm