package br.com.marinaluiza.contract.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Obligation {

//    Boolean surviving;
//    Role debtor;
//    Role creditor;
//    LegalSituation antecedent;
//    LegalSituation consequent;
//    LegalSituation trigger;

    Boolean surviving;
    String debtor;
    String creditor;
    String antecedent;
    String consequent;
    String trigger;

}
