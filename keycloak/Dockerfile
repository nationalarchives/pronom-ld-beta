FROM quay.io/keycloak/keycloak:18.0.2 as builder

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
ENV KC_FEATURES=token-exchange

RUN /opt/keycloak/bin/kc.sh build

EXPOSE 8080

WORKDIR /opt/keycloak
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]

