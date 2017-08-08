package com.example.aasir.reddit.model.Account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Aasir on 8/7/2017.
 */

public class JSON {

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JSON{" +
                "data=" + data +
                '}';
    }
}
