package pe.com.farmaciadey.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.com.farmaciadey.auth.models.UserInfo;
import pe.com.farmaciadey.auth.repository.UserRepository;
import pe.com.farmaciadey.auth.services.JwtService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@Profile("test")
public class TestTokenController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/token")
    public ResponseEntity<?> generateTestToken(@RequestParam String username) {
        UserInfo usuario = userRepository.findByUsername(username);
        if (usuario == null) {
            throw new RuntimeException("Usuario de prueba no encontrado");
        }

        String token = jwtService.GenerateToken(usuario.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}