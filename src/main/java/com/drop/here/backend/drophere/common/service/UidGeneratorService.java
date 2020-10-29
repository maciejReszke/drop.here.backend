package com.drop.here.backend.drophere.common.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class UidGeneratorService {

    public String generateUid(String startingString, int startingStringPartLength, int randomPartLength) {
        final String startUid = startingString.length() > startingStringPartLength ? startingString.substring(0, startingStringPartLength) : startingString;
        final String generatedUid = startUid.replace(" ", "-") + RandomStringUtils.randomAlphanumeric(randomPartLength);
        return StringUtils.stripAccents(generatedUid);
    }
}
