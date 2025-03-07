package blog.security.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import blog.security.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	Optional<User> findByProviderAndProviderId(String provider, String providerId);
}