FROM aaionap/aai-common:@aai.docker.version@-@aai.base.image@

ARG MICRO_HOME=/opt/app/sparky
ARG BIN_HOME=$MICRO_HOME/bin

# Build up the deployment folder structure
RUN mkdir -p $MICRO_HOME
WORKDIR $MICRO_HOME
COPY /maven/sparkybe-onap-application/ .
RUN chmod 755 $BIN_HOME/* \
    && ln -snf /logs $MICRO_HOME/logs

EXPOSE 8000 8000
EXPOSE 9517 9517
    
CMD ["/opt/app/sparky/bin/start.sh"]
