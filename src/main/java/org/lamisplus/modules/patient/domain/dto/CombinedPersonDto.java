package org.lamisplus.modules.patient.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.lamisplus.modules.patient.domain.entity.Person;

import java.util.UUID;

@Setter
@Getter
public class CombinedPersonDto {
    private Person person;
    private PersonDtoProjection dtoProjection;

    public CombinedPersonDto(Person person, PersonDtoProjection dtoProjection) {
        this.person = person;
        this.dtoProjection = dtoProjection;
    }
}
