package com.ebillson.nytimessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Ebillson GJ on 7/27/2017.
 */

public class Article implements Serializable {

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadLine() {
        return headLine;
    }

    public String getThumnNail() {
        return thumnNail;
    }




    public String getSnippet() {
        return snippet;
    }

    public String getLeadParagraph() {
        return leadParagraph;
    }

    public String getSource() {
        return source;
    }

    public SimpleDateFormat getPubDate() {
        return pubDate;
    }

    public String getNewsDesk() {
        return newsDesk;
    }


    String webUrl;
    String headLine;
    String thumnNail;
    String snippet;
    String leadParagraph;
    String source;
    SimpleDateFormat pubDate;
    String newsDesk;



    public  Article(JSONObject jsonObject){

        try{
            this.webUrl = jsonObject.getString("web_url");
            this.headLine = jsonObject.getJSONObject("headline").getString("main");
            this.newsDesk = jsonObject.getString("news_desk");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if(multimedia.length() > 0 ){
                JSONObject multimediaJson = multimedia.getJSONObject(0);

                this.thumnNail = "http://www.nytimes.com/" + multimediaJson.getString("url");

            } else{
                this.thumnNail = "";
            }
        }catch (JSONException e){

           e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray (JSONArray array){

        ArrayList<Article> results = new ArrayList<>();


        for (int x= 0; x<array.length(); x++) {

            try{
                results.add(new Article(array.getJSONObject(x)));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return results;
    }


}
