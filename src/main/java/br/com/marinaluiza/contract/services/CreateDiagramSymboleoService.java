package br.com.marinaluiza.contract.services;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import br.com.marinaluiza.contract.model.LegalPosition;
import br.com.marinaluiza.contract.model.Obligation;
import br.com.marinaluiza.contract.model.Power;

@Service
public class CreateDiagramSymboleoService extends CreateDiagramService {

 
    Pattern oblViolationPattern = Pattern.compile("oVIOLATION\\((.+?)\\)|oVIOLATED\\((.+?)\\)");
    Pattern powerExertionPattern = Pattern.compile("pEXERTED\\((.+?)\\)|pEXERTION\\((.+?)\\)");
    Pattern suspendContractPattern = Pattern.compile("cSUSPENDED\\((.+?)\\)|cSUSPENSION\\((.+?)\\)");
    Pattern resumeContractPattern = Pattern.compile("cRESUMED\\((.+?)\\)");
    Pattern terminateContractPattern = Pattern.compile("cTERMINATED\\((.+?)\\)");
    Pattern revokePartyPattern = Pattern.compile("cREVOKED_PARTY\\((.+?)\\)");
    Pattern assignPartyPattern = Pattern.compile("cASSIGNED_PARTY\\((.+?)\\)");


    public CreateDiagramSymboleoService(List<Obligation> obligations, List<Power> powers, String[] survObligations, String[] parts) {
        super(obligations, powers, survObligations, parts);
    }


    public <T extends LegalPosition> String[] legalPositionsToBeCreated(List<T> legalPositions) {
        String[] oblToBeCreated = legalPositions
            .stream()
            .filter(item -> item.getTrigger() == null).map(LegalPosition::getName)
            .toArray(String[]::new);
        return oblToBeCreated;
    }

    public <T extends LegalPosition> String[] legalPositionsToBeActivatedWithContract(List<T> legalPositions) {
        String[] oblToBeCreated = legalPositions
            .stream()
            .filter(item -> item.getTrigger() == null && item.getAntecedent() == "TRUE")
            .map(LegalPosition::getName)
            .toArray(String[]::new);
        return oblToBeCreated;
    }

    public <T extends LegalPosition> Map<String, List<String[]>> legalPositionsToBeActivated(List<T> legalPositions) {
        Map<String, List<String[]>> mappedTriggeredPower = new HashMap<>();
        mappedTriggeredPower.put("obligation_unfulfilled", new ArrayList<String[]>());
        mappedTriggeredPower.put("condition", new ArrayList<String[]>());
        
        legalPositions
            .stream()
            .filter(item -> item.getTrigger() != null)
            .forEach(lp -> {
                String[] lpTriggerAndName = new String[]{lp.getTrigger(), lp.getName()};
                if(isPowerActivatedByObligationViolation(lp.getTrigger())) {
                    mappedTriggeredPower.get("obligation_unfulfilled").add(lpTriggerAndName);
                } else {
                    mappedTriggeredPower.get("condition").add(lpTriggerAndName);
                }
            });

        return mappedTriggeredPower;
    }

    private boolean isPowerActivatedByObligationViolation(String trigger) {
        Matcher oblViolationMatcher = oblViolationPattern.matcher(trigger);
        if(oblViolationMatcher.find()) {
            return true;
        } else {
            Pattern notEvent = Pattern.compile("not|NOT \\((.*)\\)");
            Matcher notEventMatcher = notEvent.matcher(trigger);
            if(notEventMatcher.find()) {
                Obligation obligationMatch = obligations.stream().filter(obl -> obl.getConsequent().equals(notEventMatcher.group(1))).findAny().orElse(null);
                if(obligationMatch != null) {
                    return true;
                }                
            }
        }
        return false;
    }


    private String findReplacementByPowerExertion(String pwrName) {
        Power power = powers.stream().filter(pwr -> pwr.getName().equals(pwrName)).findAny().orElse(null);
        Matcher oblViolationMatcher = oblViolationPattern.matcher(power.getTrigger());
        if(power != null && oblViolationMatcher.find()) {
            String oblName =  oblViolationMatcher.group(1);
            return oblName;
        }
        return "";
    }

