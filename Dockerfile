FROM adoptopenjdk:8-jre@sha256:bc7ad37b3057fbf38b1e3c339e21a5754e7891286133c3bdd7a29b628dd6ac3a

LABEL "maintainer"="Megh"

RUN groupadd container_users && useradd -ms /bin/bash -g container_users achintya

USER achintya

ENV TIMEKEEPER_DIR /home/achintya/app/timekeeper

RUN mkdir -p $TIMEKEEPER_DIR

ADD build/libs/timekeeper-*.jar $TIMEKEEPER_DIR/

WORKDIR $TIMEKEEPER_DIR

CMD java -jar timekeeper-*.jar