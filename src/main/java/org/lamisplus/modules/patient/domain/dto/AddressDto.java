package org.lamisplus.modules.patient.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class AddressDto implements Serializable {
    private final List<String> line;
    private final String city;
    private final String district;
    private final Long stateId;
    private final String postalCode;
    private final Long countryId;
    private final Long organisationUnitId;
}
