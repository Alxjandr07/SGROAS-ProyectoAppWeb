package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

    /**
     * Incident data required to create or update an incident record.
     * @param assignmentId assignment associated with the incident.
     * @param reportedBy person or system that reported the incident.
     * @param type incident type code.
     * @param description free-text description of the incident.
     * @param incidentDate date and time when the incident happened.
     * @param location optional place where the incident occurred.
     * @param severity incident severity code.
     * @param status incident status code.
     */
public record IncidentRequest(

        @NotNull(message = "La asignacion es obligatoria")
        Long assignmentId,

        @NotBlank(message = "El reportante es obligatorio")
        @Size(max = 100, message = "El reportante no puede superar los 100 caracteres")
        String reportedBy,

        @NotBlank(message = "El tipo es obligatorio")
        String type,

        @NotBlank(message = "La descripcion es obligatoria")
        String description,

        @NotNull(message = "La fecha del incidente es obligatoria")
        LocalDateTime incidentDate,

        @Size(max = 255, message = "La ubicacion no puede superar los 255 caracteres")
        String location,

        @NotBlank(message = "La gravedad es obligatoria")
        String severity,

        @NotBlank(message = "El estado es obligatorio")
        String status
) {
}
