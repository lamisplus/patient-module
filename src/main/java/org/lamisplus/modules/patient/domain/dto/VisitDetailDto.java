package org.lamisplus.modules.patient.domain.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
public class VisitDetailDto implements Serializable {
    private final Long facilityId;
    private final Long id;
    private final Long personId;
    @PastOrPresent
    private final LocalDate checkInDate;
    @PastOrPresent
    private final LocalDate checkOutDate;
    private final String service;
    private final String status;
}
