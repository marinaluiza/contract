package br.com.marinaluiza.contract.services.interfaces;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.json.simple.JSONObject;

import br.com.marinaluiza.contract.model.Obligation;
import br.com.marinaluiza.contract.model.Power;
import br.com.marinaluiza.contract.model.DTO.DomainDTO;

public interface ContractTransformationInterface {
    
    public JSONObject createDiagram(String file) throws Exception;
    public List<Obligation> createObligations(Matcher matcher, boolean surviving);
    public List<Power> createPowers(Matcher matcher);
    public List<DomainDTO> createDomain(Matcher matchDomain);
    public Map<String, String> readContractParameters(Matcher matchContract);
}
