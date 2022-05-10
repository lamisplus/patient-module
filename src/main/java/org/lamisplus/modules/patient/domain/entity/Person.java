package org.lamisplus.modules.patient.domain.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
public class Person extends  PatientAuditEntity implements Serializable, Persistable<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "active", nullable = false)
    private Boolean active = false;
    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    private JsonNode contactPoint;
    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    private JsonNode address;
    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    private JsonNode gender;
    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    private JsonNode identifier;
    @Column(name = "deceased")
    private Boolean deceased;
    @Column(name = "deceased_date_time")
    private LocalDateTime deceasedDateTime;
    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    private JsonNode  maritalStatus;
    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    private JsonNode employmentStatus;
    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    private JsonNode education;
    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    private JsonNode organization;
    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    private JsonNode contact;
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "date_of_registration", nullable = false)
    private LocalDate dateOfRegistration;
    @Basic
    @Column(name = "archived")
    private Integer archived;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "surname")
    private String surname;

    @Column(name = "other_name")
    private String otherName;
    @Column(name = "is_date_of_birth_estimated")
    private Boolean isDateOfBirthEstimated;

    @Override
    public boolean isNew() {
        return true;
    }
}
