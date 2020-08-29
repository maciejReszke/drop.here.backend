package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.company.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.country.Country;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class CompanyDataGenerator {

    public Company company(int i, Account account, Country country) {
        return Company.builder()
                .name("companyName" + i)
                .uid("uid" + i)
                .country(country)
                .visibilityStatus(CompanyVisibilityStatus.VISIBLE)
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .account(account)
                .build();
    }
}
