package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.*;

public record VehicleRequest(

        @NotBlank(message = "La placa es obligatoria")
        @Size(max = 20, message = "La placa no puede superar los 20 caracteres")
        String plate,

        @NotBlank(message = "La marca es obligatoria")
        @Size(max = 50, message = "La marca no puede superar los 50 caracteres")
        String brand,

        @NotBlank(message = "El modelo es obligatorio")
        @Size(max = 50, message = "El modelo no puede superar los 50 caracteres")
        String model,

        @NotNull(message = "El anio es obligatorio")
        @Min(value = 1990, message = "El anio debe ser mayor o igual a 1990")
        @Max(value = 2030, message = "El anio debe ser menor o igual a 2030")
        Integer year,

        @NotNull(message = "La capacidad de pasajeros es obligatoria")
        @Min(value = 1, message = "La capacidad minima es 1")
        Integer capacity,

        @Size(max = 50, message = "El numero de motor no puede superar los 50 caracteres")
        String engineNumber,

        @Size(max = 50, message = "El numero de chasis no puede superar los 50 caracteres")
        String chassisNumber,

        @Size(max = 30, message = "El color no puede superar los 30 caracteres")
        String color,

        @NotBlank(message = "El estado es obligatorio")
        String status
) {
}
