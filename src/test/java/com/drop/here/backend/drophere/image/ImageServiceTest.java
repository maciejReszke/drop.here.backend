package com.drop.here.backend.drophere.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private FilePart filePart;

    @Mock
    private DataBuffer dataBuffer;

    @Test
    void givenImageBytesNotExistingImageWhenCreateThenSave() {
        //given
        final byte[] bytes = "bytes".getBytes();
        final ImageType type = ImageType.CUSTOMER_IMAGE;
        final String entityId = "entityId";

        when(imageRepository.findByTypeAndEntityId(type, entityId)).thenReturn(Mono.empty());
        when(imageRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        //when
        final Mono<Image> result = imageService.createImage(bytes, type, entityId);

        //then
        StepVerifier.create(result)
                .assertNext(image -> {
                    assertThat(image.getBytes()).isEqualTo(bytes);
                    assertThat(image.getType()).isEqualTo(type);
                    assertThat(image.getEntityId()).isEqualTo(entityId);
                })
                .verifyComplete();
    }

    @Test
    void givenImageBytesExistingImageWhenCreateThenSave() {
        //given
        final byte[] bytes = "bytes".getBytes();
        final ImageType type = ImageType.CUSTOMER_IMAGE;
        final String entityId = "entityId";

        when(imageRepository.findByTypeAndEntityId(type, entityId)).thenReturn(Mono.just(Image.builder().id("5").build()));
        when(imageRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        //when
        final Mono<Image> result = imageService.createImage(bytes, type, entityId);

        //then
        StepVerifier.create(result)
                .assertNext(image -> {
                    assertThat(image.getBytes()).isEqualTo(bytes);
                    assertThat(image.getType()).isEqualTo(type);
                    assertThat(image.getEntityId()).isEqualTo(entityId);
                    assertThat(image.getId()).isEqualTo("5");
                })
                .verifyComplete();
    }

    @Test
    void givenFilePartNotExistingImageWhenCreateThenSave() {
        //given
        final byte[] bytes = "bytes".getBytes();
        final ImageType type = ImageType.CUSTOMER_IMAGE;
        final String entityId = "entityId";

        when(imageRepository.findByTypeAndEntityId(type, entityId)).thenReturn(Mono.empty());
        when(imageRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        when(filePart.content()).thenReturn(Flux.just(dataBuffer));
        when(dataBuffer.readableByteCount()).thenReturn(4);
        doNothing().when(dataBuffer).read(any());

        //when
        final Mono<Image> result = imageService.createImage(filePart, type, entityId);

        //then
        StepVerifier.create(result)
                .assertNext(image -> {
                    assertThat(image.getBytes()).isEqualTo(bytes);
                    assertThat(image.getType()).isEqualTo(type);
                    assertThat(image.getEntityId()).isEqualTo(entityId);
                })
                .verifyComplete();
    }

    @Test
    void givenFilePartExistingImageWhenCreateThenSave() {
        //given
        final byte[] bytes = "bytes".getBytes();
        final ImageType type = ImageType.CUSTOMER_IMAGE;
        final String entityId = "entityId";

        when(imageRepository.findByTypeAndEntityId(type, entityId)).thenReturn(Mono.just(Image.builder().id("5").build()));
        when(imageRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        when(filePart.content()).thenReturn(Flux.just(dataBuffer));
        when(dataBuffer.readableByteCount()).thenReturn(4);
        doNothing().when(dataBuffer).read(any());

        //when
        final Mono<Image> result = imageService.createImage(filePart, type, entityId);

        //then
        StepVerifier.create(result)
                .assertNext(image -> {
                    assertThat(image.getBytes()).isEqualTo(bytes);
                    assertThat(image.getType()).isEqualTo(type);
                    assertThat(image.getEntityId()).isEqualTo(entityId);
                    assertThat(image.getId()).isEqualTo("5");
                })
                .verifyComplete();
    }

    @Test
    void givenExistingImageWhenFindThenFind() {
        //given
        final Image image = Image.builder().build();
        final ImageType imageType = ImageType.CUSTOMER_IMAGE;
        final String entityId = "entityId";

        when(imageRepository.findByTypeAndEntityId(imageType, entityId)).thenReturn(Mono.just(image));

        //when
        final Mono<Image> result = imageService.findImage(entityId, imageType);

        //then
        StepVerifier.create(result)
                .expectNext(image)
                .verifyComplete();
    }

    @Test
    void givenNotExistingImageWhenFindThenEmpty() {
        //given
        final ImageType imageType = ImageType.CUSTOMER_IMAGE;
        final String entityId = "entityId";

        when(imageRepository.findByTypeAndEntityId(imageType, entityId)).thenReturn(Mono.empty());

        //when
        final Mono<Image> result = imageService.findImage(entityId, imageType);

        //then
        StepVerifier.create(result)
                .verifyComplete();
    }
}