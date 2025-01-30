package artie.nao.service;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.aldebaran.qi.helper.proxies.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SpeechRecognitionService {

    @Value("${artie.robot.url}")
    private String robotUrl;

    private ALMemory memory;
    private ALSpeechRecognition recog;

    @PostConstruct
    public void init() throws Exception {
        String[] args = new String[0];
        Application application = new Application(args, robotUrl);

        this.memory = new ALMemory(application.session());
        this.recog = new ALSpeechRecognition(application.session());

        initialize();
    }

    private void initialize() throws CallError, InterruptedException {

        List<String> vocabulary = new ArrayList<>(Arrays.asList("Si", "No"));
        recog.unsubscribe("WordRecognized");
        recog.setAudioExpression(true);
        recog.setVisualExpression(true);
        recog.setVocabulary(vocabulary, true);
        recog.subscribe("WordRecognized");
    }

    @Scheduled(fixedRate = 100) // Executes each 100ms
    public void processSpeechRecognition() throws CallError, InterruptedException {

        Boolean speechDetected = (Boolean) memory.getData("SpeechDetected");

        if (speechDetected != null && speechDetected) {

            Object rawData = memory.getData("WordRecognized");

            if (rawData instanceof List<?> rawList) {
                List<Object> words = new ArrayList<>(rawList);
                if (!words.isEmpty()) {
                    String word = (String) words.get(0);
                    float probability = ((Number) words.get(1)).floatValue();

                    System.out.println("Word detected: " + word + " (Probability: " + probability + ")");

                    if (!word.isEmpty() && probability > 0.2) {
                        handleRecognizedWord(word);
                    }
                }
            }
        }
    }

    private void handleRecognizedWord(String word) {
        System.out.println("Recognized word: " + word);
    }
}
