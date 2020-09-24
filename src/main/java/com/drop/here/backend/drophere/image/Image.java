package com.drop.here.backend.drophere.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    @Id
    private String id;

    @NotNull
    private ImageType type;

    @NotNull
    private byte[] bytes;

    @NotBlank
    private String entityId;

    @Version
    private Long version;
}
