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

    public boolean isOwnAccountOperation(AccountAuthentication accountAuthentication, Long accountId) {
        return accountAuthentication.getPrincipal().getId().equals(accountId);
    }

    public boolean isOwnProfileOperation(AccountAuthentication accountAuthentication, String profileUid) {
        return accountAuthentication.hasProfile() &&
                accountAuthentication.getProfile().getProfileUid().equalsIgnoreCase(profileUid);
    }

    public boolean isOwnCompanyOperation(AccountAuthentication accountAuthentication, String companyUid) {
        return accountAuthentication.getPrincipal().getAccountType() == AccountType.COMPANY &&
                accountAuthentication.getCompany() != null &&
                accountAuthentication.getCompany().getUid().equalsIgnoreCase(companyUid);
    }

    public boolean isCompanyVisible(String companyUid) {
        return companyService.isVisible(companyUid);
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
