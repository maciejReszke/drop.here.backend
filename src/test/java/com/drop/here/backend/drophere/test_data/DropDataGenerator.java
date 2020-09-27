package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.spot.entity.Spot;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class DropDataGenerator {

    public Drop drop(int i, Route route, Spot spot) {
        return Drop.builder()
                .createdAt(LocalDateTime.now())
                .description("dropDescription" + i)
                .endTime(LocalDateTime.now().plusMinutes(15))
                .name("name" + i)
                .route(route)
                .spot(spot)
                .startTime(LocalDateTime.now())
                .status(DropStatus.PREPARED)
                .uid("dropUid" + i)
                .build();
    }
}
