package com.ravneetg.lcautomatique.utils.filters;

import com.ravneetg.lcautomatique.utils.DropdownValue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Ravneet on 1/20/2015.
 */
public class FilterUI {

    private Map mFilters = new LinkedHashMap<String, Filter>();
    private static final String[][] PURPOSE = {
            {"car", "Car"},
            {"credit_card", "Credit Card"},
            {"debt_consolidation", "Debt Consolidation"},
            {"home_improvement", "Home Improvement"},
            {"house", "House"},
            {"major_purchase", "Major Purchase"},
            {"medical", "Medical"},
            {"moving", "Moving"},
            {"renewable_energy", "Renewable Energy"},
            {"other", "Other"}
    };

    private static final String[][] USA_STATES = {
            {"AL", "Alabama"},
            {"AK", "Alaska"},
            {"AZ", "Arizona"},
            {"AR", "Arkansas"},
            {"CA", "California"},
            {"CO", "Colorado"},
            {"CT", "Connecticut"},
            {"DE", "Delaware"},
            {"DC", "District Of Columbia"},
            {"FL", "Florida"},
            {"GA", "Georgia"},
            {"HI", "Hawaii"},
            {"ID", "Idaho"},
            {"IL", "Illinois"},
            {"IN", "Indiana"},
            {"IA", "Iowa"},
            {"KS", "Kansas"},
            {"KY", "Kentucky"},
            {"LA", "Louisiana"},
            {"ME", "Maine"},
            {"MD", "Maryland"},
            {"MA", "Massachusetts"},
            {"MI", "Michigan"},
            {"MN", "Minnesota"},
            {"MS", "Mississippi"},
            {"MO", "Missouri"},
            {"MT", "Montana"},
            {"NE", "Nebraska"},
            {"NV", "Nevada"},
            {"NH", "New Hampshire"},
            {"NJ", "New Jersey"},
            {"NM", "New Mexico"},
            {"NY", "New York"},
            {"NC", "North Carolina"},
            {"ND", "North Dakota"},
            {"OH", "Ohio"},
            {"OK", "Oklahoma"},
            {"OR", "Oregon"},
            {"PA", "Pennsylvania"},
            {"RI", "Rhode Island"},
            {"SC", "South Carolina"},
            {"SD", "South Dakota"},
            {"TN", "Tennessee"},
            {"TX", "Texas"},
            {"UT", "Utah"},
            {"VT", "Vermont"},
            {"VA", "Virginia"},
            {"WA", "Washington"},
            {"WV", "West Virginia"},
            {"WI", "Wisconsin"},
            {"WY", "Wyoming"}
    };

    private static final String[][] HOME_OWNERSHIP = {
            {"OWN", "Own"},
            {"MORTGAGE", "Mortgage"},
            {"RENT", "Rent"}
    };

    private static final String[][] INCOME_VERIFICATION = {
            {"NOT_VERIFIED", "Not Verified"},
            {"VERIFIED", "Verified"},
            {"SOURCE_VERIFIED", "Source Verified"}
    };

    public FilterUI(){
        SeedFilterDefn();
    }

