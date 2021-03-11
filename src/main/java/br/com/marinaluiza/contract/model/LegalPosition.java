package br.com.marinaluiza.contract.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LegalPosition {

    Role debtor;
    Role creditor;
    LegalSituation antecedent;
    LegalSituation consequent;
}
