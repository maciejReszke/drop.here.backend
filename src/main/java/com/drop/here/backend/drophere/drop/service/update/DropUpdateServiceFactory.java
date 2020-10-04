package com.drop.here.backend.drophere.drop.service.update;

import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.DropStatusChange;
import com.drop.here.backend.drophere.drop.entity.Drop;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@RequiredArgsConstructor
public class DropUpdateServiceFactory {
    private final DropLiveUpdateService dropLiveUpdateService;
    private final DropCancelledUpdateService dropCancelledUpdateService;
    private final DropDelayedUpdateService dropDelayedUpdateService;
    private final DropFinishedUpdateService dropFinishedUpdateService;

    // TODO: 04/10/2020 test
    public Drop update(Drop drop, DropManagementRequest dropManagementRequest) {
        return API.Match(dropManagementRequest.getNewStatus()).of(
                Case($(DropStatusChange.LIVE), () -> dropLiveUpdateService),
                Case($(DropStatusChange.CANCELLED), () -> dropCancelledUpdateService),
                Case($(DropStatusChange.DELAYED), () -> dropDelayedUpdateService),
                Case($(DropStatusChange.FINISHED), () -> dropFinishedUpdateService))
                .update(drop, dropManagementRequest);
    }
}
