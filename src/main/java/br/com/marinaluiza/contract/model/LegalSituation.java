package br.com.marinaluiza.contract.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LegalSituation extends Situation {
    //LegalSituations are propositions
    //Deve-se notar que as proposições de lógica de primeira ordem (FOL) descrevem situações.
    // Uma proposição pode ser um átomo como happens(event, point),
    // um predicado abreviado (por exemplo, happensBefore(event, t)),
    // um estado de uma obrigação/poder/contrato (suspended(O))
    // ou a ocorrência de um evento interno que altera seus estados. suspends(O)

    String situation;

}
