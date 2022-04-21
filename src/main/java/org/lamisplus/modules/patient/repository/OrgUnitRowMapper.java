package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.dto.ApplicationCodeDto;
import org.lamisplus.modules.patient.domain.dto.OrgUnitDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrgUnitRowMapper implements RowMapper<OrgUnitDto> {
    @Override
    public OrgUnitDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        System.out.printf ("rs: ", rs);
        return new OrgUnitDto (rs.getLong ("id"), rs.getString ("name"));
    }
}
