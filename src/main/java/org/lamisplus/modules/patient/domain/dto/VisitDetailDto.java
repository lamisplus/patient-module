package org.lamisplus.modules.patient.domain.dto;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class VisitDetailDto implements Serializable {
    private final Long facilityId;
    private final Long id;
    private final Long personId;
    private final Long encounterId;
    private final LocalDateTime checkInDate;
    private final LocalDateTime checkOutDate;
    private final String service;
    private final String status;
}
