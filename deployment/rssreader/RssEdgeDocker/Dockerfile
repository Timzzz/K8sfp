FROM openjdk:8
#FROM hora/recipes-rss-edge:0.3

RUN apt-get -y update && apt-get install -y zip && apt-get install -y unzip

WORKDIR /opt/recipes-rss/

ADD https://github.com/hora-prediction/recipes-rss-kube/releases/download/0.3/rss-edge-0.3.jar /opt/recipes-rss/rss-edge.jar
ADD KiekerLogPusher/KiekerLogPusher.jar /opt/recipes-rss/KiekerLogPusher/KiekerLogPusher.jar
ADD KiekerLogPusher/lib/*.jar /opt/recipes-rss/KiekerLogPusher/lib
ADD run.sh /opt/recipes-rss/run.sh

ADD https://oss.sonatype.org/service/local/repositories/snapshots/content/net/kieker-monitoring/kieker/1.13-SNAPSHOT/kieker-1.13-20170125.034614-257-aspectj.jar /opt/recipes-rss/kieker-1.13-aspectj.jar
COPY kieker.monitoring.properties /opt/recipes-rss/kieker.monitoring.properties

ADD http://central.maven.org/maven2/org/apache/activemq/activemq-all/5.12.0/activemq-all-5.12.0.jar /opt/recipes-rss/activemq-all-5.12.0.jar
ADD http://central.maven.org/maven2/org/slf4j/slf4j-log4j12/1.7.12/slf4j-log4j12-1.7.12.jar /opt/recipes-rss/slf4j-log4j12-1.7.12.jar
ADD http://central.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.4/log4j-core-2.4.jar /opt/recipes-rss/log4j-core-2.4.jar

COPY aop.xml /opt/recipes-rss/META-INF/aop.xml
RUN zip -r rss-edge.jar META-INF
#RUN unzip rss-edge.jar

EXPOSE 9090 
CMD ["sh", "run.sh"]

