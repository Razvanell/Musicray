package razvanell.musicrays.model.track;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TrackServiceTest {

    @Mock
    private TrackRepository trackRepository;

    private TrackService trackService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        trackService = new TrackService(trackRepository); // inject mock manually
    }

    @Test
    public void getTracksReturnsAllTracks() {
        List<Track> trackList = new ArrayList<>();
        when(trackRepository.findAll()).thenReturn(trackList);

        List<Track> actualTracks = trackService.getTracks();

        assertSame(trackList, actualTracks);
        assertTrue(actualTracks.isEmpty());
        verify(trackRepository).findAll();
    }

    @Test
    public void randomTrackReturnsNullWhenNoTracks() {
        when(trackRepository.count()).thenReturn(0L);
        // Also mock findAll to return empty page to avoid NPE
        when(trackRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        assertNull(trackService.randomTrack());

        verify(trackRepository).count();
        verify(trackRepository).findAll(any(Pageable.class));
    }

    @Test
    public void randomTrackReturnsNullWhenEmptyPage() {
        when(trackRepository.count()).thenReturn(3L);
        when(trackRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        assertNull(trackService.randomTrack());

        verify(trackRepository).count();
        verify(trackRepository).findAll(any(Pageable.class));
    }

    @Test
    public void randomTrackReturnsTrackWhenPresent() {
        Track track = new Track();
        track.setId(123L);
        track.setTitle("Dr");
        track.setArtist("Artist");
        track.setPath("Path");
        track.setPlaylists(new HashSet<>());

        List<Track> trackList = new ArrayList<>();
        trackList.add(track);

        when(trackRepository.count()).thenReturn(3L);
        when(trackRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(trackList));

        assertSame(track, trackService.randomTrack());

        verify(trackRepository).count();
        verify(trackRepository).findAll(any(Pageable.class));
    }

    @Test
    public void playTrackByIdReturnsTrackIfExists() {
        Track track = new Track();
        track.setId(123L);
        track.setTitle("Dr");
        track.setArtist("Artist");
        track.setPath("Path");
        track.setPlaylists(new HashSet<>());

        Optional<Track> optionalTrack = Optional.of(track);

        when(trackRepository.existsById(anyLong())).thenReturn(true);
        when(trackRepository.findById(anyLong())).thenReturn(optionalTrack);

        Optional<Track> result = trackService.playTrackById(123L);

        assertSame(optionalTrack, result);
        assertTrue(result.isPresent());

        verify(trackRepository).existsById(anyLong());
        verify(trackRepository).findById(anyLong());
    }

    @Test
    public void playTrackByIdThrowsWhenTrackNotExists() {
        when(trackRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> trackService.playTrackById(123L));

        verify(trackRepository).existsById(anyLong());
        verify(trackRepository, never()).findById(anyLong());
    }
}
