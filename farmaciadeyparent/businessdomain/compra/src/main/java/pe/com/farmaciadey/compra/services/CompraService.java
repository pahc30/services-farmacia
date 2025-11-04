package pe.com.farmaciadey.compra.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.farmaciadey.compra.models.Compra;
import pe.com.farmaciadey.compra.repository.CompraRepository;


@Service
public class CompraService {
    @Autowired
    private CompraRepository repository;

    public Compra save(Compra o){
        o.setFecha(new Date());
        o.setTotal(o.getSubtotal() + (o.getSubtotal() * o.getIgv()));
        return repository.save(o);
    }

    @Transactional(readOnly = true)
    public List<Compra> listByUsuario(Integer usuarioId){
        return repository.list(usuarioId);
    }
    
    /**
     * Genera un código correlativo único para la compra
     * Formato: C + número de 6 dígitos (ejemplo: C000001, C000002, etc.)
     */
    public String generarCodigoCompra() {
        // Obtener el último ID de compra de la base de datos
        Long ultimoId = repository.findMaxId();
        if (ultimoId == null) {
            ultimoId = 0L;
        }
        
        // Incrementar y formatear
        Long siguienteId = ultimoId + 1;
        return String.format("C%06d", siguienteId);
    }
}
