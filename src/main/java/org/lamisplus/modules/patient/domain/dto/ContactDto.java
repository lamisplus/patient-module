package org.lamisplus.modules.patient.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ContactDto implements Serializable {
    private final Long relationshipId;
    private final String surname;
    private final String firstname;
    private final String otherName;
    private final AddressDto address;
    private final Long genderId;
    private final ContactPointDto contactPoint;
}
