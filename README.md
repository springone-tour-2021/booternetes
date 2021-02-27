# booternetes

```bash
cd customers
```

## Build:

* Build

```bash
./mvnw spring-boot:build-image
```

* Optionally [change the docker](https://spring.io/guides/gs/spring-boot-docker/) repo:

```
./mvnw spring-boot:build-image \
  -Dspring-boot.build-image.imageName=registry.s1t.k8s.camp/s1t/customers:0.0.1-SNAPSHOT
```

## Run

## Default Behavior
To run it such that it runs the embedded H2 SQL database:

```
docker run docker.io/library/customers:0.0.1-SNAPSHOT
```

## Connect to PostgreSQL

To run it such that it connects to PostgreSQL, you'll need to provide four environment variables:

* `SPRING_PROFILES_ACTIVE=cloud`
* `SPRING_R2DBC_URL=r2dbc:postgres://HOST:PORT/SCHEMA`
* `SPRING_R2DBC_USERNAME=username`
* `SPRING_R2DBC_PASSWORD=password`

```
docker run -e SPRING_PROFILES_ACTIVE=cloud docker.io/library/customers:0.0.1-SNAPSHOT
```

### Demo

### Build and push to registry

1. Build:

    ```bash
    ./mvnw spring-boot:build-image \
      -Dspring-boot.build-image.imageName=registry.s1t.k8s.camp/s1t/customers:0.0.1-SNAPSHOT
    ```

1. Push:

    ```bash
    docker push registry.s1t.k8s.camp/s1t/customers:0.0.1-SNAPSHOT
    ```

### Deploy to Kubernetes (no database)

1. Create a namespace:

    ```bash
    kubectl create namespace booternetes
    ```

1. Create a Deployment:

    ```bash
    kubectl -n booternetes create deployment \
      --image=registry.s1t.k8s.camp/s1t/customers:0.0.1-SNAPSHOT customer \
      -o yaml > k8s/manifests/deployment.yaml
    ```

1. Create a Service:

    ```bash
    kubectl -n booternetes expose deployment customer --port=8080 \
      -o yaml > k8s/manifests/service.yaml
    ```

1. Test via port-forward:

    ```bash
    kubectl -n booternetes port-forward deployment/customer 8080:8080 &

1. test readiness / liveness actuators

    ```bash
    curl -s localhost:8080/actuator/health | jq
    curl -s localhost:8080/actuator/health/readiness | jq
    curl -s localhost:8080/actuator/health/liveness | jq
    ```

1. configure readiness / liveness probes (edit k8s/manifests/deployment.yaml)

    ```yaml
    spec:
      terminationGracePeriodSeconds: 30
    containers:
    ...
        env:
          - name: MANAGEMENT_SERVER_PORT
            value: "9001"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 9001
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 9001
    ```

1. apply the new changes

    ```bash
    kubectl -n booternetes apply -f k8s/manifests/deployment.yaml
    ```


1. Deploy a ingress ... WOW MAGIC DNS / TLS

    ```bash
     kubectl -n booternetes create ingress customer --class=default \
      --rule="crm.s1t.k8s.camp/*=customer:8080,tls=crm-secret" \
      --annotation="cert-manager.io/cluster-issuer=letsencrypt-prod" \
      -o yaml > k8s/manifests/ingress.yaml
    ```

### Deploy with a database

1. Configure to use a database by reading the cloud properties into a secret:

    ```bash
    kubectl -n booternetes create secret generic customers --from-file ./src/main/resources/application-cloud.properties
    ```

1. update kube deployment to mount secret as files...

    ```bash
        spec:
          containers:
          ...
            env:
              - name: SPRING_PROFILES_ACTIVE
                value: cloud
            volumeMounts:
              - mountPath: "/config"
                name: config
                readOnly: true
          volumes:
            - name: config
              secret:
                secretName: customers
    ```


## Scripts

I've put `run.sh` and `build.sh` in the `customers` module. Invoke `build.sh` and then `run.sh`.
