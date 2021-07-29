package br.com.marinaluiza.contract.services;

import br.com.marinaluiza.contract.model.DTO.DomainDTO;
import br.com.marinaluiza.contract.services.interfaces.ContractTransformationInterface;
import br.com.marinaluiza.contract.model.Obligation;
import br.com.marinaluiza.contract.model.Power;


import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ContractTransformationService implements ContractTransformationInterface {


    public JSONObject createDiagram(String file) throws Exception {
        Matcher matcherDomain = Pattern.compile("^Domain\\s(\\w*\\d*)\\n(.*)endDomain$", Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
        Matcher matcherContract = Pattern.compile("^Contract\\s(\\w*\\d*)\\s\\((.*)\\)\\nDeclarations", Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
        Matcher matcherPowers = Pattern.compile("^Powers(.*)Constraints$",Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
        Matcher matcherObligations = Pattern.compile("^Obligations(.*)SurvivingObls$",Pattern.DOTALL | Pattern.MULTILINE).matcher(file);
        Matcher matcherSurvivingObls = Pattern.compile("^SurvivingObls(.*)Powers$",Pattern.DOTALL | Pattern.MULTILINE).matcher(file);

        List<Obligation> obligations = null;
        List<Power> powers = null;
        String[] parts = null;
        String[] survivingObligations = null;
        if(matcherDomain.find()) {
            List<DomainDTO> domains = createDomain(matcherDomain);
            Set<String> roleDomains = domains.stream()
                .filter(domain -> domain.getSpecialization().equals("ROLE"))
                .map(DomainDTO::getName)
                .collect(Collectors.toSet());
                if(matcherContract.find()) {
                    Map<String, String> parameters = readContractParameters(matcherContract);
                    parts = parameters.values().stream()
                        .filter(param -> roleDomains.contains(param))
                        .toArray(String[]::new);
                }
        }
        
        if(matcherObligations.find()) {
            obligations = createObligations(matcherObligations, false);
        }
        if(matcherPowers.find()) {
            powers = createPowers(matcherPowers);
        }
        if(matcherSurvivingObls.find()) {
            List<Obligation> survivingList = createObligations(matcherSurvivingObls, true);
            survivingObligations = survivingList.stream().map(Obligation::getName).toArray(String[]::new);

        }
        CreateDiagramSymboleoService diagramService = new CreateDiagramSymboleoService(obligations, powers, survivingObligations, parts);
        return diagramService.createDiagramContract();

    }

    public List<Obligation> createObligations(Matcher matcher, boolean surviving) {
        List<Obligation> obligationsMap = new ArrayList<>();
        String obligations = matcher.group(1).trim();
        String[] obligationsArr = obligations.split(";");
        for (String obl : obligationsArr) {
            obl = obl.trim();
            String[] oblNameAndContent = obl.split(":");
            String[] oblPrevAndMain = oblNameAndContent[1].split("->");
            String obligationMain = oblPrevAndMain[oblPrevAndMain.length > 1 ? 1 : 0].replace(" ", "");
            Matcher obligationContent = Pattern.compile("^O\\((.*)\\)").matcher(obligationMain);
            if(obligationContent.find()) {
                Obligation obligation = new Obligation();
                //any comma not followed by words and parentheses
                String regex = ",(?!\\w+\\))";
                String[] obligationParts = obligationContent.group(1).replace(" ", "").split(regex);
                obligation.setName(oblNameAndContent[0].trim());
                obligation.setDebtor(obligationParts[0]);
                obligation.setCreditor(obligationParts[1]);
                obligation.setAntecedent(obligationParts[2]);
                obligation.setConsequent(obligationParts[3]);
                obligation.setSurviving(surviving);
                //trigger
                if(oblPrevAndMain.length > 1) {
                    obligation.setTrigger(oblPrevAndMain[0]);
                }
                obligationsMap.add(obligation);

            }

        }
        return obligationsMap;

    }

    public List<Power> createPowers(Matcher matcher) {
        List<Power> powersMap = new ArrayList<>();
        String powers = matcher.group(1).trim();
        String[] powerArr = powers.split(";");
        for (String pwr : powerArr) {
            String[] pwrNameAndContent = pwr.split(":");
            String[] pwrPrevAndMain = pwrNameAndContent[1].split("->");
            String powerMain = pwrPrevAndMain[pwrPrevAndMain.length > 1 ? 1 : 0].replace(" ", "");
            Matcher powerContent = Pattern.compile("^O\\((.*)\\)").matcher(powerMain);
            if(powerContent.find()) {
                Power power = new Power();
                String[] powerParts = powerContent.group(1).split(",(?!\\s\\w*\\))");
                power.setName(pwrNameAndContent[0]);
                power.setDebtor(powerParts[0]);
                power.setCreditor(powerParts[1]);
                power.setAntecedent(powerParts[2]);
                power.setConsequent(powerParts[3]);
                //trigger
                if(pwrPrevAndMain.length > 1) {
                    power.setTrigger(pwrPrevAndMain[0]);
                }
                powersMap.add(power);

            }

        }
        return powersMap;

    }


    public List<DomainDTO> createDomain(Matcher matchDomain) {
        List<DomainDTO> domains = new ArrayList<>();
        String domainBlock = matchDomain.group(2).trim().replace("\n", "");
        String[] domainLines = domainBlock.split(";");
        for(String line : domainLines) {
            Matcher matcherDomainLine = Pattern.compile("^(.*)isA(.*)with(.*)$").matcher(line);
            if(matcherDomainLine.find()) {
                String name = matcherDomainLine.group(1).trim();
                String specialization = matcherDomainLine.group(2).trim();
                String attributes = matcherDomainLine.group(3).trim();
                String[] attributesArray = attributes.split(",");
                Map<String, String> attributesDomain = new HashMap<>();
                for(String attribute : attributesArray) {
                    String[] attributeArray = attribute.split(":");
                    attributesDomain.put(attributeArray[0].trim(), attributeArray[1].trim());
                }
                DomainDTO domain = new DomainDTO();
                domain.setName(name);
                domain.setSpecialization(specialization);
                domain.setAttributes(attributesDomain);

                domains.add(domain);
            }
        }

        return domains;
        
    }

    public Map<String, String> readContractParameters(Matcher matchContract) {
        String parametersString = matchContract.group(2).trim();
        String[] parameters = parametersString.split(",");
        Map<String, String> paramsByName = new HashMap<>();
        for(String param : parameters) {
            String[] paramArray = param.split(":");
            paramsByName.put(paramArray[0].trim(), paramArray[1].trim());
        }
        return paramsByName;
    }

}
