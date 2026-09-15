package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.time.LocalDate;

    /**
     * Driver data returned by the API after querying or mutating drivers.
     * @param id driver unique identifier.
     * @param firstNames driver given names.
     * @param lastNames driver family names.
     * @param nationalId national identification number.
     * @param licenseNumber driver license number.
     * @param licenseType driver license class or category.
     * @param licenseExpiry date when the license expires.
     * @param phone contact phone number.
     * @param email contact email address.
     * @param status driver status code.
     * @param active whether the driver is currently active.
     * @param licenseExpiring whether the license expires within the warning window.
     * @param createdAt registration timestamp.
     * @param updatedAt last update timestamp.
     */
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
