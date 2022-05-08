package org.lamisplus.modules.patient.service;

import com.github.dockerjava.api.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.VitalSign;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.lamisplus.modules.patient.repository.VitalSignRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VitalSignService {
    private final VitalSignRepository vitalSignRepository;
    private final PersonRepository personRepository;

    public void archivedVitalSign(Long id) {
        VitalSign existingVitalSign = getExistingVitalSign (id);
        existingVitalSign.setArchived (1);
        vitalSignRepository.save(existingVitalSign);
    }

    public VitalSign registerVitalSign(VitalSign vitalSign) {
        Long personId = vitalSign.getPersonId ();
        getExistingPerson (personId);
        return vitalSignRepository.save (vitalSign);
    }


    public VitalSign updateVitalSign(Long id, VitalSign vitalSign) {
        getExistingVitalSign (id);
        return vitalSignRepository.save (vitalSign);
    }


    public List<VitalSign> getVitalSign() {
        return vitalSignRepository.getVitalSignByArchived(0);
    }

    public VitalSign getVitalSignById(Long id) {
        return getExistingVitalSign (id);
    }


    private VitalSign getExistingVitalSign(Long id) {
        return vitalSignRepository
                .findById (id)
                .orElseThrow (() -> new NotFoundException ("No vital sign found with id " + id));
    }

    private Person getExistingPerson(Long personId) {
        return personRepository
                .findById (personId)
                .orElseThrow (() -> new NotFoundException ("No person found with id " + personId));
    }
}
