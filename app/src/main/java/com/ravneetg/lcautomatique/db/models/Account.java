package com.ravneetg.lcautomatique.db.models;

/**
 * Created by Ravneet on 1/16/2015.
 */
public class Account {
    private int AccountId;
    private String AccountName;
    private String CreatedDate;
    private String ModifiedDate;

    public Account(String accountName){
        this.AccountName = accountName;
    }

    public Account(int accountId, String accountName, String createdDate, String modifiedDate){
        this.AccountId = accountId;
        this.AccountName = accountName;
        this.CreatedDate = createdDate;
        this.ModifiedDate = modifiedDate;
    }

    public int getAccountId()
    {
        return AccountId;
    }

    public String getAccountName(){
        return AccountName;
    }

    public String getCreatedDate(){
        return CreatedDate;
    }

    public String getModifiedDate(){
        return ModifiedDate;
    }
}
