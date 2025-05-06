package com.example.GameWWW.model.dto.request;

import com.example.GameWWW.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerInfoReq implements Serializable {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String middleName;

    @NotNull
    private Gender gender;

    @NotNull
    @JsonFormat(pattern = "dd.MM.yyyy")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirthday;

    @NotEmpty
    private String telephonNum;

    @NotEmpty
    private String email;

    @NotNull
    private String placeOfWorkOrStudy;

    private String additionalInformation;
}
