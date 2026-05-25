package com.ducnv.wsschat.config;

import java.security.Principal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StompPrincipal implements Principal {

    private String username;

    public StompPrincipal(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }
}