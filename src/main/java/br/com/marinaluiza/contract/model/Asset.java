package br.com.marinaluiza.contract.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Asset {

    Double quantity;
    Object quality;
    Party owner;

}
