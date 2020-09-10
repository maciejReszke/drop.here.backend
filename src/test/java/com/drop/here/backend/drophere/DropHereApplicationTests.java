package com.drop.here.backend.drophere;

import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DropHereApplicationTests extends IntegrationBaseClass {

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }

}
