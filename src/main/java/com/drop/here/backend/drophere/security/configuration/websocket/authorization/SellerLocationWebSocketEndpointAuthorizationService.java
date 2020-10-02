package com.drop.here.backend.drophere.security.configuration.websocket.authorization;

import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfileService;
import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@RequiredArgsConstructor
public class SellerLocationWebSocketEndpointAuthorizationService implements WebSocketEndpointAuthorizationService {
    private final DropService dropService;
    private final AccountProfileService accountProfileService;

    @Override
    public boolean authorize(AccountAuthentication authentication, String profileUid) {
        return API.Match(authentication.getPrincipal().getAccountType()).of(
                Case($(AccountType.COMPANY), () -> authorizeCompany(authentication, profileUid)),
                Case($(AccountType.CUSTOMER), () -> authorizeCustomer(authentication, profileUid))
        );
    }

    private boolean authorizeCustomer(AccountAuthentication authentication, String profileUid) {
        return dropService.isSellerLocationAvailableForCustomer(profileUid, authentication.getCustomer());
    }

    private boolean authorizeCompany(AccountAuthentication authentication, String profileUid) {
        return accountProfileService.existsByAccountAndProfileUid(authentication.getPrincipal(), profileUid);
    }
}
