package br.com.marinaluiza.contract.services;

import br.com.marinaluiza.contract.model.Contract;
import br.com.marinaluiza.contract.model.Obligation;
import br.com.marinaluiza.contract.model.diagram.Diagram;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@Service
public class CreateDiagramService {


    public String readJsonFile() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            FileReader file = new FileReader(classLoader.getResource("diagramObligation.json").getFile());
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(file);
            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject createDiagramByObligation(Map<String, Obligation> obligationsMap) throws ParseException {
        JSONObject contract = new JSONObject();
        JSONArray obligations = new JSONArray();
        JSONParser parser = new JSONParser();
        for (Map.Entry<String,Obligation> pair : obligationsMap.entrySet()) {
            Obligation obl = pair.getValue();
            String diagramJson = readJsonFile();

            diagramJson = diagramJson.replace("$name", pair.getKey());
            if(obl.getTrigger() != null) {
                diagramJson = diagramJson.replace("$trigger", obl.getTrigger());
                diagramJson = diagramJson.replace("$initial", "created");
            } else {
                diagramJson = diagramJson.replace("$initial", "initial");
            }
            diagramJson = diagramJson.replace("$antecedent", obl.getAntecedent());
            diagramJson = diagramJson.replace("$consequent", obl.getConsequent());
            JSONObject obligationJson = (JSONObject) parser.parse(diagramJson);
            obligations.add(obligationJson);
            contract.put("obligations", obligations);
        }
        return contract;
    }
}
