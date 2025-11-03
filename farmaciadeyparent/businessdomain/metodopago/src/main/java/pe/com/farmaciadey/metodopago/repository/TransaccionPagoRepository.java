package pe.com.farmaciadey.metodopago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.com.farmaciadey.metodopago.models.TransaccionPago;
import pe.com.farmaciadey.metodopago.models.TransaccionPago.EstadoTransaccion;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionPagoRepository extends JpaRepository<TransaccionPago, Long> {
    
    @Query("SELECT t FROM TransaccionPago t WHERE t.eliminado = 0")
    List<TransaccionPago> findAllActive();
    
    @Query("SELECT t FROM TransaccionPago t WHERE t.compraId = :compraId AND t.eliminado = 0")
    List<TransaccionPago> findByCompraId(@Param("compraId") Long compraId);
    
    @Query("SELECT t FROM TransaccionPago t WHERE t.referenciaExterna = :referenciaExterna AND t.eliminado = 0")
    Optional<TransaccionPago> findByReferenciaExterna(@Param("referenciaExterna") String referenciaExterna);
    
    @Query("SELECT t FROM TransaccionPago t WHERE t.estado = :estado AND t.eliminado = 0")
    List<TransaccionPago> findByEstado(@Param("estado") EstadoTransaccion estado);
    
    @Query("SELECT t FROM TransaccionPago t WHERE t.compraId = :compraId AND t.estado = :estado AND t.eliminado = 0")
    Optional<TransaccionPago> findByCompraIdAndEstado(@Param("compraId") Long compraId, @Param("estado") EstadoTransaccion estado);
}