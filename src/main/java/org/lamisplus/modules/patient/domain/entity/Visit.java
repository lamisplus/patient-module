package org.lamisplus.modules.patient.domain.entity;

import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Entity
@Table(name = "visit")
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Visit extends PatientAuditEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @PastOrPresent
    @Column(name = "visit_start_date", nullable = false)
    private LocalDate visitStartDate;

    @PastOrPresent
    @Column(name = "visit_end_date", nullable = false)
    private LocalDate visitEndDate;

    @Column(name = "uuid", updatable = false, nullable = false)
    private String uuid;

    private Integer archived;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
