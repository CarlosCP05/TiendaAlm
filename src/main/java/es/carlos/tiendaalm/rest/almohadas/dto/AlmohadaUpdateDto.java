package es.carlos.tiendaalm.rest.almohadas.dto;

import es.carlos.tiendaalm.rest.almohadas.models.Tacto;
import es.carlos.tiendaalm.rest.almohadas.validators.FimezaOption;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlmohadaUpdateDto {
    @Min(value = 0, message = "El peso mínimo es de 1 gramo")
    private final Integer peso;

    @Min(value = 0, message = "La altura mínima es de 1 cm")
    private final Integer altura;

    @Min(value = 0, message = "El ancho mínimo es de 1 cm")
    private final Integer ancho;

    @Min(value = 0, message = "El grosor mínimo es de 1 cm")
    private final Integer grosor;

    private final Tacto tacto;

    @FimezaOption
    private final String firmeza;
}
