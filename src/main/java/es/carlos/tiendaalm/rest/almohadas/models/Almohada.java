package es.carlos.tiendaalm.rest.almohadas.models;

import es.carlos.tiendaalm.rest.tiendas.models.Tienda;
import jakarta.persistence.*;
import lombok.*;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // JPA necesita un constructor vacío
@Entity
@Table(name = "ALMOHADAS")
public class Almohada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer peso;
    @Column(nullable = false)
    private Integer altura;
    @Column(nullable = false)
    private Integer ancho;
    @Column(nullable = false)
    private Integer grosor;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Tacto tacto;
    @Column(nullable = false)
    private String firmeza;

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    //Relación con tienda, Una tienda puede tener muchas almohadas y repetidas
    //Me recomiendo usar otra entidad como stock
//    @ManyToOne
//    @JoinColumn(name = "tienda_id")
//    private Tienda tienda;
}