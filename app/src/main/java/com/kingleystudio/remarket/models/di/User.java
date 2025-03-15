package com.kingleystudio.remarket.models.di;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    @JsonProperty("id")
    private int id;
    @JsonProperty("username")
    private String username;
}
