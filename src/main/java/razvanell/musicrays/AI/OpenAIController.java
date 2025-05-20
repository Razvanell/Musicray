package razvanell.musicrays.AI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/openai")
public class OpenAIController {

    private final OpenAIService openAIService;

    public OpenAIController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @GetMapping("/info")
    public ResponseEntity<String> getInfo(@RequestParam String query) {
        String result = openAIService.getSongOrArtistInfo(query);
        return ResponseEntity.ok(result);
    }
}
