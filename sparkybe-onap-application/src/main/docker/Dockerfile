FROM eclipse-temurin:11-jre-alpine
USER nobody

ENV MICRO_HOME=/opt/app/sparky
WORKDIR $MICRO_HOME

COPY --chown=nobody:nobody /maven/sparkybe-onap-application/ .


EXPOSE 8000 8000
EXPOSE 9517 9517

CMD ["/bin/sh", "/opt/app/sparky/bin/start.sh"]
