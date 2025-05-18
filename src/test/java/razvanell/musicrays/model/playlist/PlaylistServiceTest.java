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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    public void testGetPlaylists() {
        ArrayList<Playlist> playlistList = new ArrayList<>();
        when(this.playlistRepository.findAllByUserId(any())).thenReturn(playlistList);
        List<Playlist> actualPlaylists = this.playlistService.getPlaylists(123L);
        assertSame(playlistList, actualPlaylists);
        assertTrue(actualPlaylists.isEmpty());
        verify(this.playlistRepository).findAllByUserId(any());
    }

    @Test
    public void testPostPlaylist() {
        User user = new User();
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.org");
        user.setPassword("iloveyou");
        user.setTokens(new ArrayList<ConfirmationToken>());
        user.setPlaylists(new ArrayList<Playlist>());
        user.setLocked(true);
        user.setImageUrl("https://example.org/example");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        user.setFirstName("Jane");

        Playlist playlist = new Playlist();
        playlist.setId(123L);
        playlist.setName("Name");
        playlist.setTracks(new HashSet<Track>());
        playlist.setUser(user);
        Optional<Playlist> ofResult = Optional.<Playlist>of(playlist);
        when(this.playlistRepository.findByName(anyString())).thenReturn(ofResult);
        assertThrows(IllegalStateException.class, () -> this.playlistService.postPlaylist(new Playlist(), 123L));
        verify(this.playlistRepository).findByName(anyString());
    }

    @Test
    public void testPostPlaylist2() {
        User user = new User();
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.org");
        user.setPassword("iloveyou");
        user.setTokens(new ArrayList<ConfirmationToken>());
        user.setPlaylists(new ArrayList<Playlist>());
        user.setLocked(true);
        user.setImageUrl("https://example.org/example");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        user.setFirstName("Jane");
        Optional<User> ofResult = Optional.<User>of(user);
        when(this.userRepository.findById((Long) any())).thenReturn(ofResult);

        User user1 = new User();
        user1.setLastName("Doe");
        user1.setEmail("jane.doe@example.org");
        user1.setPassword("iloveyou");
        user1.setTokens(new ArrayList<ConfirmationToken>());
        user1.setPlaylists(new ArrayList<Playlist>());
        user1.setLocked(true);
        user1.setImageUrl("https://example.org/example");
        user1.setId(123L);
        user1.setUserRole(UserRole.USER);
        user1.setEnabled(true);
        user1.setFirstName("Jane");

        Playlist playlist = new Playlist();
        playlist.setId(123L);
        playlist.setName("Name");
        playlist.setTracks(new HashSet<Track>());
        playlist.setUser(user1);
        when(this.playlistRepository.save((Playlist) any())).thenReturn(playlist);
        when(this.playlistRepository.findByName(anyString())).thenReturn(Optional.<Playlist>empty());
        Playlist playlist1 = new Playlist();
        this.playlistService.postPlaylist(playlist1, 123L);
        verify(this.playlistRepository).save((Playlist) any());
        verify(this.playlistRepository).findByName(anyString());
        verify(this.userRepository).findById((Long) any());
        assertEquals(
                "Playlist(id=null, name=null, user=User(id=123, firstName=Jane, lastName=Doe, email=jane.doe@example.org,"
                        + " password=iloveyou, imageUrl=https://example.org/example, userRole=USER, enabled=true, locked=true,"
                        + " playlists=[], tokens=[]), tracks=null)",
                playlist1.toString());
    }

    @Test
    public void testDeletePlaylist() {
        doNothing().when(this.playlistRepository).deleteById(any());
        when(this.playlistRepository.existsById(any())).thenReturn(true);
        this.playlistService.deletePlaylist(123L);
        verify(this.playlistRepository).existsById(any());
        verify(this.playlistRepository).deleteById(any());
    }

    @Test
    public void testDeletePlaylist2() {
        doNothing().when(this.playlistRepository).deleteById(any());
        when(this.playlistRepository.existsById(any())).thenReturn(false);
        assertThrows(IllegalStateException.class, () -> this.playlistService.deletePlaylist(123L));
        verify(this.playlistRepository).existsById(any());
    }

    @Test
    public void testPutUser() {
        User user = new User();
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.org");
        user.setPassword("iloveyou");
        user.setTokens(new ArrayList<>());
        user.setPlaylists(new ArrayList<>());
        user.setLocked(true);
        user.setImageUrl("https://example.org/example");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        user.setFirstName("Jane");

        Playlist playlist = new Playlist();
        playlist.setId(123L);
        playlist.setName("Name");
        playlist.setTracks(new HashSet<>());
        playlist.setUser(user);
        Optional<Playlist> ofResult = Optional.of(playlist);
        when(this.playlistRepository.findById(any())).thenReturn(ofResult);
        this.playlistService.putPlaylist(new Playlist());
        verify(this.playlistRepository).findById(any());
    }

    @Test
    public void testPutUser2() {
        when(this.playlistRepository.findById(any())).thenReturn(Optional.empty());

        Playlist playlist = new Playlist();
        playlist.setId(0L);
        assertThrows(IllegalStateException.class, () -> this.playlistService.putPlaylist(playlist));
        verify(this.playlistRepository).findById(any());
    }

    @Test
    public void testPutUser3() {
        User user = new User();
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.org");
        user.setPassword("iloveyou");
        user.setTokens(new ArrayList<>());
        user.setPlaylists(new ArrayList<>());
        user.setLocked(true);
        user.setImageUrl("https://example.org/example");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        user.setFirstName("Jane");

        Playlist playlist = new Playlist();
        playlist.setId(123L);
        playlist.setName("Name");
        playlist.setTracks(new HashSet<>());
        playlist.setUser(user);
        Optional<Playlist> ofResult = Optional.of(playlist);
        when(this.playlistRepository.findById(any())).thenReturn(ofResult);
        User user1 = new User();

        Playlist playlist1 = new Playlist(123L, "Name", user1, new HashSet<>());
        playlist1.setId(0L);
        this.playlistService.putPlaylist(playlist1);
        verify(this.playlistRepository).findById(any());
    }

    @Test
    public void testAddTrack() {
        Track track = new Track();
        track.setId(123L);
        track.setPlaylists(new HashSet<>());
        track.setTitle("Dr");
        track.setArtist("Artist");
        track.setPath("Path");
        Optional<Track> ofResult = Optional.of(track);
        when(this.trackRepository.findById(any())).thenReturn(ofResult);

        User user = new User();
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.org");
        user.setPassword("iloveyou");
        user.setTokens(new ArrayList<>());
        user.setPlaylists(new ArrayList<>());
        user.setLocked(true);
        user.setImageUrl("https://example.org/example");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        user.setFirstName("Jane");

        Playlist playlist = new Playlist();
        playlist.setId(123L);
        playlist.setName("Name");
        playlist.setTracks(new HashSet<>());
        playlist.setUser(user);
        Optional<Playlist> ofResult1 = Optional.of(playlist);
        when(this.playlistRepository.findById(any())).thenReturn(ofResult1);
        this.playlistService.addTrack(new Playlist(), 123L);
        verify(this.playlistRepository).findById(any());
        verify(this.trackRepository).findById(any());
    }

    @Test
    public void testAddTrack2() {
        when(this.trackRepository.findById(any())).thenReturn(Optional.empty());

        User user = new User();
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.org");
        user.setPassword("iloveyou");
        user.setTokens(new ArrayList<>());
        user.setPlaylists(new ArrayList<>());
        user.setLocked(true);
        user.setImageUrl("https://example.org/example");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        user.setFirstName("Jane");

        Playlist playlist = new Playlist();
        playlist.setId(123L);
        playlist.setName("Name");
        playlist.setTracks(new HashSet<>());
        playlist.setUser(user);
        Optional<Playlist> ofResult = Optional.of(playlist);
        when(this.playlistRepository.findById(any())).thenReturn(ofResult);
        assertThrows(IllegalStateException.class, () -> this.playlistService.addTrack(new Playlist(), 123L));
        verify(this.playlistRepository).findById(any());
        verify(this.trackRepository).findById(any());
    }

    @Test
    public void testAddTrack3() {
        Track track = new Track();
        track.setId(123L);
        track.setPlaylists(new HashSet<>());
        track.setTitle("Dr");
        track.setArtist("Artist");
        track.setPath("Path");
        Optional<Track> ofResult = Optional.of(track);
        when(this.trackRepository.findById(any())).thenReturn(ofResult);
        when(this.playlistRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> this.playlistService.addTrack(new Playlist(), 123L));
        verify(this.playlistRepository).findById(any());
    }

    @Test
    public void testRemoveTrack() {
        Track track = new Track();
        track.setId(123L);
        track.setPlaylists(new HashSet<>());
        track.setTitle("Dr");
        track.setArtist("Artist");
        track.setPath("Path");
        Optional<Track> ofResult = Optional.of(track);
        when(this.trackRepository.findById(any())).thenReturn(ofResult);

        User user = new User();
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.org");
        user.setPassword("iloveyou");
        user.setTokens(new ArrayList<>());
        user.setPlaylists(new ArrayList<>());
        user.setLocked(true);
        user.setImageUrl("https://example.org/example");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        user.setFirstName("Jane");

        Playlist playlist = new Playlist();
        playlist.setId(123L);
        playlist.setName("Name");
        playlist.setTracks(new HashSet<>());
        playlist.setUser(user);
        Optional<Playlist> ofResult1 = Optional.of(playlist);
        when(this.playlistRepository.findById(any())).thenReturn(ofResult1);
        this.playlistService.removeTrack(new Playlist(), 123L);
        verify(this.playlistRepository).findById(any());
        verify(this.trackRepository).findById(any());
    }

    @Test
    public void testRemoveTrack2() {
        when(this.trackRepository.findById(any())).thenReturn(Optional.empty());

        User user = new User();
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.org");
        user.setPassword("iloveyou");
        user.setTokens(new ArrayList<>());
        user.setPlaylists(new ArrayList<>());
        user.setLocked(true);
        user.setImageUrl("https://example.org/example");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        user.setFirstName("Jane");

        Playlist playlist = new Playlist();
        playlist.setId(123L);
        playlist.setName("Name");
        playlist.setTracks(new HashSet<>());
        playlist.setUser(user);
        Optional<Playlist> ofResult = Optional.of(playlist);
        when(this.playlistRepository.findById(any())).thenReturn(ofResult);
        assertThrows(IllegalStateException.class, () -> this.playlistService.removeTrack(new Playlist(), 123L));
        verify(this.playlistRepository).findById(any());
        verify(this.trackRepository).findById(any());
    }

    @Test
    public void testRemoveTrack3() {
        Track track = new Track();
        track.setId(123L);
        track.setPlaylists(new HashSet<>());
        track.setTitle("Dr");
        track.setArtist("Artist");
        track.setPath("Path");
        Optional<Track> ofResult = Optional.of(track);
        when(this.trackRepository.findById(any())).thenReturn(ofResult);
        when(this.playlistRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> this.playlistService.removeTrack(new Playlist(), 123L));
        verify(this.playlistRepository).findById(any());
    }
}