package org.lamisplus.modules.patient.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EncounterStatusResponseDto
{
    private boolean status;
    private String message;

}
