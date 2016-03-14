package com.ravneetg.lcautomatique.data.gson;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by khenush on 8/16/2015.
 */
public class ActivityQueryResult {

    private List<JSONObject> data;
    private String oldestActivityDate;
    private String newestActivityDate;

    public List<JSONObject> getData() {
        return data;
    }

    public void setData(List<JSONObject> data) {
        this.data = data;
    }

    public String getOldestActivityDate() {
        return oldestActivityDate;
    }

    public void setOldestActivityDate(String firstActivityDate) {
        this.oldestActivityDate = firstActivityDate;
    }

    public String getNewestActivityDate() {
        return newestActivityDate;
    }

    public void setNewestActivityDate(String lastActivityDate) {
        this.newestActivityDate = lastActivityDate;
    }
}
