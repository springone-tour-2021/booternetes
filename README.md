# booternetes


## Build:

`mvn spring-boot:build-image` 

## Run 

## Default Behavior
To run it such that it runs the embedded H2 SQL database: 

`docker run docker.io/library/customers:0.0.1-SNAPSHOT ` 

## Connect to PostgreSQL 

To run it such that it connects to PostgreSQL, you'll need to provide four environment variables: 

* `SPRING_PROFILES_ACTIVE=cloud` 
* `SPRING_R2DBC_URL=r2dbc:postgres://HOST:PORT/SCHEMA`
* `SPRING_R2DBC_USERNAME=username`
* `SPRING_R2DBC_PASSWORD=password`

`docker run -e SPRING_PROFILES_ACTIVE=cloud docker.io/library/customers:0.0.1-SNAPSHOT ` 



