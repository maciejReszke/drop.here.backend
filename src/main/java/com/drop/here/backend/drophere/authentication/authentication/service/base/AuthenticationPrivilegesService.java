package com.drop.here.backend.drophere.authentication.authentication.service.base;

import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.company.service.CompanyService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationPrivilegesService {
    private final CompanyService companyService;

    public boolean isOwnCompanyOperation(AccountAuthentication accountAuthentication, String companyUid) {
        return accountAuthentication.getPrincipal().getAccountType() == AccountType.COMPANY &&
                accountAuthentication.getCompany() != null &&
                accountAuthentication.getCompany().getUid().equalsIgnoreCase(companyUid);
    }

    public boolean isCompanyVisibleForCustomer(AccountAuthentication accountAuthentication, String companyUid) {
        return companyService.isVisible(companyUid) &&
                accountAuthentication.getCustomer() != null &&
                !companyService.isBlocked(companyUid, accountAuthentication.getCustomer());
    }

    public boolean isOwnCustomerOperation(AccountAuthentication accountAuthentication, Long customerId) {
        return accountAuthentication.getPrincipal().getAccountType() == AccountType.CUSTOMER &&
                accountAuthentication.getCustomer() != null &&
                accountAuthentication.getCustomer().getId().equals(customerId);
    }

    public boolean isCompaniesCustomer(AccountAuthentication accountAuthentication, Long customerId) {
        return accountAuthentication.getPrincipal().getAccountType() == AccountType.COMPANY &&
                accountAuthentication.getCompany() != null &&
                companyService.hasRelation(accountAuthentication.getCompany(), customerId);
    }
}
