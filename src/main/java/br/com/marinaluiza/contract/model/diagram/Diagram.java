package br.com.marinaluiza.contract.model.diagram;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Diagram {

    String name;
    String initialState;
    Transition[] transitions;
    String[] states;

}
