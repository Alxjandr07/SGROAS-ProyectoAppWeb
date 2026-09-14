package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Peticion para verificar el correo electronico de una cuenta.
 * @param email correo electronico a verificar
 * @param codigo codigo de verificacion de seis digitos
 */
public record VerifyEmailRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe ser un email valido")
        String email,

        @NotBlank(message = "El codigo es obligatorio")
        @Pattern(regexp = "\\d{6}", message = "El codigo debe tener 6 digitos")
        String codigo
) {
}
