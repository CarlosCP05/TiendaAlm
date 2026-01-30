package es.carlos.tiendaalm.rest.almohadas.repositories;

import es.carlos.tiendaalm.rest.almohadas.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlmohadasRepository extends JpaRepository<Almohada, Long>, JpaSpecificationExecutor<Almohada> {

    Optional<Almohada>findById(long id);
    boolean existsById(long id);
    void deleteById(long id);

    List<Almohada> findByIsDeleted(boolean isDeleted);

    @Modifying
    @Query("UPDATE Almohada a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeleted(@Param("id") long id);
}
