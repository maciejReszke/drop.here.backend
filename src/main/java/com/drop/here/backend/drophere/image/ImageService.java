package com.drop.here.backend.drophere.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

// TODO MONO:
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    // todo bylo transactional
    public Image createImage(byte[] image, ImageType imageType) {
        return imageRepository.save(
                Image.builder()
                        .bytes(image)
                        .type(imageType)
                        .build()
        );
    }
}
