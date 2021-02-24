FROM @aai.docker.namespace@/aai-common-@aai.base.image@:@aai.base.image.version@

ARG MICRO_HOME=/opt/app/sparky
ARG BIN_HOME=$MICRO_HOME/bin
ARG UI_LOGS=/var/log/onap/AAI-UI

USER root

WORKDIR $MICRO_HOME

COPY --chown=aaiadmin:aaiadmin /maven/sparkybe-onap-application/ .

# Build up the deployment folder structure
RUN mkdir -p $MICRO_HOME $MICRO_HOME/logs $UI_LOGS && \
    chown -R aaiadmin:aaiadmin $MICRO_HOME/logs $UI_LOGS && \
    chmod 755 $BIN_HOME/* && \
    ln -snf /logs $MICRO_HOME/logs

USER aaiadmin

EXPOSE 8000 8000
EXPOSE 9517 9517

CMD ["/opt/app/sparky/bin/start.sh"]
