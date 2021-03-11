package br.com.marinaluiza.contract.model;

public enum PrimitivePredicates {

    WITHIN("within"), //  situation s holds when event e happens
    OCCURS("occurs"), //  situation s holds during the whole interval T, not just in any of its subintervals
    INITIATES("initiates"), //    event e brings about situation s
    TERMINATES("terminates"), //    event e terminates situation s
    HAPPENS("happens"),
    HAPPENS_BEFORE("happensBefore");


    PrimitivePredicates(String predicate) {

    }
}
