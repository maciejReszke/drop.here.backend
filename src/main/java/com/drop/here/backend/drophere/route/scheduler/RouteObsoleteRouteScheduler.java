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
@ConditionalOnProperty(value = "route.scheduling.obsolete.enabled", havingValue = "true")
@Slf4j
public class RouteObsoleteRouteScheduler {
    private final RouteService routeService;

    @Scheduled(cron = "${route.scheduling.obsolete.cron}")
    @SchedulerLock(name = "finishObsoleteRoutes",
            lockAtMostFor = "${route.scheduling.obsolete.lock}",
            lockAtLeastFor = "${route.scheduling.obsolete.lock}")
    public void finishObsolete() {
        routeService.finishObsolete();
    }
}
