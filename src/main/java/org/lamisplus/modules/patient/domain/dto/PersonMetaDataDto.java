package org.lamisplus.modules.patient.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PersonMetaDataDto implements Serializable {
    private  int totalRecords;

    private int totalPages;

    private int pageSize;

    private int currentPage;

    private List<PersonResponseDto> records;
}
