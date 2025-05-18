package razvanell.musicrays.model.playlist;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import razvanell.musicrays.model.track.Track;
import razvanell.musicrays.model.track.TrackRepository;
import razvanell.musicrays.model.user.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    public List<Playlist> getPlaylists(Long userId) {
        return playlistRepository.findAllByUserId(userId);
    }

    public void postPlaylist(Playlist playlist, Long userId) {
        if (playlistRepository.findByName(playlist.getName()).isPresent()) {
            throw new IllegalStateException("A playlist with this name already exists");
        }
        playlist.setUser(userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User does not exist")));
        playlistRepository.save(playlist);
    }

    @Transactional
    public void deletePlaylist(Long playlistId) {
        if (!playlistRepository.existsById(playlistId)) {
            throw new IllegalStateException("Playlist does not exist in the database");
        }
        System.out.println("what the fuck");
        playlistRepository.deleteById(playlistId);
    }


    @Transactional
    public void putPlaylist(Playlist playlist) {
        if (playlist.getId() == null) {
            throw new IllegalArgumentException("Playlist ID must not be null when updating");
        }
        Playlist oldPlaylist = playlistRepository.findById(playlist.getId()).orElseThrow(() -> new IllegalStateException("Playlist does not exist"));
        if (playlist.getName() != null) {
            oldPlaylist.setName(playlist.getName());
        }
    }


    @Transactional
    public void addTrack(Playlist playlist, Long trackId) {
        Playlist oldPlaylist = playlistRepository.findById(playlist.getId()).orElseThrow(() -> new IllegalStateException("Playlist does not exist"));
        Track trackToBeAdded = trackRepository.findById(trackId).orElseThrow(() -> new IllegalStateException("Track does not exist"));
        oldPlaylist.getTracks().add(trackToBeAdded);
    }

    @Transactional
    public void removeTrack(Playlist playlist, Long trackId) {
        Playlist oldPlaylist = playlistRepository.findById(playlist.getId()).orElseThrow(() -> new IllegalStateException("Playlist does not exist"));
        Track trackToBeRemoved = trackRepository.findById(trackId).orElseThrow(() -> new IllegalStateException("Track does not exist in playlist"));
        oldPlaylist.getTracks().remove(trackToBeRemoved);
    }

}