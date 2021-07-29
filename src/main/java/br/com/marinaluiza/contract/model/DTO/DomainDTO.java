package br.com.marinaluiza.contract.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class DomainDTO {

    String name;
    String specialization;
    Map<String, String> attributes;
}
