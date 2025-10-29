package pe.com.farmaciadey.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import pe.com.farmaciadey.auth.models.UserInfo;
import pe.com.farmaciadey.auth.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existe el usuario de prueba
        if (userRepository.findByUsername("test1") == null) {
            // Crear usuario de prueba para la app Android
            UserInfo testUser = new UserInfo();
            testUser.setIdentificacion("00000001");
            testUser.setNombres("Test");
            testUser.setApellidos("User");
            testUser.setTelefono("");
            testUser.setEmail("test1@example.com");
            testUser.setDireccion("");
            testUser.setRol("USUARIO");
            testUser.setUsername("test1");
            testUser.setPassword(passwordEncoder.encode("test1"));
            testUser.setEliminado(0);
            
            userRepository.save(testUser);
            System.out.println("✅ Usuario de prueba creado: test1/test1");
        } else {
            System.out.println("✅ Usuario de prueba ya existe: test1/test1");
        }

        // Crear un admin si no existe
        if (userRepository.findByUsername("admin") == null) {
            UserInfo adminUser = new UserInfo();
            adminUser.setIdentificacion("00000002");
            adminUser.setNombres("Admin");
            adminUser.setApellidos("User");
            adminUser.setTelefono("");
            adminUser.setEmail("admin@farmaciadey.com");
            adminUser.setDireccion("");
            adminUser.setRol("ADMIN");
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEliminado(0);
            
            userRepository.save(adminUser);
            System.out.println("✅ Usuario admin creado: admin/admin123");
        }
    }
}