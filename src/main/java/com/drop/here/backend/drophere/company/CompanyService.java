package com.drop.here.backend.drophere.company;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public boolean isVisible(String companyUid) {
        return companyRepository.findByUid(companyUid)
                .map(company -> company.getVisibilityStatus() == CompanyVisibilityStatus.VISIBLE)
                .orElse(false);
    }
}