    private void SeedFilterDefn(){
        Filter fTerm = createChoiceFilter("Loan Term","term",new String[]{"36", "60"});
        mFilters.put("term", fTerm);

        Filter fGrade = createChoiceFilter("Grade","grade",new String[]{"A", "B", "C", "D", "E", "F", "G"});
        fGrade.setHalfWidth(true);
        mFilters.put("grade", fGrade);

        Filter fSubGrade = createChoiceFilter("Sub Grade","subGrade",new String[]{"A1", "A2", "A3", "A4", "A5", "B1", "B2", "B3", "B4", "B5", "C1", "C2", "C3", "C4", "C5", "D1", "D2", "D3", "D4", "D5", "E1", "E2", "E3", "E4", "E5", "F1", "F2", "F3", "F4", "F5", "G1", "G2", "G3", "G4", "G5"});
        fSubGrade.setHalfWidth(true);
        mFilters.put("subGrade", fSubGrade);

        Filter fPurpose = createChoiceFilter("Purpose", "purpose", PURPOSE);
        fPurpose.setHalfWidth(true);
        mFilters.put("purpose", fPurpose);

        Filter fAddrState = createChoiceFilter("State","addrState",USA_STATES);
        fAddrState.setHalfWidth(true);
        mFilters.put("addrState", fAddrState);

        Filter fHomeOwnership = createChoiceFilter("Home ownership","homeOwnership", HOME_OWNERSHIP);
        fHomeOwnership.setHalfWidth(true);
        mFilters.put("homeOwnership", fHomeOwnership);

        Filter fIncomeVerified = createChoiceFilter("Income", "isIncV", INCOME_VERIFICATION);
        fIncomeVerified.setHalfWidth(true);
        mFilters.put("isIncV", fIncomeVerified);

        mFilters.put("annualInc", createNumericRangeFilter("Annual Income","annualInc"));
        mFilters.put("dti", createNumericRangeFilter("Debt to income","dti"));

        //TODO
        //we probably use the filter name internal as creditScore,
        //but for LC this needs to be converted into ficoRangeLow & ficoRangeHigh
        mFilters.put("ficoRangeLow", createNumericRangeFilter("FICO Range Low","ficoRangeLow"));
        mFilters.put("ficoRangeHigh", createNumericRangeFilter("FICO Range High","ficoRangeHigh"));

        mFilters.put("chargeoffWithin12Mths", createNumericRangeFilter("Chargeoff within 12 months", "chargeoffWithin12Mths"));
        mFilters.put("taxLiens", createNumericRangeFilter("Tax Liens", "taxLiens"));
        mFilters.put("inqLast6Mths", createNumericRangeFilter("Inquiries in last 6 months", "inqLast6Mths"));
        mFilters.put("mthsSinceRecentInq", createNumericRangeFilter("Months since recent inquiry", "mthsSinceRecentInq"));
        mFilters.put("revolBal", createNumericRangeFilter("Revolving Balance", "revolBal"));
        mFilters.put("revolUtil", createNumericRangeFilter("Revolving Utilization", "revolUtil"));

        mFilters.put("totalAcc", createNumericRangeFilter("Total accounts", "totalAcc"));
        mFilters.put("mortAcc", createNumericRangeFilter("Mortgage accounts", "mortAcc"));
        // This is special numeric and here user might want to filter by not available or empty
        mFilters.put("mthsSinceLastMajorDerog", createNumericRangeFilter("Months to major derogatory", "mthsSinceLastMajorDerog"));
        mFilters.put("pubRec", createNumericRangeFilter("Public records", "pubRec"));
        mFilters.put("pubRecBankruptcies", createNumericRangeFilter("Public record bankruptcy", "pubRecBankruptcies"));

    }

    private Filter createNumericFilter(String caption, String baseFilterName){
        return new Filter(caption, baseFilterName, Filter.FilterType.ftNumber);
    }

    private Filter createChoiceFilter(String caption, String baseFilterName, String[] choices){
        DropdownValue[] dv = new DropdownValue[choices.length];
        for (int i = 0; i < dv.length; i++) {
            dv[i] = new DropdownValue(choices[i], choices[i]);
        }
        return createChoiceFilter(caption, baseFilterName, dv);
    }

    private Filter createChoiceFilter(String caption, String baseFilterName, String[][] choices){
        DropdownValue[] dv = new DropdownValue[choices.length];
        for (int i = 0; i < dv.length; i++) {
            dv[i] = new DropdownValue(choices[i][1], choices[i][0]);
        }
        return createChoiceFilter(caption, baseFilterName, dv);
    }

    private Filter createChoiceFilter(String caption, String baseFilterName, DropdownValue[] choices){
        Filter f = new Filter(caption, baseFilterName, Filter.FilterType.ftChoice);
        f.setFilterChoice(choices);
        return f;
    }

    private Filter createNumericRangeFilter(String caption, String baseFilterName){
        Filter f = new Filter(caption, baseFilterName, Filter.FilterType.ftRange);
        return f;
    }

    public Map<String, Filter> getFilterDefinition(){
        return mFilters;
    }
}
