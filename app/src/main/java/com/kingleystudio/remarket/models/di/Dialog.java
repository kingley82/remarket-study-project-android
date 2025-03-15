package com.kingleystudio.remarket.models.di;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dialog {
    private int id;
    private User member1;
    private User member2;
    private String last_message;
}
