package org.lamisplus.modules.patient.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckInDto {
    List<Long> serviceIds;
    private VisitDto visitDto;

}
