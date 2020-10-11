package com.drop.here.backend.drophere.route.scheduler;

import com.drop.here.backend.drophere.route.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(value = "route.scheduling.finish.enabled", havingValue = "true")
@Slf4j
public class RouteFinishingScheduler {
    private final RouteService routeService;

    @Scheduled(cron = "${route.scheduling.finish.cron}")
    @SchedulerLock(name = "finishFinishedRoutes",
            lockAtMostFor = "${route.scheduling.finish.lock}",
            lockAtLeastFor = "${route.scheduling.finish.lock}")
    public void finishFinished() {
        routeService.finishToBeFinished();
    }

}
