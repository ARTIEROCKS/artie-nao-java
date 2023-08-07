package artie.nao.service;

import artie.generator.dto.bmle.BML;
import artie.nao.config.Constants;
import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALLeds;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class NaoServiceImpl implements NaoService {

    @Value("${artie.robot.url}")
    private String robotUrl;


    /**
     * Function to deserialize the xml
     * @param xml
     * @return
     * @throws JAXBException
     */
    public BML deserializeXML(String xml) throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        xmlMapper.registerModule(module);
        return xmlMapper.readValue(xml, BML.class);
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
        try {
            Application application = new Application(args, this.robotUrl);

            //3- Sets the text the robot should say
            ALTextToSpeech tts = new ALTextToSpeech(application.session());
            tts.say(bmle.getSpeech().getText());

            //TODO: Review the tone = the volume
            tts.setParameter("speed", Float.valueOf(bmle.getSpeech().getSpeed()));
            tts.setVolume(Float.valueOf(bmle.getSpeech().getTone()));

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

            //Launches the actions
            application.run();
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }

    }
}
