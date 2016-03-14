package com.ravneetg.lcautomatique.data;

/**
 * Created by khenush on 10/30/2015.
 */
public class ReturnRateInfo {
    private double receivedPrincipal;
    private double outstandingPrincipal;
    private double interestEarned;
    private double lostPrincipal;

    public double getReceivedPrincipal() {
        return receivedPrincipal;
    }

    public void setReceivedPrincipal(double receivedPrincipal) {
        this.receivedPrincipal = receivedPrincipal;
    }

    public double getOutstandingPrincipal() {
        return outstandingPrincipal;
    }

    public void setOutstandingPrincipal(double outstandingPrincipal) {
        this.outstandingPrincipal = outstandingPrincipal;
    }

    public double getInterestEarned() {
        return interestEarned;
    }

    public void setInterestEarned(double interestEarned) {
        this.interestEarned = interestEarned;
    }

    public double getLostPrincipal() {
        return lostPrincipal;
    }

    public void setLostPrincipal(double lostPrincipal) {
        this.lostPrincipal = lostPrincipal;
    }

    public double calculateReturnRate() {
        double totalPrincipal = receivedPrincipal + outstandingPrincipal;
        return ( (interestEarned - lostPrincipal) / totalPrincipal) * 100;
    }
}
