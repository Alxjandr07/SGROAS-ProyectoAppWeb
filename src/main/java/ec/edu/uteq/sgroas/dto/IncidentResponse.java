package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.time.LocalDateTime;

    /**
     * Incident data returned by the API after querying or mutating incidents.
     * @param id incident unique identifier.
     * @param assignmentId assignment associated with the incident.
     * @param reportedBy person or system that reported the incident.
     * @param type incident type code.
     * @param description free-text description of the incident.
     * @param incidentDate date and time when the incident happened.
     * @param location place where the incident occurred.
     * @param severity incident severity code.
     * @param status incident status code.
     * @param active whether the incident is currently active.
     * @param createdAt registration timestamp.
     * @param updatedAt last update timestamp.
     */
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
