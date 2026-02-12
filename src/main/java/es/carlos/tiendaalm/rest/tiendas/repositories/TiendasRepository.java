package es.carlos.tiendaalm.rest.tiendas.repositories;

import es.carlos.tiendaalm.rest.tiendas.models.Tienda;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TiendasRepository extends JpaRepository<Tienda, Long>, JpaSpecificationExecutor<Tienda> {
    Optional<Tienda> findByNombreEqualsIgnoreCase(String nombre);

    List<Tienda> findByNombreContainingIgnoreCase(String nombre);

    List<Tienda> findByIsDeleted(Boolean isDeleted);

    @Modifying
    @Query("UPDATE Tienda tie SET tie.isDeleted = true WHERE tie.id = :id")
    void updateIsDeletedToTrueById(Long id);

    //Hay que cambiar la consulta haciendo que pase por stock, el de carlos busca si hay alguna tajeta asociada a un titular y devuelve un boolean
    //Es decir que busca si en una tienda hay almohadas con él id de tienda, es posible que tenga que pensar otra cosa por ejemplo si una almohada está en alguna tienda(seria desde almohada) o una lista de almohadas según la tienda
    //@Query("SELECT CASE WHEN COUNT(t) > 0 THEN true false END FROM Almohada a WHERE a.")
    //Boolean existAlmohadaById(Long id);
}