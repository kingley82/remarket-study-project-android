package com.kingleystudio.shopnchat.models.di;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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
