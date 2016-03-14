package com.ravneetg.lcautomatique.data;

import java.util.List;
import java.util.Map;

/**
 * Datastructure which is created after massaging data from
 * activity table for number of days like 7 days or 30 days.
 * <p/>
 * Created by khenush on 8/10/2015.
 */
public class ActivityOverPeriod {
    private double changeAccountTotal;
    private double changePaymentsReceived;
    private List<String> newNotes;
    // Key is the status and value is list of note id
    private Map<String, List<String>> noteStatusByStatus;

    /**
     * Oldest activity date used for the given time period.
     */
    private String oldestActivityDate;

    /**
     * Newest activity date for the given time period.
     */
    private String newestActivityDate;

    /**
     * Oldest activity date recorded in DB.
     */
    private String overallOldestActivityDate;

    public boolean isEmpty() {
        return (changeAccountTotal == 0
                && changePaymentsReceived == 0
                && (newNotes == null || newNotes.size() == 0)
                && (noteStatusByStatus == null || noteStatusByStatus.size() == 0));
    }

    public String getOverallOldestActivityDate() {
        return overallOldestActivityDate;
    }

    public void setOverallOldestActivityDate(String overallOldestActivityDate) {
        this.overallOldestActivityDate = overallOldestActivityDate;
    }

    public String getOldestActivityDate() {
        return oldestActivityDate;
    }

    public void setOldestActivityDate(String oldestActivityDate) {
        this.oldestActivityDate = oldestActivityDate;
    }

    public String getNewestActivityDate() {
        return newestActivityDate;
    }

    public void setNewestActivityDate(String newestActivityDate) {
        this.newestActivityDate = newestActivityDate;
    }

    public Map<String, List<String>> getNoteStatusByStatus() {
        return noteStatusByStatus;
    }

    public void setNoteStatusByStatus(Map<String, List<String>> noteStatusByStatus) {
        this.noteStatusByStatus = noteStatusByStatus;
    }

    public double getChangeAccountTotal() {
        return changeAccountTotal;
    }

    public void setChangeAccountTotal(double changeAccountTotal) {
        this.changeAccountTotal = changeAccountTotal;
    }

    public double getChangePaymentsReceived() {
        return changePaymentsReceived;
    }

    public void setChangePaymentsReceived(double changePaymentsReceived) {
        this.changePaymentsReceived = changePaymentsReceived;
    }

    public List<String> getNewNotes() {
        return newNotes;
    }

    public void setNewNotes(List<String> newNotes) {
        this.newNotes = newNotes;
    }
}
