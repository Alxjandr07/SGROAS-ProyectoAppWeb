package ec.edu.uteq.sgroas.dto;

import java.time.Instant;

/**
 * Response with full user data.
 * @param id unique user identifier
 * @param name user name
 * @param email user email
 * @param role assigned role
 * @param active whether the user is active
 * @param createdAt record creation timestamp
 * @param updatedAt last update timestamp
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
