package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe ser un email valido")
        String email
) {
}
