package com.kingleystudio.shopnchat.models.di;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private int id;
    private int dialog;
    private String message;
    private User sender;
    private long time;
}
