package org.lamisplus.modules.patient.domain.dto;

import lombok.Data;

@Data
public class VisitRequest
{
    private String checkInDate;
    private Long personId;
}
