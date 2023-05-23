package artie.nao.service;

import artie.generator.dto.bmle.BML;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface NaoService {
    BML deserializeXML(String xml) throws JsonProcessingException;
    void executeBMLE(String xml) throws Exception;
}
