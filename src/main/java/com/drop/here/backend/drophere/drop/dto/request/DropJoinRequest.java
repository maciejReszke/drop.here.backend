package com.drop.here.backend.drophere.drop.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DropJoinRequest {

    @ApiModelProperty(value = "Password needed to join drop (if is needed)", example = "Aezakmi")
    @Length(max = 20)
    private String password;

    @NotNull
    @ApiModelProperty(value = "Does user want to receive notifications", example = "true")
    private boolean receiveNotification;
}
