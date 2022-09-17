package org.lamisplus.modules.patient.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitDto implements Serializable {
    private Long facilityId;
    private Long id;
    private Long personId;
    private String checkInDate;
    public String checkOutDate;
    public List<EncounterResponseDto> encounters;


}
