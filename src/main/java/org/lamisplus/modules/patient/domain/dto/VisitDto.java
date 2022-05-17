package org.lamisplus.modules.patient.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitDto implements Serializable {
    private Long facilityId;
    private Long id;
    private Long personId;
    @PastOrPresent
    private LocalDate visitStartDate;
    @PastOrPresent
    private LocalDate visitEndDate;
}
