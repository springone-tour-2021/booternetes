#!/usr/bin/env bash
docker run \
    -e SPRING_PROFILES_ACTIVE=cloud \
    -e SPRING_R2DBC_URL=r2dbc:postgres://localhost/orders  \
    -e SPRING_R2DBC_USERNAME=jlong \
    -e SPRING_R2DBC_PASSWORD=password
    docker.io/library/customers:0.0.1-SNAPSHOT


