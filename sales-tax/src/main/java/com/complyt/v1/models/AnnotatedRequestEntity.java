package com.complyt.v1.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AnnotatedRequestEntity {
    @NotNull(message = "User may not be null")
    @NotBlank(message = "User may not be blank")
    private String user;

    @NotNull
    @Size(min = 4, max = 7, message = "Password may of length between 4 and 7")
    private String password;
}
