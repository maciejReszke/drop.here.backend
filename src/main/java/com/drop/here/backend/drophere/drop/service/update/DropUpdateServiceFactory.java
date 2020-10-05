package com.drop.here.backend.drophere.drop.service.update;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.DropStatusChange;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.spot.entity.Spot;
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

    public DropStatus update(Drop drop, Spot spot, Company company, AccountProfile profile, DropManagementRequest dropManagementRequest) {
        return API.Match(dropManagementRequest.getNewStatus()).of(
                Case($(DropStatusChange.LIVE), () -> dropLiveUpdateService),
                Case($(DropStatusChange.CANCELLED), () -> dropCancelledUpdateService),
                Case($(DropStatusChange.DELAYED), () -> dropDelayedUpdateService),
                Case($(DropStatusChange.FINISHED), () -> dropFinishedUpdateService))
                .update(drop, spot, company, profile, dropManagementRequest);
    }
}
