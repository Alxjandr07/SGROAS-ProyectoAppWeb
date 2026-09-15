package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record IncidentResponse(
        Long id,
        Long assignmentId,
        String reportedBy,
        String type,
        String description,
        LocalDateTime incidentDate,
        String location,
        String severity,
        String status,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
