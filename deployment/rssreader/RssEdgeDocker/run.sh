java -jar KiekerLogPusher/KiekerLogPusher.jar "$HOSTNAME" /tmp/ 172.16.22.5:8086 mydb kiekerlogs &
java -javaagent:kieker-1.13-aspectj.jar -Dkieker.monitoring.configuration=kieker.monitoring.properties -cp kieker-1.13-aspectj.jar:activemq-all-5.12.0.jar:slf4j-log4j12-1.7.12.jar:log4j-core-2.4.jar:rss-edge.jar com.netflix.recipes.rss.server.EdgeServer
