package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.time.LocalDate;

public record DriverResponse(
        Long id,
        String firstNames,
        String lastNames,
        String nationalId,
        String licenseNumber,
        String licenseType,
        LocalDate licenseExpiry,
        String phone,
        String email,
        String status,
        Boolean active,
        Boolean licenseExpiring,
        Instant createdAt,
        Instant updatedAt
) {
}
