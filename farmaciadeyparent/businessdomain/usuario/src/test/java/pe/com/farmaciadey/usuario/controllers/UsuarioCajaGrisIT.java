package pe.com.farmaciadey.usuario.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.farmaciadey.usuario.models.Usuario;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsuarioCajaGrisIT {
    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCrearYListarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdentificacion("999");
        usuario.setNombres("Caja Gris");
        usuario.setApellidos("Test");
        usuario.setTelefono("123456789");
        usuario.setEmail("gris@test.com");
        usuario.setDireccion("Calle Gris");
        usuario.setRol("USER");
        usuario.setUsername("grisuser");
        usuario.setPassword("grispass");
        usuario.setEliminado(0);

        // Crear usuario
        webTestClient.post().uri("/api/usuario/save")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(usuario)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.dato.username").isEqualTo("grisuser");

        // Listar usuarios y verificar que existe
        webTestClient.post().uri("/api/usuario/list")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.dato[?(@.username=='grisuser')]").exists();
    }
}