    private void findTriggerOnPowers(Obligation obligation, HashMap<String,String> replacementMap) {
        Power power = powers.stream().filter(pwr -> pwr.getConsequent().equals(obligation.getTrigger())).findAny().orElse(null);
        if(power != null) {
            Matcher oblViolationMatcher = oblViolationPattern.matcher(power.getTrigger());
            if(oblViolationMatcher.find()) {
                String oblName =  oblViolationMatcher.group(1);
                replacementMap.put(obligation.getName(), oblName);
                oblTriggeredByOblViolation.add(new String[]{"unfulfilled " + oblName, obligation.getName()});

            } else {
                Pattern notEvent = Pattern.compile("not|NOT \\((.*)\\)");
                Matcher notEventMatcher = notEvent.matcher(power.getTrigger());
                if(notEventMatcher.find()) {
                    Obligation obligationMatch = obligations.stream().filter(obl -> obl.getConsequent().equals(notEventMatcher.group(1))).findAny().orElse(null);
                    if(obligationMatch != null) {
                        replacementMap.put(obligation.getName(), obligationMatch.getName());
                        oblTriggeredByOblViolation.add(new String[]{"unfulfilled " + obligationMatch.getName(), obligation.getName()});
                    }                
                }
            }
            
        }
    }

    public String[] powersThatSuspendContract() {
        String[] suspenderPwrs = powers
            .stream()
            .filter(item -> {
                Matcher suspenderMatcher = suspendContractPattern.matcher(item.getConsequent());
                return suspenderMatcher.find();
            })
            .map(LegalPosition::getName)
            .toArray(String[]::new);
        return suspenderPwrs;
    }

    public String[] powersThatResumeContract() {
        String[] resumerPwrs = powers
            .stream()
            .filter(item -> {
                Matcher resumerMatcher = resumeContractPattern.matcher(item.getConsequent());
                return resumerMatcher.find();
            })
            .map(LegalPosition::getName)
            .toArray(String[]::new);
        return resumerPwrs;
    }

    public String[] powersThatTerminateContract() {
        String[] terminatorPwrs = powers
            .stream()
            .filter(item -> {
                Matcher terminatorMatcher = terminateContractPattern.matcher(item.getConsequent());
                return terminatorMatcher.find();
            })
            .map(LegalPosition::getName)
            .toArray(String[]::new);
        return terminatorPwrs;
    }

    public List<String[]> unfulfilledObligations(List<String[]> obligationsToUnfulfill) {
        // String[][] obligationsToUnfulfill =
        List<String[]> newObligationsToUnfulfill = new ArrayList<>();
        for(String[] setObligations : obligationsToUnfulfill) {
            String[] newSet = new String[setObligations.length];
            for(int j = 0; j < setObligations.length; j++) {
                newSet[j] = "~" + setObligations[j];
            }
            newObligationsToUnfulfill.add(newSet);
        }
        
        return newObligationsToUnfulfill;
    }

    public List<String[]> fulfillObligations() {
        // get all with no triggers - 1st set
        
        List<String[]> setFulfilledObligations = new ArrayList<>();
        String[] arrayOfActiveObligations = legalPositionsToBeCreated(obligations);
        List<Obligation> triggeredObl = obligations.stream().filter(item -> item.getTrigger() != null).collect(Collectors.toList());
        HashMap<String, String> replacementObligationsMap = getReplacementObligationsMap(triggeredObl);

        List<Obligation> obligationsTriggeredByCondition = triggeredObl
            .stream()
            .filter(item -> !replacementObligationsMap.containsKey(item.getName()))
            .collect(Collectors.toList());
        List<String> obligationsTriggeredByConditionAsList = obligationsTriggeredByCondition
            .stream()
            .map(obligation -> obligation.getName())
            .collect(Collectors.toList());
        this.oblTriggeredByCondition = obligationsTriggeredByCondition
            .stream()
            .map(obligation -> {
                return new String[]{obligation.getTrigger(), obligation.getName()};
            })
            .collect(Collectors.toList());
            
        List<String> myList = new ArrayList<String>(Arrays.asList(arrayOfActiveObligations));
        myList.addAll(new ArrayList<String>(obligationsTriggeredByConditionAsList));
        String[] allActiveObligations = myList.toArray(new String[myList.size()]);
        setFulfilledObligations.add(allActiveObligations);


        replaceFulfilledObligations(replacementObligationsMap, triggeredObl, setFulfilledObligations);

        return setFulfilledObligations;   
    }

