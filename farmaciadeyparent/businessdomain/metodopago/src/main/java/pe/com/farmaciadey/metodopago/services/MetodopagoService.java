package pe.com.farmaciadey.metodopago.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.farmaciadey.metodopago.models.Metodopago;
import pe.com.farmaciadey.metodopago.repository.MetodopagoRepository;

@Service
public class MetodopagoService {
    public double calcularComision(String tipo, double monto) {
        switch (tipo.toUpperCase()) {
            case "TARJETA":
                return monto * 0.039;
            case "YAPE":
                return monto * 0.025;
            case "EFECTIVO":
                return 0.0;
            default:
                return 0.0;
        }
    }
    @Autowired
    private MetodopagoRepository repository;

    public List<Metodopago> list(){
        return repository.list();
    }

    public Metodopago save(Metodopago o){
        return repository.save(o);
    }

    public Metodopago update(Metodopago o){
        Optional<Metodopago> res = repository.findById(o.getId());
        if(res.isPresent()){
            Metodopago metodo = res.get();
            metodo.setTipo(o.getTipo());
            metodo.setDescripcion(o.getDescripcion());

            return repository.save(metodo);
        }
        return null;
    }

    public boolean delete(Integer id){
        Optional<Metodopago> res = repository.findById(id);
        if(res.isPresent()){
            Metodopago metodo = res.get();
            metodo.setEliminado(1);
            repository.save(metodo);
            return true;
        }
        return false;
    }

    public Metodopago find(Integer id){
        return repository.find(id);
    }
}
