package org.lamisplus.modules.patient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.patient.controller.exception.NoRecordFoundException;
import org.lamisplus.modules.patient.domain.dto.VitalSignDto;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.VitalSign;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.lamisplus.modules.patient.repository.VitalSignRepository;
import org.lamisplus.modules.patient.utility.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VitalSignService {
    private final VitalSignRepository vitalSignRepository;
    private final PersonRepository personRepository;

    public void archivedVitalSign(Long id) {
        VitalSign existingVitalSign = getExistingVitalSign (id);
        existingVitalSign.setArchived (1);
        vitalSignRepository.save (existingVitalSign);
    }

    public VitalSignDto registerVitalSign(VitalSignDto vitalSignDto) {
        log.info ("I am in service {}", vitalSignDto.getEncounterDate ());
        Long personId = vitalSignDto.getPersonId ();
        getExistingPerson (personId);
        log.info ("current user : {}", getCurrentLoginUser ());
        VitalSign vitalSign = convertVitalSignDtoToVitalSignEntity (vitalSignDto);
        vitalSign.setUuid (UUID.randomUUID ().toString ());
        VitalSign saveVitalSign = vitalSignRepository.save (vitalSign);
        return convertVitalSignEntityToVitalSignDto (saveVitalSign);
    }


    public VitalSignDto updateVitalSign(Long id, VitalSignDto vitalSignDto) {
        VitalSign existingVitalSign = getExistingVitalSign (id);
        VitalSign vitalSign = convertVitalSignDtoToVitalSignEntity (vitalSignDto);
        vitalSign.setId (existingVitalSign.getId ());
        vitalSign.setCreatedDate (existingVitalSign.getCreatedDate ());
        vitalSign.setCreatedBy (existingVitalSign.getCreatedBy ());
        VitalSign updateVitalSign = vitalSignRepository.save (vitalSign);
        return convertVitalSignEntityToVitalSignDto (updateVitalSign);
    }


    public List<VitalSignDto> getVitalSign() {
        return vitalSignRepository.getVitalSignByArchived (0)
                .stream ().map (this::convertVitalSignEntityToVitalSignDto)
                .collect (Collectors.toList ());
    }

    public VitalSignDto getVitalSignById(Long id) {
        return convertVitalSignEntityToVitalSignDto (getExistingVitalSign (id));
    }


    private VitalSign getExistingVitalSign(Long id) {
        return vitalSignRepository
                .findById (id)
                .orElseThrow (() -> new NoRecordFoundException ("No vital sign found with id " + id));
    }

    private Person getExistingPerson(Long personId) {
        return personRepository
                .findById (personId)
                .orElseThrow (() -> new NoRecordFoundException ("No person found with id " + personId));
    }

    private VitalSign convertVitalSignDtoToVitalSignEntity(VitalSignDto vitalSignDto) {
        VitalSign vitalSign = new VitalSign ();
        vitalSign.setDiastolic (vitalSignDto.getDiastolic ());
        vitalSign.setBodyWeight (vitalSignDto.getBodyWeight ());
        vitalSign.setServiceTypeId (vitalSignDto.getServiceTypeId ());
        vitalSign.setEncounterDate (vitalSignDto.getEncounterDate ());
        vitalSign.setHeight (vitalSignDto.getHeight ());
        vitalSign.setPersonId (vitalSignDto.getPersonId ());
        vitalSign.setSystolic (vitalSignDto.getSystolic ());
        vitalSign.setFacilityId (vitalSign.getFacilityId ());
        vitalSign.setArchived (0);
        return vitalSign;
    }

    private String getCurrentLoginUser() {
        return SecurityUtils.getCurrentUserLogin ().orElse ("");

    }

    private VitalSignDto convertVitalSignEntityToVitalSignDto(VitalSign vitalSign) {
        return VitalSignDto
                .builder ()
                .id (vitalSign.getId ())
                .diastolic (vitalSign.getDiastolic ())
                .bodyWeight (vitalSign.getBodyWeight ())
                .serviceTypeId (vitalSign.getServiceTypeId ())
                .encounterDate (vitalSign.getEncounterDate ())
                .height (vitalSign.getHeight ())
                .personId (vitalSign.getPersonId ())
                .systolic (vitalSign.getSystolic ())
                .archived (vitalSign.getArchived ())
                .uuid (vitalSign.getUuid ())
                .facilityId (vitalSign.getFacilityId ())
                .build ();
    }
}
