package ec.edu.uteq.sgroas.dto;

/**
 * Session profile returned in authentication responses.
 * No tokens included: JWT travels only in HttpOnly cookie
 * {@code access_token} (Secure + SameSite=Strict).
 * @param name authenticated user name.
 * @param email authenticated user email.
 * @param role authenticated user role.
 * @param expiresIn seconds until session expiration.
 */
public record SessionResponse(
        String name,
        String email,
        String role,
        Long expiresIn
) {
}
