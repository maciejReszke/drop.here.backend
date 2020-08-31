package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropPersistenceServiceTest {

    @InjectMocks
    private DropPersistenceService dropPersistenceService;

    @Mock
    private DropRepository dropRepository;

    @Test
    void givenExistingDropWhenFindDropThenFind() {
        //given
        final String dropUid = "dropUid";
        final String companyUid = "companyUid";
        final Drop drop = Drop.builder().build();

        when(dropRepository.findByUidAndCompanyUid(dropUid, companyUid))
                .thenReturn(Optional.of(drop));

        //when
        final Drop result = dropPersistenceService.findDrop(dropUid, companyUid);

        //then
        assertThat(result).isEqualTo(drop);
    }

    @Test
    void givenNotExistingDropWhenFindDropThenException() {
        //given
        final String dropUid = "dropUid";
        final String companyUid = "companyUid";

        when(dropRepository.findByUidAndCompanyUid(dropUid, companyUid))
                .thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> dropPersistenceService.findDrop(dropUid, companyUid));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

}