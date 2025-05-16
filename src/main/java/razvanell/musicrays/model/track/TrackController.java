package razvanell.musicrays.model.track;

import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "api/track")
@AllArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @GetMapping(path = "/random")
    public ResponseEntity<Set<Track>> getFiveRandomTracks() {
        Set<Track> tracks = new LinkedHashSet<>();
        while(tracks.size() < 10) {
            tracks.add(trackService.randomTrack());
        }
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Track>> getTracks() {
        List<Track> tracks = trackService.getTracks();
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }

    @GetMapping(path = "/play/{trackId}")
    public ResponseEntity<InputStreamResource> playTrackById(@PathVariable("trackId") Long id) throws FileNotFoundException {
        String filePath = trackService.playTrackById(id).get().getPath();

        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(filePath);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Ranges", "bytes");
        headers.set("Content-Type", "audio/mpeg");
        headers.set("Content-Range", "bytes 50-30000000");
        headers.set("Content-Length", String.valueOf(file.length()));
        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }


}
