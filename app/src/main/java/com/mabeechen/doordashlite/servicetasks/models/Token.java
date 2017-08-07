package com.mabeechen.doordashlite.servicetasks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Encapsulates token response from service
 *
 * @author mabeechen
 * @since 8-4-17
 */
public class Token {
    @SerializedName("token")
    @Expose
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String city) {
        this.token = city;
    }
}
