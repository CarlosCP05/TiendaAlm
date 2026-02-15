package es.carlos.tiendaalm.rest.stocks.dto;

import es.carlos.tiendaalm.rest.almohadas.models.Almohada;
import es.carlos.tiendaalm.rest.tiendas.models.Tienda;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class StockRequestDto {
    @NotBlank(message = "La tienda no puede estar vacío")
    @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private final Tienda tienda;

    private final List<Almohada> almohadas;

    private Integer cantidad;

}
