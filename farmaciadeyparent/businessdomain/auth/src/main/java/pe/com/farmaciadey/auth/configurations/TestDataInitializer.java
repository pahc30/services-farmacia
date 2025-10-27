package pe.com.farmaciadey.auth.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.com.farmaciadey.auth.models.UserInfo;
import pe.com.farmaciadey.auth.repository.UserRepository;

@Component
@Profile("test")
public class TestDataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Crear usuario de prueba simple si no existe
        String testUsername = "testuser";
        if (userRepository.findByUsername(testUsername) == null) {
            UserInfo user = new UserInfo();
            user.setUsername(testUsername);
            user.setPassword(passwordEncoder.encode("test123"));
            user.setEmail("test@test.com");
            user.setNombres("Test");
            user.setApellidos("User");
            user.setRol("ROLE_ADMIN");
            userRepository.save(user);
        }
    }
}