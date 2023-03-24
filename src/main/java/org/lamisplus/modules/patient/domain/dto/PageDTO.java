package org.lamisplus.modules.patient.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDTO implements Serializable {
    private long totalRecords;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
}
