package razvanell.musicrays;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import razvanell.musicrays.model.playlist.Playlist;
import razvanell.musicrays.model.playlist.PlaylistRepository;
import razvanell.musicrays.model.track.Track;
import razvanell.musicrays.model.track.TrackRepository;
import razvanell.musicrays.model.user.User;
import razvanell.musicrays.model.user.UserRepository;
import razvanell.musicrays.model.user.UserRole;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
@AllArgsConstructor
public class DatabaseUpdaterConfig {

    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final ConfigProperties configProp;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

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
                // No action on non-create ddl-auto
            };
        }
    }

    void addDefaultUsers() {
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

        if (listOfFiles == null) {
            System.err.println("Music files folder not found or empty.");
            return;
        }

        for (File file : listOfFiles) {
            try {
                if (file.isFile()) {
                    String[] fileinfo = file.getName().split(" - ");
                    if (fileinfo.length < 2) {
                        System.err.println("Skipping file with invalid name format: " + file.getName());
                        continue;
                    }
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
                System.err.println("Failed to process file: " + file.getName());
            }
        }
    }

    void addDefaultPlaylists() {
        Optional<User> adminOpt = userRepository.findByEmail("admin@user.com");
        Optional<User> userOpt = userRepository.findByEmail("user@user.com");

        if (adminOpt.isEmpty() || userOpt.isEmpty()) {
            System.err.println("Required users not found to create playlists.");
            return;
        }

        User admin = adminOpt.get();
        User user = userOpt.get();

        List<Playlist> defaultPlaylists = List.of(
                Playlist.builder().user(admin).name("Metal").build(),
                Playlist.builder().user(admin).name("Rock").build(),
                Playlist.builder().user(admin).name("Disco").build(),
                Playlist.builder().user(user).name("Electronic").build()
        );

        for (Playlist playlist : defaultPlaylists) {
            boolean exists = playlistRepository
                    .findByUserAndName(playlist.getUser(), playlist.getName())
                    .isPresent();
            if (!exists) {
                playlistRepository.save(playlist);
            }
        }
    }

    void addTracksToPlaylists() {
        // Find the playlists by user and name instead of ID
        Optional<User> adminOpt = userRepository.findByEmail("admin@user.com");
        if (adminOpt.isEmpty()) {
            System.err.println("Admin user not found, cannot add tracks to playlists.");
            return;
        }
        User admin = adminOpt.get();

        Optional<Playlist> metalOpt = playlistRepository.findByUserAndName(admin, "Metal");
        Optional<Playlist> rockOpt = playlistRepository.findByUserAndName(admin, "Rock");

        if (metalOpt.isEmpty() || rockOpt.isEmpty()) {
            System.err.println("Required playlists (Metal or Rock) not found.");
            return;
        }

        Playlist metal = metalOpt.get();
        Playlist rock = rockOpt.get();

        // Find tracks by artist/title instead of fixed IDs to avoid fragility
        Optional<Track> track1Opt = trackRepository.findByArtistAndTitle("SomeArtist1", "SomeTitle1");
        Optional<Track> track2Opt = trackRepository.findByArtistAndTitle("SomeArtist2", "SomeTitle2");
        Optional<Track> track4Opt = trackRepository.findByArtistAndTitle("SomeArtist4", "SomeTitle4");
        Optional<Track> track7Opt = trackRepository.findByArtistAndTitle("SomeArtist7", "SomeTitle7");

        // Replace the above with your actual artist/title or get tracks differently
        if (track1Opt.isEmpty() || track2Opt.isEmpty() || track4Opt.isEmpty() || track7Opt.isEmpty()) {
            System.err.println("One or more required tracks not found.");
            return;
        }

        Set<Track> metalTracks = metal.getTracks();
        metalTracks.add(track1Opt.get());
        metalTracks.add(track2Opt.get());
        metalTracks.add(track4Opt.get());

        Set<Track> rockTracks = rock.getTracks();
        rockTracks.add(track7Opt.get());

        playlistRepository.saveAll(List.of(metal, rock));
    }

}
