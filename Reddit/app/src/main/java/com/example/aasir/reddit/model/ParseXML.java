package com.example.aasir.reddit.model;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aasir on 7/9/2017.
 */

public class ParseXML {

    private static final String TAG = "ParseXML";
    private final String NONE = "NONE";

    private String tag;
    private String xml;
    private String endtag;

    public ParseXML(String tag, String xml) {
        this.tag = tag;
        this.xml = xml;
        this.endtag = NONE;
    }

    public ParseXML(String tag, String xml, String endtag) {
        this.tag = tag;
        this.xml = xml;
        this.endtag = endtag;
    }

    public List<String> parse(){

        List<String> result = new ArrayList<>();
        String[] splitXML;
        String marker;

        // Parsing subreddit feed
        if (endtag.equals(NONE)){
            marker = "\"";
            splitXML = xml.split(tag + marker);
        }

        // Parsing comment feed
        else {
            marker = endtag;
            splitXML = xml.split(tag);
        }


        int count = splitXML.length;

        // Parsing the CData to retrieve href values
        for(int i = 1; i < count; i++){
            String string = splitXML[i];

            int index = string.indexOf(marker);
            string = string.substring(0, index);

            result.add(string);
        }


        return result;
    }


}
