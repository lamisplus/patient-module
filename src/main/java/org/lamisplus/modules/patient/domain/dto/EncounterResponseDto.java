package org.lamisplus.modules.patient.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lamisplus.modules.patient.utility.LocalDateConverter;

import javax.persistence.Convert;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EncounterResponseDto {
    private Long facilityId;
    private Long id;
    @PastOrPresent
    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate encounterDate;
    private Long personId;
    private String uuid;
    private Long visitId;
    private String serviceCode;
    private String status;

}
