package org.lamisplus.modules.patient.domain.entity;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
public class VitalSignDto implements Serializable {
    private final Long id;
    @NotNull
    private final Double bodyWeight;
    @NotNull
    private final Double diastolic;
    @PastOrPresent
    @NotNull
    private final LocalDate encounterDate;
    @NotNull
    private final Double height;
    @NotNull
    private final Long personId;
    private final Long serviceTypeId;
    @NotNull
    private final Double systolic;
    private final Integer archived;
}
