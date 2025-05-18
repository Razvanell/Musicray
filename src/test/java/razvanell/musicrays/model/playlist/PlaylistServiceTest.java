package razvanell.musicrays.model.playlist;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import razvanell.musicrays.model.track.Track;
import razvanell.musicrays.model.track.TrackRepository;
import razvanell.musicrays.model.user.User;
import razvanell.musicrays.model.user.UserRepository;
import razvanell.musicrays.model.user.UserRole;
import razvanell.musicrays.model.user.registration.token.ConfirmationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {PlaylistService.class})
@ExtendWith(SpringExtension.class)
public class PlaylistServiceTest {

    @Autowired
    private PlaylistService playlistService;

    @MockitoBean
    private PlaylistRepository playlistRepository;

    @MockitoBean
    private TrackRepository trackRepository;

    @MockitoBean
    private UserRepository userRepository;

    private User buildTestUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.org");
        user.setPassword("iloveyou");
        user.setImageUrl("https://example.org/example");
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        user.setLocked(true);
        user.setTokens(new ArrayList<>());
        user.setPlaylists(new ArrayList<>());
        return user;
    }

    private Playlist buildTestPlaylist(Long id, String name, User user) {
        Playlist playlist = new Playlist();
        playlist.setId(id);
        playlist.setName(name);
        playlist.setUser(user);
        playlist.setTracks(new HashSet<>());
        return playlist;
    }

    @Test
    public void testGetPlaylists() {
        List<Playlist> playlistList = new ArrayList<>();
        when(playlistRepository.findAllByUserId(any())).thenReturn(playlistList);

        List<Playlist> actualPlaylists = playlistService.getPlaylists(123L);

        assertSame(playlistList, actualPlaylists);
        assertTrue(actualPlaylists.isEmpty());
        verify(playlistRepository).findAllByUserId(any());
    }

    @Test
    public void testPostPlaylist_DuplicateNameThrowsException() {
        User user = buildTestUser(123L);
        Playlist existing = buildTestPlaylist(123L, "Name", user);
        when(playlistRepository.findByName("Name")).thenReturn(Optional.of(existing));
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));

        Playlist newPlaylist = new Playlist();
        newPlaylist.setName("Name");

        assertThrows(IllegalStateException.class, () -> playlistService.postPlaylist(newPlaylist, 123L));
        verify(playlistRepository).findByName("Name");
    }

    @Test
    public void testPostPlaylist_SuccessfulCreation() {
        User user = buildTestUser(123L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(playlistRepository.findByName("Name")).thenReturn(Optional.empty());

        Playlist toSave = new Playlist();
        toSave.setName("Name");

        Playlist saved = buildTestPlaylist(123L, "Name", user);
        when(playlistRepository.save(any())).thenReturn(saved);

        playlistService.postPlaylist(toSave, 123L);

        verify(playlistRepository).save(any());
        verify(playlistRepository).findByName("Name");
        verify(userRepository).findById(123L);
        assertEquals("Name", toSave.getName());
        assertEquals(user, toSave.getUser());
    }

    @Test
    public void testDeletePlaylist_Exists() {
        when(playlistRepository.existsById(any())).thenReturn(true);
        doNothing().when(playlistRepository).deleteById(any());

        playlistService.deletePlaylist(123L);

        verify(playlistRepository).existsById(123L);
        verify(playlistRepository).deleteById(123L);
    }

    @Test
    public void testDeletePlaylist_NotFoundThrows() {
        when(playlistRepository.existsById(any())).thenReturn(false);
        assertThrows(IllegalStateException.class, () -> playlistService.deletePlaylist(123L));
        verify(playlistRepository).existsById(123L);
    }

    @Test
    public void testPutPlaylist_ExistingPlaylist() {
        Playlist existing = buildTestPlaylist(123L, "Name", buildTestUser(123L));
        when(playlistRepository.findById(any())).thenReturn(Optional.of(existing));

        playlistService.putPlaylist(existing);

        verify(playlistRepository).findById(123L);
    }

    @Test
    public void testPutPlaylist_NotFoundThrows() {
        when(playlistRepository.findById(any())).thenReturn(Optional.empty());
        Playlist playlist = new Playlist();
        playlist.setId(123L);

        assertThrows(IllegalStateException.class, () -> playlistService.putPlaylist(playlist));
        verify(playlistRepository).findById(123L);
    }

    @Test
    public void testPutPlaylist_ReplacingUser() {
        Playlist existing = buildTestPlaylist(123L, "Name", buildTestUser(123L));
        when(playlistRepository.findById(any())).thenReturn(Optional.of(existing));

        Playlist updated = new Playlist(123L, "Name", new User(), new HashSet<>());

        playlistService.putPlaylist(updated);

        verify(playlistRepository).findById(123L);
    }

    @Test
    public void testAddTrackToPlaylist() {
        Track track = new Track();
        track.setId(123L);
        track.setTitle("Title");
        track.setArtist("Artist");
        track.setPath("Path");
        track.setPlaylists(new HashSet<>());

        when(trackRepository.findById(any())).thenReturn(Optional.of(track));

        Playlist playlist = buildTestPlaylist(456L, "Rock", buildTestUser(456L));
        playlist.setTracks(new HashSet<>());

        when(playlistRepository.findById(any())).thenReturn(Optional.of(playlist));
        when(playlistRepository.save(any())).thenReturn(playlist);

        playlistService.addTrack(playlist, 123L);

        verify(playlistRepository).findById(456L);
        verify(trackRepository).findById(123L);

        assertTrue(playlist.getTracks().contains(track));
    }
}
