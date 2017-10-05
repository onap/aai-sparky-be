FROM ubuntu:14.04

ARG MICRO_HOME=/opt/app/sparky
ARG BIN_HOME=$MICRO_HOME/bin
ARG HTTP_PROXY
ARG HTTPS_PROXY

ENV HTTP_PROXY  ${HTTP_PROXY}
ENV HTTPS_PROXY ${HTTPS_PROXY}
ENV https_proxy ${HTTPS_PROXY}
ENV http_proxy  ${HTTP_PROXY}

RUN if [ ! -z ${HTTP_PROXY} ]; then echo "Acquire::http::proxy  \"${HTTP_PROXY}\";" >> /etc/apt/apt.conf.d/01proxy; fi && \
    if [ ! -z ${HTTPS_PROXY} ]; then echo "Acquire::https::proxy \"${HTTPS_PROXY}\";" >> /etc/apt/apt.conf.d/01proxy; fi

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
copy swm/package/nix/dist_files/appl/sparky-be/1.0-SNAPSHOT/ $MICRO_HOME/
RUN ls -la $MICRO_HOME/
RUN mkdir -p $BIN_HOME
COPY *.sh $BIN_HOME/
RUN chmod 755 $BIN_HOME/*
RUN ln -s /logs $MICRO_HOME/logs

EXPOSE 8000 8000

CMD tail -F -n0 /etc/hosts
CMD /opt/app/sparky/bin/start.sh
#CMD top
