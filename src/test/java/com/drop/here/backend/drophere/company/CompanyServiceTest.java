package com.drop.here.backend.drophere.company;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {
    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Test
    void givenVisibleCompanyWhenIsVisibleThenTrue() {
        //given
        final String uid = "uid";
        when(companyRepository.findByUid(uid)).thenReturn(Optional.of(Company.builder()
                .visibilityStatus(CompanyVisibilityStatus.VISIBLE)
                .build()));

        //when
        final boolean result = companyService.isVisible(uid);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNotVisibleCompanyWhenIsVisibleThenFalse() {
        //given
        final String uid = "uid";
        when(companyRepository.findByUid(uid)).thenReturn(Optional.of(Company.builder()
                .visibilityStatus(CompanyVisibilityStatus.HIDDEN)
                .build()));

        //when
        final boolean result = companyService.isVisible(uid);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenNotExistingCompanyWhenIsVisibleThenFalse() {
        //given
        final String uid = "uid";
        when(companyRepository.findByUid(uid)).thenReturn(Optional.empty());

        //when
        final boolean result = companyService.isVisible(uid);

        //then
        assertThat(result).isFalse();
    }
}