#java -jar lib/K8sFpFaultController-0.0.1-SNAPSHOT-jar-with-dependencies.jar "$HOSTNAME" 172.16.84.5:8086 k8sfp 60000 &
java -javaagent:byteman-download-3.0.6/lib/byteman.jar=listener:true,script:/opt/recipes-rss/byteman/memLeak1.btm,boot:/opt/recipes-rss/byteman/byteman/BytemanFIUtils.jar -javaagent:kieker-1.13-aspectj.jar -Dkieker.monitoring.configuration=kieker.monitoring.properties -cp kieker-1.13-aspectj.jar:activemq-all-5.12.0.jar:slf4j-log4j12-1.7.12.jar:log4j-core-2.4.jar:rss-middletier.jar com.netflix.recipes.rss.server.MiddleTierServer

