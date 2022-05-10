package org.lamisplus.modules.patient.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.lamisplus.modules.patient.utility.LocalDateConverter;

import javax.persistence.Convert;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class VitalSignDto implements Serializable {
    private final Long id;
    @NotNull
    private final Double bodyWeight;
    @NotNull
    private final Double diastolic;
    @PastOrPresent
    @NotNull
    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate encounterDate;
    @NotNull
    private final Double height;
    @NotNull
    private final Long personId;
    private final Long serviceTypeId;
    @NotNull
    private final Double systolic;

    @NotNull
    @JsonIgnore
    private final String uuid;

    private final Integer archived;
}
