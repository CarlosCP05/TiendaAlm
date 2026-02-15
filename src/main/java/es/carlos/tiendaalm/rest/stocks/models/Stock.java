package es.carlos.tiendaalm.rest.stocks.models;

import es.carlos.tiendaalm.rest.almohadas.models.Almohada;
import es.carlos.tiendaalm.rest.tiendas.models.Tienda;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "STOCK_ALMOHADAS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"tienda_id", "almohada_id"})
        }
)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tienda_id")
    private Tienda tienda;

    @OneToMany(mappedBy = "stock")
    @JsonIgnoreProperties("titular")
    @ToString.Exclude
    private List<Almohada> almohada;

    @Column(nullable = false)
    private Integer cantidad;
}
