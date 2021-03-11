package br.com.marinaluiza.contract.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class Interval {
    LocalDateTime start;
    LocalDateTime end;
}
