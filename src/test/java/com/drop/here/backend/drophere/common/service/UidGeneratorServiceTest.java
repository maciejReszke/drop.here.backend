package com.drop.here.backend.drophere.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UidGeneratorServiceTest {

    @InjectMocks
    private UidGeneratorService uidGeneratorService;

    @Test
    void givenEmptyStartingStringWhenGenerateUidThenGenerate() {
        //given
        final String startingString = "";
        final int startingStringPartLength = 5;
        final int randomPartLength = 6;

        //when
        final String result = uidGeneratorService.generateUid(startingString, startingStringPartLength, randomPartLength);

        //then
        assertThat(result).hasSize(6)
                .doesNotContain(" ");
    }

    @Test
    void givenStartingStringShorterThanStartingStringPartLengthWhenGenerateUidThenGenerate() {
        //given
        final String startingString = "mac k";
        final int startingStringPartLength = 5;
        final int randomPartLength = 6;

        //when
        final String result = uidGeneratorService.generateUid(startingString, startingStringPartLength, randomPartLength);

        //then
        assertThat(result).hasSize(11)
                .doesNotContain(" ")
                .startsWith("mac-k");
    }

    @Test
    void givenStartingStringLongerThanStartingStringPartLengthWhenGenerateUidThenGenerate() {
        //given
        final String startingString = "glodny niedzwiedz";
        final int startingStringPartLength = 5;
        final int randomPartLength = 6;

        //when
        final String result = uidGeneratorService.generateUid(startingString, startingStringPartLength, randomPartLength);

        //then
        assertThat(result).hasSize(11)
                .doesNotContain(" ")
                .startsWith("glodn")
                .doesNotStartWith("glodny-niedzwiedz");
    }
}