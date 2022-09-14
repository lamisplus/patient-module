package org.lamisplus.modules.patient.domain.dto;

import lombok.Data;

import java.util.List;
@Data
public class VisitRequestDto
{
    private List<Long> serviceIds;
    private VisitRequest visitDto;
}
