package es.carlos.tiendaalm.rest.almohadas.repositories;

import es.carlos.tiendaalm.rest.almohadas.models.*;
import java.util.List;
import java.util.Optional;

import es.carlos.tiendaalm.rest.tiendas.models.Tienda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AlmohadasRepository extends JpaRepository<Almohada, Long>, JpaSpecificationExecutor<Almohada> {

    Optional<Almohada>findById(long id);
    boolean existsById(long id);
    void deleteById(long id);

    List<Almohada> findByIsDeleted(boolean isDeleted);

    @Modifying
    @Query("UPDATE Almohada a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeleted(@Param("id") long id);

    //Terminar de implementar stock y modificar estas busquedas si es necesario
    @Query("SELECT a FROM Almohada a WHERE a.stock.tienda.id = :usuarioId")
    Page<Almohada> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("SELECT a FROM Almohada a WHERE a.stock.tienda.id = :usuarioId")
    List<Almohada> findByUsuarioId(Long usuarioId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Almohada a WHERE a.stock.tienda.usuario.id = :id")
    Boolean existsByUsuarioId(Long id);

    // Implementar junto con stock
    //List<Almohada> findByStock(Stock stock);
}
