package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato valido")
        @Size(max = 255)
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 6, max = 100, message = "La contrasena debe tener entre 6 y 100 caracteres")
        String password,

        @NotBlank(message = "El rol es obligatorio")
        String rol
) {
}
