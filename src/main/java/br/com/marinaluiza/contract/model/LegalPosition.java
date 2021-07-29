package br.com.marinaluiza.contract.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LegalPosition {


    String debtor;
    String creditor;
    String antecedent;
    String consequent;
    String trigger;
    String name;
}
