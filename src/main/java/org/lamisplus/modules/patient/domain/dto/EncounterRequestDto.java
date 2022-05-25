package org.lamisplus.modules.patient.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EncounterRequestDto implements Serializable {
    private Long facilityId;
    private Long id;
    @PastOrPresent
    private LocalDate encounterDate;
    private Long personId;
    private String uuid;
    private Long visitId;
    private Set<String> serviceCode;
    private String status;
}
