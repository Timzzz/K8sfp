FROM hora/recipes-rss-middletier-kieker:0.4

RUN apt-get -y update && apt-get install -y zip && apt-get install -y unzip

WORKDIR /opt/recipes-rss/

ADD run.sh /opt/recipes-rss/run.sh
ADD lib/byteman-download-3.0.6-bin.zip /opt/recipes-rss/lib/byteman-download-3.0.6-bin.zip
ADD lib/*.jar /opt/recipes-rss/lib/
ADD byteman/* /opt/recipes-rss/byteman/

RUN unzip lib/byteman-download-3.0.6-bin.zip

EXPOSE 9191

CMD ["sh", "run.sh"]

