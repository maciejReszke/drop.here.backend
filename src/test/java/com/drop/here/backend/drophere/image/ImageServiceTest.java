package com.drop.here.backend.drophere.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ImageRepository imageRepository;

    @Test
    void givenImageWhenCreateThenSave() {
        //given
        final byte[] bytes = "bytes".getBytes();
        final ImageType type = ImageType.CUSTOMER_IMAGE;

        when(imageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        final Image image = imageService.createImage(bytes, type);

        //then
        assertThat(image.getBytes()).isEqualTo(bytes);
        assertThat(image.getType()).isEqualTo(type);
    }

}