    private void replaceFulfilledObligations(HashMap<String, String> replacementObligationsMap, List<Obligation> obligations, List<String[]> setFulfilledObligations) {
        List<String[]> newSets = new ArrayList<>();
        List<Obligation> notFoundedObligations = new ArrayList<>();
        obligations.forEach(item -> {
            if(replacementObligationsMap.containsKey(item.getName())) {
                boolean founded = false;
                String oblToReplace = replacementObligationsMap.get(item.getName());
                for(String[] fullfilledObligations : setFulfilledObligations) {
                    int oblIndex = Arrays.asList(fullfilledObligations).indexOf(oblToReplace);
                    if(oblIndex >= 0) {
                        founded = true;
                        String[] newSet = Arrays.copyOf(fullfilledObligations, fullfilledObligations.length);
                        newSet[oblIndex] = item.getName();
                        newSets.add(newSet);
                    }
                    
                }
                if(!founded) {
                    notFoundedObligations.add(item);
                }
                setFulfilledObligations.addAll(newSets);
                
            }
        });

        if(notFoundedObligations.size() > 0) {
            replaceFulfilledObligations(replacementObligationsMap, notFoundedObligations, setFulfilledObligations);
        }
    }

    private HashMap<String, String> getReplacementObligationsMap(List<Obligation> triggeredObl) {
        HashMap<String, String> replacementMap = new HashMap<String, String>();
        triggeredObl.stream().forEach(item -> {
            Matcher oblViolationMatcher = oblViolationPattern.matcher(item.getTrigger());
            Matcher powerExertionMatcher = powerExertionPattern.matcher(item.getTrigger());

            if(oblViolationMatcher.find()) {
                String oblName =  oblViolationMatcher.group(1);
                replacementMap.put(item.getName(), oblName);
                oblTriggeredByOblViolation.add(new String[]{"unfulfilled " + oblName, item.getName()});
             } else if(powerExertionMatcher.find()) {
                 String pwrName =  powerExertionMatcher.group(1);
                 String oblToReplace = findReplacementByPowerExertion(pwrName);
                 replacementMap.put(item.getName(), oblToReplace);
                 oblTriggeredByPower.add(new String[]{"power" + pwrName, item.getName()});

            } else {
                 findTriggerOnPowers(item, replacementMap);
             }
        });
        return replacementMap;
    }

    public <T extends LegalPosition> String[] legalPositionsActivatedByContractSuspension(List<T> legalPositions) {
        String[] legalPositionResult = legalPositions
            .stream()
            .filter(item -> {
                if(item.getTrigger() != null) {
                    Matcher suspenderMatcher = suspendContractPattern.matcher(item.getTrigger());
                    return suspenderMatcher.find();
                }
                return false;

            })
            .map(LegalPosition::getName)
            .toArray(String[]::new);
        return legalPositionResult;
    }

    public List<String> powersThatRevokeParty() {
        List<String> revokePowers = new ArrayList<String>();
        powers.stream()
            .forEach(item -> {
                Matcher revokeMatcher = revokePartyPattern.matcher(item.getConsequent());
                if(revokeMatcher.find()) {
                    revokePowers.add("power" + item.getName());
                }
            });
        return revokePowers;
    }

    public List<String> powersThatAssignParty() {
        List<String> assignPowers = new ArrayList<String>();
        powers.stream()
            .forEach(item -> {
                Matcher assignMatcher = assignPartyPattern.matcher(item.getConsequent());
                if(assignMatcher.find()) {
                    assignPowers.add("power" + item.getName());
                }
            });
        return assignPowers;
    }

}
