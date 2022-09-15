package org.lamisplus.modules.patient.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class EncounterPersonListDto {
    private LocalDateTime encounterDate;
    private Long encounterId;
}
