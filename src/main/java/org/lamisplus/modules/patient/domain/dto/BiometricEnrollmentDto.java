package org.lamisplus.modules.patient.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.lamisplus.modules.patient.utility.LocalDateConverter;

import javax.persistence.Convert;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class BiometricEnrollmentDto implements Serializable {
    private final Long facilityId;
    private final Long personId;
    @NotNull
    private final byte[] template;
    @NotNull
    private final String biometricType;
    @NotNull
    private final String templateType;
    @NotNull
    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate date;
    private final JsonNode extra;
}
