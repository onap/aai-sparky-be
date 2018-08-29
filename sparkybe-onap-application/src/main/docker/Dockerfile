FROM ubuntu:14.04

ARG MICRO_HOME=/opt/app/sparky
ARG BIN_HOME=$MICRO_HOME/bin

RUN apt-get update

# Install and setup java8
RUN apt-get update && apt-get install -y software-properties-common
## sudo -E is required to preserve the environment. If you remove that line, it will most like freeze at this step
RUN sudo -E add-apt-repository ppa:openjdk-r/ppa && apt-get update && apt-get install -y openjdk-8-jdk
## Setup JAVA_HOME, this is useful for docker commandline
ENV JAVA_HOME usr/lib/jvm/java-8-openjdk-amd64
RUN export JAVA_HOME

# Build up the deployment folder structure
RUN mkdir -p $MICRO_HOME
RUN mkdir -p $BIN_HOME
RUN mkdir -p $MICRO_HOME/config/
RUN mkdir -p $MICRO_HOME/lib/
RUN mkdir -p $MICRO_HOME/static/

ADD *.jar $MICRO_HOME/lib/
ADD scripts/* $MICRO_HOME/bin/
COPY static/ $MICRO_HOME/static/
COPY config/ $MICRO_HOME/config/

COPY oxm $MICRO_HOME/

RUN chmod 755 $MICRO_HOME/bin/*
RUN chmod 755 $MICRO_HOME/lib/*
RUN chmod 755 $MICRO_HOME/static/*
RUN chmod 755 $MICRO_HOME/config/*

RUN ln -s /logs $MICRO_HOME/logs

EXPOSE 8000 8000
EXPOSE 9517 9517
    
CMD ["/opt/app/sparky/bin/start.sh"]
