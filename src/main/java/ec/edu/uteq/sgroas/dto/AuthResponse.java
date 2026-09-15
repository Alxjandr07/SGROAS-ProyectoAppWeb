package ec.edu.uteq.sgroas.dto;

/**
 * Authentication response with tokens and basic user data.
 * @param accessToken token for authenticated requests.
 * @param refreshToken token for session renewal.
 * @param tokenType type of issued token.
 * @param expiresIn seconds until access token expiration.
 * @param name authenticated user name.
 * @param email authenticated user email.
 * @param role authenticated user role.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        String name,
        String email,
        String role
) {
}
