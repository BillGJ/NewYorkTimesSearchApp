package com.ebillson.nytimessearch;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Ebillson GJ on 7/28/2017.
 */

public class SearchSettings implements Serializable {








    SimpleDateFormat beginDate;

    public enum Sort {
        newest, oldest, none
    }
    Sort sortOrder;

    ArrayList<String> filters;

    public SearchSettings() {
        // apply default value to settings
        this.sortOrder = Sort.none;
        filters = new ArrayList<String>();
    }

    public Sort getSortOrder() {
        return sortOrder;
    }

    public SimpleDateFormat getBeginDate() {
        return beginDate;
    }

    public ArrayList<String> getFilters() {
        return filters;
    }

    public void setSortOrder(Sort sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setBeginDate(SimpleDateFormat beginDate) {
        this.beginDate = beginDate;
    }

    // Add a String to apply Filter
    public void addFilter(String filter) {
        filters.add(filter);
    }

    // generate a string formatted for NY times search filters using Lucene Syntax
    public String generateNewsDeskFiltersOR() {
        //string example: news_desk:("Arts" "Fashion & Style" "Sports")
        String luceneSyntax = "news_desk:(";

        for(String filter : filters) {
            luceneSyntax = luceneSyntax.concat("\""+filter+"\" ");
        }

        luceneSyntax = luceneSyntax.concat(")");

        return luceneSyntax;
    }

    // return a String of formatted Date yyyy-MM-dd for
    public String formatBeginDate() {
        return beginDate.format(beginDate.getCalendar().getTime());
    }













}
