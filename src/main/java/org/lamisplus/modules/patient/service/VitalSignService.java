package org.lamisplus.modules.patient.service;

import com.github.dockerjava.api.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.patient.controller.exception.NoRecordFoundException;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.VitalSign;
import org.lamisplus.modules.patient.domain.entity.VitalSignDto;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.lamisplus.modules.patient.repository.VitalSignRepository;
import org.lamisplus.modules.patient.utility.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VitalSignService {
    private final VitalSignRepository vitalSignRepository;
    private final PersonRepository personRepository;

    static  final  String CURRENT_LOGIN_USER = SecurityUtils.getCurrentUserLogin ().orElse ("");

    static  final  Date CURRENT_DATE = Date.from (Instant.now ());


    public void archivedVitalSign(Long id) {
        VitalSignDto existingVitalSign = getExistingVitalSign (id);
        VitalSign vitalSign = convertVitalSignDtoToVitalSignEntity (existingVitalSign);
        vitalSign.setArchived (1);
        vitalSignRepository.save (vitalSign);
    }

    public VitalSignDto registerVitalSign(VitalSignDto vitalSignDto) {
        Long personId = vitalSignDto.getPersonId ();
        getExistingPerson (personId);
        VitalSign vitalSign = convertVitalSignDtoToVitalSignEntity (vitalSignDto);
        vitalSign.setCreatedBy (CURRENT_LOGIN_USER);
        vitalSign.setCreatedDate (CURRENT_DATE);
        VitalSign saveVitalSign = vitalSignRepository.save (vitalSign);
        return convertVitalSignEntityToVitalSignDto (saveVitalSign);
    }



    public VitalSign updateVitalSign(Long id, VitalSignDto vitalSignDto) {
        getExistingVitalSign (id);
        VitalSign vitalSign = convertVitalSignDtoToVitalSignEntity (vitalSignDto);
        vitalSign.setLastModifiedBy (CURRENT_LOGIN_USER);
        vitalSign.setLastModifiedDate (CURRENT_DATE);
        return vitalSignRepository.save (vitalSign);
    }


    public List<VitalSignDto> getVitalSign() {
        return vitalSignRepository.getVitalSignByArchived (0)
                .stream ().map (this::convertVitalSignEntityToVitalSignDto)
                .collect(Collectors.toList());
    }

    public VitalSignDto getVitalSignById(Long id) {
        return getExistingVitalSign (id);
    }


    private VitalSignDto getExistingVitalSign(Long id) {
        return vitalSignRepository
                .findById (id)
                .map (this::convertVitalSignEntityToVitalSignDto)
                .orElseThrow (() -> new NoRecordFoundException ("No vital sign found with id " + id));
    }

    private Person getExistingPerson(Long personId) {
        return personRepository
                .findById (personId)
                .orElseThrow (() -> new NoRecordFoundException ("No person found with id " + personId));
    }
    private VitalSign convertVitalSignDtoToVitalSignEntity(VitalSignDto vitalSignDto) {
        VitalSign vitalSign = VitalSign
                .builder ()
                .diastolic (vitalSignDto.getDiastolic ())
                .bodyWeight (vitalSignDto.getBodyWeight ())
                .serviceTypeId (vitalSignDto.getServiceTypeId ())
                .encounterDate (vitalSignDto.getEncounterDate ())
                .height (vitalSignDto.getHeight ())
                .personId (vitalSignDto.getPersonId ())
                .archived (0)
                .build ();
        if(vitalSign.getId () != null || vitalSign.getId () != 0){
            vitalSign.setId (vitalSignDto.getId ());

        }
        return  vitalSign;
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
                .archived (0)
                .build ();
    }
}
