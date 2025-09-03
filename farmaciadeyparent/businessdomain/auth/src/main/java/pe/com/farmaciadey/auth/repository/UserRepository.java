package pe.com.farmaciadey.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.com.farmaciadey.auth.models.UserInfo;

public interface UserRepository extends JpaRepository<UserInfo, Integer> {
    UserInfo findByUsername(String username);
}
