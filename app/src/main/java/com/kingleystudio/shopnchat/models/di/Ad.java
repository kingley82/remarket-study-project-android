package com.kingleystudio.shopnchat.models.di;

import org.json.JSONArray;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ad {
    private Integer id;
    private String title;
    private float price;
    private List<String> images;
    private String phone;
    private String description;
    private User seller;
    private String status;
}
