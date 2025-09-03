package pe.com.farmaciadey.auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import pe.com.farmaciadey.auth.models.CustomUserDetails;
import pe.com.farmaciadey.auth.models.UserInfo;
import pe.com.farmaciadey.auth.repository.UserRepository;

@Component
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserInfo user = repository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Credenciales incorrectas");
        }
        return new CustomUserDetails(user);
    }

    public UserInfo findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public UserInfo save(UserInfo o) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        o.setPassword(passwordEncoder.encode(o.getPassword()));
        return repository.save(o);
    }
}
