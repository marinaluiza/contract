package br.com.marinaluiza.contract.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class Event {

    EventEnum type;
    LocalDateTime time;

    public void trigger() {
        this.time = LocalDateTime.now();
    }

}
