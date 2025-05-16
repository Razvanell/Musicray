package razvanell.musicrays.model.playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import razvanell.musicrays.model.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    @Query("SELECT playlist FROM Playlist playlist WHERE playlist.name = ?1")
    Optional<Playlist> findByName(String name);

    @Query("SELECT playlist FROM Playlist playlist WHERE playlist.user.id = :id")
    List<Playlist> findAllByUserId(Long id);

    @Query("SELECT playlist FROM Playlist playlist WHERE playlist.user = ?1 AND playlist.name = ?2")
    Optional<Playlist> findByUserAndName(User user, String name);

    @Modifying
    @Query("delete from Playlist playlist where playlist.id = ?1")
    void deleteById(Long playlistId);
}