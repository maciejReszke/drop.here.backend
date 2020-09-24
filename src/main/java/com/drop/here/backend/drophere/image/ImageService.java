package com.drop.here.backend.drophere.image;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public Mono<Image> createImage(byte[] bytes, ImageType imageType, String entityId) {
        return findImage(entityId, imageType)
                .switchIfEmpty(Mono.just(Image.builder().entityId(entityId).type(imageType).build()))
                .doOnNext(image -> image.setBytes(bytes))
                .flatMap(imageRepository::save);
    }

    public Mono<Image> createImage(FilePart filePart, ImageType imageType, String entityId) {
        return readBytes(filePart)
                .flatMap(bytes -> createImage(bytes, imageType, entityId));
    }

    private Mono<byte[]> readBytes(FilePart image) {
        return image.content()
                .map(this::readBytes)
                .reduce(ArrayUtils::addAll);
    }

    private byte[] readBytes(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return bytes;
    }

    public Mono<Image> findImage(String entityId, ImageType imageType) {
        return imageRepository.findByTypeAndEntityId(imageType, entityId);
    }
}
