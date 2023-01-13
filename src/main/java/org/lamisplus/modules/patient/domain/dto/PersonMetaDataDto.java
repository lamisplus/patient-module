package org.lamisplus.modules.patient.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PersonMetaDataDto implements Serializable {
    private  long totalRecords;

    private int totalPages;

    private int pageSize;

    private int currentPage;

    private List records = new ArrayList<>();

}
