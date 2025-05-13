package razvanell.musicrays;

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

@Configuration
@AllArgsConstructor
public class DatabaseUpdaterConfig {

    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    @Bean
    CommandLineRunner trackCommandLineRunner() {
        return args -> {
            addDefaultUsers();
            addTracks();
            addDefaultPlaylists();
            addTracksToPlaylists();
        };
    }

    void addDefaultUsers() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User adminUser = User.builder()
                .firstName("admin")
                .lastName("admin")
                .email("admin@user.com")
                .password(bCryptPasswordEncoder.encode("admin"))
                .imageUrl("https://toppng.com/uploads/preview/indir-lul-twitch-chat-emote-icon-scarf-11562913439oaksbcecxz.png")
                .userRole(UserRole.ADMIN)
                .enabled(true)
                .build();

        User userUser = User.builder()
                .firstName("user")
                .lastName("user")
                .email("user@user.com")
                .password(bCryptPasswordEncoder.encode("user"))
                .imageUrl("https://blog.cdn.own3d.tv/resize=fit:crop,height:400,width:600/5N9ww4tCTtWsdaQj51yS")
                .userRole(UserRole.USER)
                .enabled(true)
                .build();

        userRepository.saveAll(List.of(adminUser, userUser));
    }

    void addTracks() {
        File folder = new File("src/main/resources/musicfiles");
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        for (File file: listOfFiles) {
            try {
                if(file.isFile()) {
                    String[] fileinfo = file.getName().split(" - ");
                    Track track = Track.builder()
                            .artist(fileinfo[0])
                            .title(fileinfo[1].replaceFirst("[.][^.]+$", ""))
                            .path(file.getPath())
                            .build();
                    trackRepository.save(track);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    void addDefaultPlaylists() {
        User admin = userRepository.findById(1L).get();
        User user = userRepository.findById(2L).get();

        Playlist metal = Playlist.builder().user(admin).name("Metal").build();
        Playlist rock = Playlist.builder().user(admin).name("Rock").build();
        Playlist disco = Playlist.builder().user(admin).name("Disco").build();
        Playlist electronic = Playlist.builder().user(user).name("Electronic").build();

        playlistRepository.saveAll(List.of(metal, rock, disco, electronic));
    }

    @Transactional
    void addTracksToPlaylists() {
        Track track1 = trackRepository.findById(1L).get();
        Track track2 = trackRepository.findById(2L).get();
        Track track4 = trackRepository.findById(4L).get();
        Track track7 = trackRepository.findById(7L).get();

        Playlist metal = playlistRepository.findById(1L).get();
        Playlist rock = playlistRepository.findById(2L).get();

        metal.getTracks().addAll(List.of(track1, track2, track4));
        rock.getTracks().add(track7);
        playlistRepository.saveAll(List.of(metal, rock));
    }

}
