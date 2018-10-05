package org.softwire.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse
{
    @JsonProperty
    public String token;

    public LoginResponse(String token)
    {
        this.token = token;
    }
}
