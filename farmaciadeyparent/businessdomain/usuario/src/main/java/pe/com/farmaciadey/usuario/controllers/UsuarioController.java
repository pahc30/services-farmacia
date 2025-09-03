package pe.com.farmaciadey.usuario.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.farmaciadey.usuario.models.Usuario;
import pe.com.farmaciadey.usuario.models.responses.DataResponse;
import pe.com.farmaciadey.usuario.services.UsuarioService;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService service;

    @PostMapping(value = "/save")
    public ResponseEntity<Object> save(@RequestBody Usuario request) throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.save(request));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/update/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Integer id, @RequestBody Usuario request)
            throws Exception {
        DataResponse response = new DataResponse();
        try {
            if (id != request.getId()) {
                response.setException("No se pudo actualizar el registro");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            response.setDato(service.update(request));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/list")
    public ResponseEntity<Object> list() throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.list());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/find/{id}")
    public ResponseEntity<Object> find(@PathVariable("id") Integer id) throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.find(id));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.delete(id));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
