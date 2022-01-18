package com.hugo.test.payload.request;

import javax.validation.constraints.NotBlank;

public class Player {
    @NotBlank
    private String name;

    @NotBlank
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
