package br.com.marinaluiza.contract.services;

import br.com.marinaluiza.contract.model.DTO.DomainDTO;
import br.com.marinaluiza.contract.model.Obligation;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ContractTransformationService {

    Map<String, DomainDTO> domains;
    CreateDiagramService diagramService;

    public ContractTransformationService(CreateDiagramService diagramService) {
        this.diagramService = diagramService;
    }
//        Matcher matcherDomain = Pattern.compile("^Domain\\s(\\w*\\d*)\\n(.*)endDomain$", Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
//        if(matcherDomain.find()) {
//            createDomainSpecialization(matcherDomain);
//        }
//        Matcher matcherContract = Pattern.compile("^Contract\\s(.*)\\s\\((.*)\\)$", Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
//        if(matcherContract.find()) {
//            readContractParameters(matcherContract);
//        }
//        Matcher matcherDeclarations = Pattern.compile("^Declarations(.*)Preconditions$",Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
//        Matcher matcherPre = Pattern.compile("^Preconditions(.*)Postconditions$",Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
//        Matcher matcherPos = Pattern.compile("^Postconditions(.*)Obligations$",Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
//        Matcher surv = Pattern.compile("^SurvivingObls(.*)Powers$",Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
//        Matcher powers = Pattern.compile("^Powers(.*)Constraints$",Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
//        Matcher constraints = Pattern.compile("^Constraints(.*)endContract$",Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
//        if(matcherDeclarations.find()) {
//
//        }

    public String createDiagram(String file) throws ClassNotFoundException {

        Matcher matcherObligations = Pattern.compile("^Obligations(.*)SurvivingObls$",Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
        if(matcherObligations.find()) {
            createObligations(matcherObligations);
        }

        return "";
    }

    private void createObligations(Matcher matcher) {
        Map <String, Obligation> obligationsMap = new HashMap<>();
        String obligations = matcher.group(0).trim();
        String[] obligationsArr = obligations.split(";");
        for (String obl : obligationsArr) {
            String[] oblNameAndContent = obl.split(":");
            String[] oblPrevAndMain = oblNameAndContent[1].split("->");
            String obligationMain = oblPrevAndMain[oblPrevAndMain.length > 1 ? 1 : 0].replace(" ", "");
            Matcher obligationContent = Pattern.compile("^O\\((.*)\\)").matcher(obligationMain);
            if(obligationContent.find()) {
                Obligation obligation = new Obligation();
                String[] obligationParts = obligationContent.group(0).split(",(?!\\s\\w*\\))");
                obligation.setDebtor(obligationParts[0]);
                obligation.setCreditor(obligationParts[1]);
                obligation.setAntecedent(obligationParts[2]);
                obligation.setConsequent(obligationParts[3]);
                //trigger
                if(oblPrevAndMain.length > 1) {
                    obligation.setTrigger(oblPrevAndMain[0]);
                }
                obligationsMap.put(oblNameAndContent[0], obligation);

            }

        }

    }


    private void createDomainSpecialization(Matcher matchDomain) throws ClassNotFoundException {
        //Matcher matcherDomain = Pattern.compile("^(.*)isA(.*)with(.*)$").matcher(line);
        String name = matchDomain.group(0).trim();
        String specialization = matchDomain.group(1).trim();
        String attributes = matchDomain.group(2).trim();

        String[] attributesArray = attributes.split(",");
        Map<String, String> attributesDomain = new HashMap<>();
        for(String attribute : attributesArray) {
            String[] attributeArray = attribute.split(":");
            attributesDomain.put(attributeArray[0].trim(), attributeArray[1].trim());
        }
        DomainDTO domain = new DomainDTO();
        domain.setSpecialization(specialization);
        domain.setAttributes(attributesDomain);

        domains.put(name, domain);
    }

    public void readContractParameters(Matcher matchContract) {
        String name = matchContract.group(0).trim();
        String parametersString = matchContract.group(1).trim();
        String[] parameters = parametersString.split(",");
        Map<String, String> paramsByName = new HashMap<>();
        for(String param : parameters) {
            String[] paramArray = param.split(":");
            paramsByName.put(paramArray[0].trim(), paramArray[1].trim());
        }

    }

    public void setDeclarationAtrributes(String line) {

    }

}
