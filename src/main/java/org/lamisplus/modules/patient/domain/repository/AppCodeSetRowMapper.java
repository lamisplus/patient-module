package org.lamisplus.modules.patient.domain.repository;

import org.lamisplus.modules.patient.domain.dto.ApplicationCodeDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AppCodeSetRowMapper implements RowMapper<ApplicationCodeDto> {
    @Override
    public ApplicationCodeDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        System.out.printf ("codeSet: ", rs);
        return  new ApplicationCodeDto (rs.getLong ("id"),  rs.getString ("display"));
    }
}
