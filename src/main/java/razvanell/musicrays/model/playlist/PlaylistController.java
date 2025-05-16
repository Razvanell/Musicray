package razvanell.musicrays.model.playlist;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/playlist")
@AllArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping(path = "user/{userId}")
    public ResponseEntity<List<Playlist>> getUserPlaylists(@PathVariable Long userId) {
        List<Playlist> playlists = playlistService.getPlaylists(userId);
        return new ResponseEntity<>(playlists, HttpStatus.OK);
    }

    @PostMapping(path = "post/{userId}")
    public void postPlaylist(@RequestBody Playlist playlist, @PathVariable Long userId) {
        playlistService.postPlaylist(playlist, userId);
    }

    @DeleteMapping(path = "delete/{playlistId}")
    public ResponseEntity<?> deletePlaylist(@PathVariable("playlistId") Long playlistId) {
        playlistService.deletePlaylist(playlistId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "put")
    public ResponseEntity<?> putPlaylist(@RequestBody Playlist playlist) {
        if (playlist.getId() == null) {
            return new ResponseEntity<>("Playlist ID must not be null", HttpStatus.BAD_REQUEST);
        }
        playlistService.putPlaylist(playlist);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PutMapping(path = "add-track-to-playlist/{trackId}")
    public ResponseEntity<?> addTrack(@RequestBody Playlist playlist, @PathVariable Long trackId) {
        playlistService.addTrack(playlist, trackId);
        System.out.println("Received track " + trackId + " playlist: " + playlist);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "remove-track-from-playlist/{trackId}")
    public ResponseEntity<?> removeTrack(@RequestBody Playlist playlist, @PathVariable Long trackId) {
        playlistService.removeTrack(playlist, trackId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
