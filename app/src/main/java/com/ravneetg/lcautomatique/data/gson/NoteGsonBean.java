package com.ravneetg.lcautomatique.data.gson;

import com.google.gson.annotations.Expose;

/**
 * Created by khenush on 6/30/2015.
 */
public class NoteGsonBean {

    @Expose
    private Integer loanId;
    @Expose
    private Integer noteId;
    @Expose
    private Integer orderId;
    @Expose
    private Float interestRate;
    @Expose
    private Integer loanLength;
    @Expose
    private String loanStatus;
    @Expose
    private String grade;
    @Expose
    private String subGrade;
    @Expose
    private Integer loanAmount;
    @Expose
    private Integer noteAmount;
    @Expose
    private Float paymentsReceived;
    @Expose
    private String issueDate;
    @Expose
    private String orderDate;
    @Expose
    private String loanStatusDate;

    /**
     * @return The loanId
     */
    public Integer getLoanId() {
        return loanId;
    }

    /**
     * @param loanId The loanId
     */
    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    /**
     * @return The noteId
     */
    public Integer getNoteId() {
        return noteId;
    }

    /**
     * @param noteId The noteId
     */
    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    /**
     * @return The orderId
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * @param orderId The orderId
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * @return The interestRate
     */
    public Float getInterestRate() {
        return interestRate;
    }

    /**
     * @param interestRate The interestRate
     */
    public void setInterestRate(Float interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * @return The loanLength
     */
    public Integer getLoanLength() {
        return loanLength;
    }

    /**
     * @param loanLength The loanLength
     */
    public void setLoanLength(Integer loanLength) {
        this.loanLength = loanLength;
    }

    /**
     * @return The loanStatus
     */
    public String getLoanStatus() {
        return loanStatus;
    }

    /**
     * @param loanStatus The loanStatus
     */
    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    /**
     * @return The grade
     */
    public String getGrade() {
        return grade;
    }

    /**
     * @param grade The grade
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * @return The subGrade
     */
    public String getSubGrade() {
        return subGrade;
    }

    /**
     * @param subGrade The subGrade
     */
    public void setSubGrade(String subGrade) {
        this.subGrade = subGrade;
    }

    /**
     * @return The loanAmount
     */
    public Integer getLoanAmount() {
        return loanAmount;
    }

    /**
     * @param loanAmount The loanAmount
     */
    public void setLoanAmount(Integer loanAmount) {
        this.loanAmount = loanAmount;
    }

    /**
     * @return The noteAmount
     */
    public Integer getNoteAmount() {
        return noteAmount;
    }

    /**
     * @param noteAmount The noteAmount
     */
    public void setNoteAmount(Integer noteAmount) {
        this.noteAmount = noteAmount;
    }

    /**
     * @return The paymentsReceived
     */
    public Float getPaymentsReceived() {
        return paymentsReceived;
    }

    /**
     * @param paymentsReceived The paymentsReceived
     */
    public void setPaymentsReceived(Float paymentsReceived) {
        this.paymentsReceived = paymentsReceived;
    }

    /**
     * @return The issueDate
     */
    public String getIssueDate() {
        return issueDate;
    }

    /**
     * @param issueDate The issueDate
     */
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * @return The orderDate
     */
    public String getOrderDate() {
        return orderDate;
    }

    /**
     * @param orderDate The orderDate
     */
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * @return The loanStatusDate
     */
    public String getLoanStatusDate() {
        return loanStatusDate;
    }

    /**
     * @param loanStatusDate The loanStatusDate
     */
    public void setLoanStatusDate(String loanStatusDate) {
        this.loanStatusDate = loanStatusDate;
    }

}
