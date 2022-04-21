package org.lamisplus.modules.patient.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ContactPointDto implements Serializable {
    private final String type;
    private final String value;

}
