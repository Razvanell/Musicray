package razvanell.musicrays.model.track;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import razvanell.musicrays.model.playlist.Playlist;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tracks")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Track implements Serializable {

    @Id
    @SequenceGenerator(name = "track_sequence", sequenceName = "track_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_sequence")
    private Long id;
    private String artist;
    private String title;
    private String path;
    @JsonIgnore
    @ManyToMany(mappedBy = "tracks", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @Fetch(value = FetchMode.SELECT)
    private Set<Playlist> playlists;

    /* Without this override the method creates a stackoverflow error due to many-to-many relationship.
     * Removing playlists from the return breaks the loop.
     * */
    @Override
    public int hashCode() {
        return Objects.hash(id, artist, title, path);
    }
}
