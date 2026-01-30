package es.carlos.tiendaalm.rest.almohadas.dto;

import es.carlos.tiendaalm.rest.almohadas.models.Tacto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlmohadaResponseDto {
    private Long id;
    private Integer peso;
    private Integer altura;
    private Integer ancho;
    private Integer grosor;
    private Tacto tacto;
    private String firmeza;
}
