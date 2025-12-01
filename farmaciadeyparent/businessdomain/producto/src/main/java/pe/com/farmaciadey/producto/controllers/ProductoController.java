package pe.com.farmaciadey.producto.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pe.com.farmaciadey.producto.models.Producto;
import pe.com.farmaciadey.producto.models.responses.DataResponse;
import pe.com.farmaciadey.producto.services.ProductoService;

@RestController
@RequestMapping("/api/producto")
public class ProductoController {

    @Autowired
    ProductoService service;

        @Value("${app.base-url}")
        private String baseUrl;
    @Value("${file.upload-dir:/app/uploads}")
    private String uploadDir;

    @PostMapping(value = "/save-without-image")
    public ResponseEntity<Object> saveWithoutImage(@RequestBody Producto request) throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.save(request));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> save(
            @RequestPart("producto") Producto request,
            @RequestPart(name = "imagen", required = false) MultipartFile imagen) throws Exception {
        DataResponse response = new DataResponse();
        try {
            if (imagen != null) {
                String urlImage = uploadFile(imagen.getInputStream(),
                        UUID.randomUUID().toString() + imagen.getOriginalFilename());
                if (urlImage == null) {
                    response.setException("No se pudo guardar la imagen");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                request.setUrl(urlImage);
            }
            
            response.setDato(service.save(request));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/update-without-image/{id}")
    public ResponseEntity<Object> updateWithoutImage(@PathVariable("id") Integer id, @RequestBody Producto request)
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

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> update(@PathVariable("id") Integer id, @RequestPart("producto") Producto request,
            @RequestPart("imagen") MultipartFile imagen)
            throws Exception {
        DataResponse response = new DataResponse();
        System.out.println("ID: " + id);
        System.out.println("REQUEST: " + request);
        try {
            if (id != request.getId()) {
                response.setException("No se pudo actualizar el registro");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (imagen != null) {
                String urlImage = uploadFile(imagen.getInputStream(),
                        UUID.randomUUID().toString() + imagen.getOriginalFilename());
                if (urlImage == null) {
                    response.setException("No se pudo guardar la imagen");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

                request.setUrl(urlImage);
            }

            response.setDato(service.update(request));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/change/{id}")
    public ResponseEntity<Object> change(@PathVariable("id") Integer id, @RequestBody Integer cantidadComprada)
            throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.change(id, cantidadComprada));
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

    @PostMapping(value = "/search", consumes = "text/plain")
    public ResponseEntity<Object> search(@RequestBody String searchTerm) throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.search(searchTerm));
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

    private String uploadFile(InputStream file, String name) {

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = name;

        try (InputStream inputStream = file) {
            Files.copy(file, Paths.get(uploadDir + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);

            // Retornar la URL de acceso al archivo usando la variable baseUrl
            String fileUrl = baseUrl + "/api/producto/images/" + fileName;
            return fileUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
