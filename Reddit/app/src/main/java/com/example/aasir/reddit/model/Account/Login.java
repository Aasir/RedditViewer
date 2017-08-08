package com.example.aasir.reddit.model.Account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Aasir on 8/7/2017.
 */

public class Login {

    @SerializedName("json")
    @Expose
    private JSON json;

    public JSON getJSON() {
        return json;
    }

    public void setJSON(JSON json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "Login{" +
                "json=" + json +
                '}';
    }
}
