package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RestablecerContrasenaRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe ser un email valido")
        String email,

        @NotBlank(message = "El codigo es obligatorio")
        @Pattern(regexp = "\\d{6}", message = "El codigo debe tener 6 digitos")
        String codigo,

        @NotBlank(message = "La nueva contrasena es obligatoria")
        @Size(min = 6, max = 100, message = "La contrasena debe tener al menos 6 caracteres")
        String nuevaPassword
) {
}
