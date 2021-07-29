package br.com.marinaluiza.contract.services;

import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.List;
import java.util.Map;

import br.com.marinaluiza.contract.model.LegalPosition;
import br.com.marinaluiza.contract.model.Obligation;
import br.com.marinaluiza.contract.model.Power;
import java.util.ArrayList;

public abstract class CreateDiagramService {

    List<Obligation> obligations;
    List<Power> powers;
    String[] parts;
    String[] survivingObligations;
    List<String[]> oblTriggeredByPower = new ArrayList<String[]>();
    List<String[]> oblTriggeredByOblViolation = new ArrayList<String[]>();
    List<String[]> oblTriggeredByCondition = new ArrayList<String[]>();

    
    public CreateDiagramService(List<Obligation> obligations, List<Power> powers, String[] survObligations, String[] parts) {
        this.obligations = obligations;
        this.powers = powers;
        this.survivingObligations = survObligations;
        this.parts = parts;
    }

    public JSONObject readJsonFile(String filename) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            FileReader file = new FileReader(classLoader.getResource(filename).getFile());
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(file);
            return (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject createDiagramContract() throws ParseException {
        JSONObject diagramJson = readJsonFile("diagram-3.json");
        JSONObject transitions = (JSONObject) diagramJson.get("transitions");

        //Create contract
        JSONObject create = (JSONObject) transitions.get("create_contract");
        create.put("obligations_created", legalPositionsToBeCreated(obligations));
        create.put("powers_created", legalPositionsToBeCreated(powers));
        create.put("parties_to_be_included", parts);

        //Activate contract
        JSONObject activate = (JSONObject) transitions.get("activate_contract");
        activate.put("obligations_activated", legalPositionsToBeActivatedWithContract(obligations));
        activate.put("powers_activated", legalPositionsToBeActivatedWithContract(powers));

        List<String[]> setOfFulfillObligations = fulfillObligations();

        //Activate obligations
        JSONObject activateObl = (JSONObject) transitions.get("activate_obligation");
        if(oblTriggeredByPower != null && oblTriggeredByPower.size() > 0) {
            activateObl.put("obligations_to_be_activated_by_powers", oblTriggeredByPower);
        }
        if(oblTriggeredByOblViolation != null && oblTriggeredByOblViolation.size() > 0) {
            activateObl.put("obligations_to_be_activated_by_unfulfilled_obligations", oblTriggeredByOblViolation);
        }
        if(oblTriggeredByCondition != null && oblTriggeredByCondition.size() > 0) {
            activateObl.put("obligations_to_be_activated_by_conditions", oblTriggeredByCondition);
        }

        //Activate powers
        JSONObject activatePwr = (JSONObject) transitions.get("activate_power");
        Map<String, List<String[]>> mapPowersToBeActivated = legalPositionsToBeActivated(powers);
        activatePwr.put("powers_to_be_activated_by_unfulfilled_obligations", 
            mapPowersToBeActivated.get("obligation_unfulfilled"));
        activatePwr.put("powers_to_be_activated_by_conditions", 
            mapPowersToBeActivated.get("condition"));


        JSONObject suspendContract = (JSONObject) transitions.get("suspend_contract");
        suspendContract.put("powers_that_suspend_the_contract", powersThatSuspendContract());
        suspendContract.put("obligations_to_be_activated", legalPositionsActivatedByContractSuspension(obligations));
        suspendContract.put("powers_to_be_activated", legalPositionsActivatedByContractSuspension(powers));


        JSONObject resumeContract = (JSONObject) transitions.get("resume_contract");
        resumeContract.put("powers_that_resume_the_contract", powersThatResumeContract());
        
        
        JSONObject fulfillContract = (JSONObject) transitions.get("fulfill_active_obligations");
        fulfillContract.put("obligations_fulfilled", setOfFulfillObligations);
        fulfillContract.put("surviving_obligations_to_be_activated", survivingObligations);

        JSONObject termninateContract = (JSONObject) transitions.get("terminate_contract");
        termninateContract.put("obligations_unfulfilled", unfulfilledObligations(setOfFulfillObligations));
        termninateContract.put("powers_that_terminate_the_contract", powersThatTerminateContract());

        JSONObject revokeParty = (JSONObject) transitions.get("revoke_party");
        revokeParty.put("powers_that_revoke_party", powersThatRevokeParty());

        JSONObject assingParty = (JSONObject) transitions.get("assign_party");
        assingParty.put("powers_that_assign_party", powersThatAssignParty());


        return diagramJson;
    }

    public abstract <T extends LegalPosition> String[] legalPositionsToBeCreated(List<T> legalPositions);
    public abstract <T extends LegalPosition> String[] legalPositionsToBeActivatedWithContract(List<T> legalPositions);
    public abstract <T extends LegalPosition> Map<String, List<String[]>> legalPositionsToBeActivated(List<T> legalPositions);
    public abstract String[] powersThatSuspendContract();
    public abstract String[] powersThatResumeContract();
    public abstract String[] powersThatTerminateContract();
    public abstract List<String> powersThatRevokeParty();
    public abstract List<String> powersThatAssignParty();
    public abstract List<String[]> unfulfilledObligations(List<String[]> obligationsToUnfulfill);
    public abstract List<String[]> fulfillObligations();
    public abstract <T extends LegalPosition> String[] legalPositionsActivatedByContractSuspension(List<T> legalPositions);
}
