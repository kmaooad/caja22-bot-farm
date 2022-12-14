package edu.kmaooad.domain.dto.department;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateDepartmentDTO {
    String id;
    String name;
    Boolean isHiring;
    String orgId;
}