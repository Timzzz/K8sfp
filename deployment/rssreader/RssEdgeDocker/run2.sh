#java -jar KiekerLogPusher/KiekerLogPusher.jar "$HOSTNAME" /tmp/ 10.0.6.56:30343 mydb kiekerlogs &
#java -cp byteman/BytemanFIUtils.jar -javaagent:kieker-1.12-aspectj.jar -Dkieker.monitoring.configuration=kieker.monitoring.properties -javaagent:${BYTEMAN_HOME}/lib/byteman.jar=script:byteman/appmain.btm implementation.Nameserver


#java -javaagent:${BYTEMAN_HOME}/lib/byteman.jar=script:byteman/appmain.btm,boot:/home/tim/docker/springboot/byteman/BytemanFIUtils.jar -cp byteman/BytemanFIUtils.jar:. implementation.Nameserver

java -javaagent:${BYTEMAN_HOME}/lib/byteman.jar=listener:true,boot:/home/tim/docker/springboot/byteman/BytemanFIUtils.jar -cp byteman/BytemanFIUtils.jar:. implementation.Nameserver


