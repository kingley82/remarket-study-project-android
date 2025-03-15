package com.kingleystudio.remarket.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    @JsonProperty(Types.PAYLOAD)
    private JsonNode payload;
    @JsonProperty(Types.EVENT)
    private String typeEvent;
}
