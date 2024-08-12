package org.lamisplus.modules.patient.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceRequest {
    private Long visitId;
    private String serviceCode;
}
