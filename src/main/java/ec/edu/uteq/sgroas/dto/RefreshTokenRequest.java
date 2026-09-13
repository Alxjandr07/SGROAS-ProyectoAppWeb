package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Peticion para renovar la sesion con el token de refresco.
 * @param refreshToken token de refresco vigente.
 */
public record RefreshTokenRequest(

        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {
}