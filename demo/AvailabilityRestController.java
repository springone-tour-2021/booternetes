package com.example.customers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class AvailabilityRestController {

    private final ApplicationContext context;

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityRestController.class);

    // Set Liveness health indicator to "DOWN"
    // Kubernetes will restart container when it detects the liveness probe returns down
    @PostMapping("/down")
    void down() {

        AvailabilityChangeEvent.publish(this.context, LivenessState.BROKEN);
    }

    // Set Readiness health indicator to "OUT_OF_SERVICE" for 30 seconds
    // Kubernetes will stop sending trafic when it detects the readiness state is out-of-service,
    //   and resume traffic when it detects the readiness state is up again
    @PostMapping("/pause")
    void pause() {
        logger.warn("Starting system pause (30s)...");
        AvailabilityChangeEvent.publish(this.context, ReadinessState.REFUSING_TRAFFIC);
        try {
            Thread.sleep(30_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AvailabilityChangeEvent.publish(this.context, ReadinessState.ACCEPTING_TRAFFIC);
        logger.info("System pause complete");
    }

}
