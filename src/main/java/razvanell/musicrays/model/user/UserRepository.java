package razvanell.musicrays.model.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT user FROM User user WHERE user.email = ?1")
    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User user SET user.enabled = TRUE WHERE user.email = ?1")
    void enableUser(String email);
}
