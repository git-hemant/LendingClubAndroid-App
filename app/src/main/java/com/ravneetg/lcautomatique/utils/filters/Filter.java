package com.ravneetg.lcautomatique.utils.filters;

import com.ravneetg.lcautomatique.utils.DropdownValue;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Ravneet on 1/20/2015.
 */
public class Filter {



    public enum FilterType{
        ftChoice,
        ftNumber,
        ftRange
    }
    private String mCaption;
    private String mBaseFilterName;
    private FilterType mFilterType;
    private DropdownValue[] mFilterChoice;
    private boolean mHalfWidth = false;


    public Filter(String caption, String baseFilterName, FilterType filterType){
        this.mCaption = (caption);
        this.mBaseFilterName = (baseFilterName);
        this.mFilterType = filterType;
        this.mHalfWidth = false; //most of them are full width
    }


    public void setFilterChoice(DropdownValue[] mFilterChoice) {
        this.mFilterChoice = mFilterChoice;
    }

    public List<DropdownValue> getFilterChoice() {
        return Arrays.asList(mFilterChoice);
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public String getBaseFilterName() {
        return mBaseFilterName;
    }

    public FilterType getFilterType() {
        return mFilterType;
    }

    public void setHalfWidth(boolean halfWidth){
        mHalfWidth = halfWidth;
    }

    public boolean isHalfWidth() {
        return mHalfWidth;
    }


}
