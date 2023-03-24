package org.lamisplus.modules.patient.domain.dto;

import lombok.Data;

@Data
public class VisitRequest
{
    private Long personId;
    private String checkInDate;

}
