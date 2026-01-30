package es.carlos.tiendaalm.rest.almohadas.dto;

import es.carlos.tiendaalm.rest.almohadas.models.Tacto;
import es.carlos.tiendaalm.rest.almohadas.validators.FimezaOption;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlmohadaCreateDto {
    @NotNull(message = "El peso tiene que estar definido")
    @Min(value = 0, message = "El peso mínimo es de 1 gramo")
    private final Integer peso;

    @NotNull(message = "La altura tiene que estar definida")
    @Min(value = 0, message = "La altura mínima es de 1 cm")
    private final Integer altura;

    @NotNull(message = "El ancho tiene que estar definido")
    @Min(value = 0, message = "El ancho mínimo es de 1 cm")
    private final Integer ancho;

    @NotNull(message = "El grosor tiene que estar definido")
    @Min(value = 0, message = "El grosor mínimo es de 1 cm")
    private final Integer grosor;

    @NotNull(message = "El tacto tiene que estar definido")
    private final Tacto tacto;

    @NotBlank(message = "La firmeza tiene que estar definida")
    @FimezaOption
    private final String firmeza;
}
