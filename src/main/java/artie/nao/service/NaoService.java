package artie.nao.service;

import artie.generator.dto.bmle.BML;
import artie.nao.config.Constants;
import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALLeds;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.Arrays;

@Service
public class NaoService {

    @Value("${artie.robot.url}")
    private String robotUrl;


    /**
     * Function to deserialize the xml
     * @param xml
     * @return
     * @throws JAXBException
     */
    private BML deserializeXML(String xml) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(BML.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        return (BML) unmarshaller.unmarshal(reader);
    }

    /**
     * Function to execute the BMLE
     * @param xml
     * @throws JAXBException
     */
    public void executeBMLE(String xml) throws Exception {

        //1- Deserialization of the xml into a BMLe
        BML bmle = this.deserializeXML(xml);
        String[] args = new String[0];

        //2- Starts the application where we configure the next robot behavior
        Application application = new Application(args, this.robotUrl);

        //3- Sets the text the robot should say
        ALTextToSpeech tts = new ALTextToSpeech(application.session());
        tts.say(bmle.getSpeech().getText());

        //4- Sets the facial leds, getting the eyes group and setting for each led
        ALLeds leds = new ALLeds(application.session());
        Arrays.stream(Constants.EYES).forEach(led -> {
            try {
                //TODO: Set the number of seconds of the fade
                leds.fadeRGB(led, bmle.getFace().getLexeme(), Float.valueOf("2"));
            } catch (CallError | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        //5- Sets the posture
        ALRobotPosture posture = new ALRobotPosture(application.session());
        //TODO: Set the max speed fraction
        posture.goToPosture(bmle.getPosture().getLexeme(), Float.valueOf("0.5"));

    }
}
