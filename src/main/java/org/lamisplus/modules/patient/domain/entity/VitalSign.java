package org.lamisplus.modules.patient.domain.entity;

import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Builder
@Entity
@Table(name = "vital_sign")
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class VitalSign extends PatientAuditEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @NotNull
    private Double bodyWeight;
    @NotNull
    private Double diastolic;
    @PastOrPresent
    @NotNull
    private LocalDate encounterDate;
    @NotNull
    private Double height;
    @NotNull
    private Long personId;
    private Long serviceTypeId;
    @NotNull
    private Double systolic;
    private Integer archived;
    @NotNull
    private String uuid;

    @Override
    public boolean isNew() {
        return true;
    }
}
