package br.com.marinaluiza.contract.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class Contract {

    List<Obligation> obligations;
    List<Obligation> survivingObligations;
    List<Power> powers;
    List<Role> roles;
    Boolean preCondition;
    Boolean postCondition;
    Asset asset;
    LocalDateTime effDate;
}
