package artie.nao;

import artie.generator.dto.bmle.BML;
import artie.nao.service.NaoService;
import artie.nao.service.NaoServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertFalse;

public class NaoServiceTest {
    private NaoService naoService;

    @BeforeEach
    void setUp() throws Exception {
        naoService = new NaoServiceImpl();
    }

    @Test
    void deserializeXMLTest() throws JsonProcessingException {
        String xml = "<bml character=\"603fcf4b27b5975142462a53\"><posture lexeme=\"stand\"/><gaze target=\"603fcf4b27b5975142462a53\"/><face lexeme=\"yellow\"/><gesture lexeme=\"arms_akimbo\"/><bmle-speech tone=\"high\" speed=\"high\"><text>ask_help</text></bmle-speech></bml>";
        BML bmle = naoService.deserializeXML(xml);
        assertFalse("BMLE is null", bmle==null);
    }
}
