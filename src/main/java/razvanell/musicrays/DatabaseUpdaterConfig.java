package razvanell.musicrays;

import org.springframework.beans.factory.annotation.Autowired;
import razvanell.musicrays.model.playlist.Playlist;
import razvanell.musicrays.model.playlist.PlaylistRepository;
import razvanell.musicrays.model.track.Track;
import razvanell.musicrays.model.track.TrackRepository;
import razvanell.musicrays.model.user.User;
import razvanell.musicrays.model.user.UserRepository;
import razvanell.musicrays.model.user.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.transaction.Transactional;
import java.io.File;
import java.util.List;
import java.util.Set;

@Configuration
@AllArgsConstructor
public class DatabaseUpdaterConfig {

    @Autowired
    ConfigProperties configProp;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    @Bean
    CommandLineRunner trackCommandLineRunner() {
        String ddlAuto = configProp.getConfigValue("spring.jpa.hibernate.ddl-auto");
        if ("create".equals(ddlAuto) || "create-drop".equals(ddlAuto)) {
            return args -> {
                addDefaultUsers();
                addTracks();
                addDefaultPlaylists();
                addTracksToPlaylists();
            };
        } else {
            return args -> {
            };
        }

    }

    void addDefaultUsers() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (userRepository.findByEmail("admin@user.com").isEmpty()) {
            User adminUser = User.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .email("admin@user.com")
                    .password(bCryptPasswordEncoder.encode("admin"))
                    .imageUrl("https://toppng.com/uploads/preview/indir-lul-twitch-chat-emote-icon-scarf-11562913439oaksbcecxz.png")
                    .userRole(UserRole.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(adminUser);
        }

        if (userRepository.findByEmail("user@user.com").isEmpty()) {
            User userUser = User.builder()
                    .firstName("user")
                    .lastName("user")
                    .email("user@user.com")
                    .password(bCryptPasswordEncoder.encode("user"))
                    .imageUrl("https://blog.cdn.own3d.tv/resize=fit:crop,height:400,width:600/5N9ww4tCTtWsdaQj51yS")
                    .userRole(UserRole.USER)
                    .enabled(true)
                    .build();
            userRepository.save(userUser);
        }
    }


    void addTracks() {
        File folder = new File("src/main/resources/musicfiles");
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        for (File file : listOfFiles) {
            try {
                if (file.isFile()) {
                    String[] fileinfo = file.getName().split(" - ");
                    String artist = fileinfo[0].trim();
                    String title = fileinfo[1].replaceFirst("[.][^.]+$", "").trim();

                    // Check if track already exists
                    if (trackRepository.findByArtistAndTitle(artist, title).isEmpty()) {
                        Track track = Track.builder()
                                .artist(artist)
                                .title(title)
                                .path(file.getPath())
                                .build();
                        trackRepository.save(track);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    void addDefaultPlaylists() {
        User admin = userRepository.findById(1L).orElseThrow();
        User user = userRepository.findById(2L).orElseThrow();

        List<Playlist> defaultPlaylists = List.of(
                Playlist.builder().user(admin).name("Metal").build(),
                Playlist.builder().user(admin).name("Rock").build(),
                Playlist.builder().user(admin).name("Disco").build(),
                Playlist.builder().user(user).name("Electronic").build()
        );

        // Check if playlist already exists
        for (Playlist playlist : defaultPlaylists) {
            boolean exists = playlistRepository
                    .findByUserAndName(playlist.getUser(), playlist.getName())
                    .isPresent();
            if (!exists) {
                playlistRepository.save(playlist);
            }
        }
    }


    @Transactional
    void addTracksToPlaylists() {
        Track track1 = trackRepository.findById(1L).orElseThrow();
        Track track2 = trackRepository.findById(2L).orElseThrow();
        Track track4 = trackRepository.findById(4L).orElseThrow();
        Track track7 = trackRepository.findById(7L).orElseThrow();

        Playlist metal = playlistRepository.findById(1L).orElseThrow();
        Playlist rock = playlistRepository.findById(2L).orElseThrow();

        // Set.add() prevents duplicates
        Set<Track> metalTracks = metal.getTracks();
        metalTracks.add(track1);
        metalTracks.add(track2);
        metalTracks.add(track4);

        Set<Track> rockTracks = rock.getTracks();
        rockTracks.add(track7);

        playlistRepository.saveAll(List.of(metal, rock));
    }



}
