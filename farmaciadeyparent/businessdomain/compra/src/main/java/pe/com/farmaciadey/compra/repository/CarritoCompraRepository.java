package pe.com.farmaciadey.compra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.com.farmaciadey.compra.models.CarritoCompra;

public interface CarritoCompraRepository extends JpaRepository<CarritoCompra, Integer> {
    @Query("SELECT o FROM CarritoCompra o WHERE eliminado = 0 AND usuarioId = ?1")
    List<CarritoCompra> list(Integer usuarioId);

    @Query("SELECT o FROM CarritoCompra o WHERE eliminado = 0 AND usuarioId = ?1 AND productoId = ?2")
    CarritoCompra find(Integer usuarioId, Integer productoId);

}
