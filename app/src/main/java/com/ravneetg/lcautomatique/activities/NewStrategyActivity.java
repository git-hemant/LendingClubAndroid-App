package com.ravneetg.lcautomatique.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ravneetg.lcautomatique.MainApplication;
import com.ravneetg.lcautomatique.R;
import com.ravneetg.lcautomatique.controls.MultiSelectSpinner;
import com.ravneetg.lcautomatique.db.DBHelper;
import com.ravneetg.lcautomatique.db.models.StrategyConfig;
import com.ravneetg.lcautomatique.request.RequestUtil;
import com.ravneetg.lcautomatique.request.data.response.PortfoliosOwnedResponseData;
import com.ravneetg.lcautomatique.request.data.response.ResponseData;
import com.ravneetg.lcautomatique.utils.DropdownValue;
import com.ravneetg.lcautomatique.utils.SimpleAsyncTask;
import com.ravneetg.lcautomatique.utils.Util;
import com.ravneetg.lcautomatique.utils.filters.Filter;
import com.ravneetg.lcautomatique.utils.filters.FilterUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NewStrategyActivity extends ActionBarActivity {

    private LinearLayout mLLContainer;
    private EditText mtxtStrategyName;
    private Spinner mTargetPortfolio;
    private Spinner mAmountPerNote;
    private Spinner mMaxOrdersPerDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_strategy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_content_clear);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewStrategyActivity.this.finish();
            }
        });

        mLLContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
        mtxtStrategyName = (EditText) findViewById(R.id.txtStrategyName);
        mTargetPortfolio = (Spinner) findViewById(R.id.lstTargetPortfolio);
        mTargetPortfolio.setBackgroundResource(R.drawable.automatique_spinner_disabled_holo_light);
        mAmountPerNote = (Spinner) findViewById(R.id.lstAmountPerNote);
        mMaxOrdersPerDay = (Spinner) findViewById(R.id.lstMaxOrdersPerDay);
        populateTargetPortfolio();
        populateAmountPerNote();
        populateMaxOrdersPerDay();
        buildFilterUI();
    }

    private void populateTargetPortfolio() {
        /*
        ArrayAdapter<String> defaultAdapter = new ArrayAdapter<String>(NewStrategyActivity.this,
                android.R.layout.simple_spinner_item, new String[]{getString(R.string.default_portfolio)});
        */
        final DropdownValue portfolioNone = new DropdownValue(getString(R.string.default_portfolio),"");
        ArrayAdapter<DropdownValue> defaultAdapter = new ArrayAdapter<DropdownValue>(NewStrategyActivity.this,
                android.R.layout.simple_spinner_item, new DropdownValue[]{portfolioNone});
        defaultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTargetPortfolio.setAdapter(defaultAdapter);
        Util.doAsyncTask(new SimpleAsyncTask() {
            @Override
            public void doWork() {
                PortfoliosOwnedResponseData data = RequestUtil.requestPortfoliosOwned(NewStrategyActivity.this);
                final JSONArray[] jsonPortfolios = {null};
                if(data.getResponseCode() == ResponseData.RESPONSE_SUCCESS){
                    JSONObject p = data.getResponseData();
                    try {
                        jsonPortfolios[0] = p.getJSONArray(PortfoliosOwnedResponseData.MY_PORTFOLIOS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JSONArray jsonPortfoliosDetails = jsonPortfolios[0];
                                if (jsonPortfoliosDetails != null) {
                                    DropdownValue[] portfolios = new DropdownValue[jsonPortfoliosDetails.length() + 1];
                                    // Specify default portfolio as none, as it is optional field.
                                    portfolios[0] = portfolioNone;
                                    for (int i = 1; i < jsonPortfoliosDetails.length(); i++) {
                                        try {
                                            JSONObject portfolio = jsonPortfoliosDetails.getJSONObject(i);
                                            String portfolioName = portfolio.getString(PortfoliosOwnedResponseData.PORTFOLIO_NAME);
                                            long portfolioId = portfolio.getLong(PortfoliosOwnedResponseData.PORTFOLIO_ID);
                                            portfolios[i] = new DropdownValue(portfolioName, String.valueOf(portfolioId));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    ArrayAdapter<DropdownValue> adapter = new ArrayAdapter<DropdownValue>(NewStrategyActivity.this,
                                            android.R.layout.simple_spinner_item,
                                            portfolios);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    mTargetPortfolio.setAdapter(adapter);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void populateAmountPerNote() {
        List<String> listAmount = new ArrayList<String>();
        int minInvestment = 25;
        // Till $200 add all in split of $25
        for (int i = 1; i <= 8; i++) listAmount.add("$" + String.valueOf(minInvestment * i));
        // From $200 till $100 add at interval of $50
        for (int i = 9; i <= 40; i++) listAmount.add("$" + String.valueOf(minInvestment * ++i));

        ArrayAdapter<String> defaultAdapter = new ArrayAdapter<String>(NewStrategyActivity.this,
                android.R.layout.simple_spinner_item, listAmount);
        defaultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAmountPerNote.setAdapter(defaultAdapter);
    }

    private void populateMaxOrdersPerDay() {
        List<String> listMaxOrders = new ArrayList<String>();
        listMaxOrders.add("Any");
        // Till $200 add all in split of $25
        for (int i = 1; i <= 10; i++) listMaxOrders.add(String.valueOf(i));

        ArrayAdapter<String> defaultAdapter = new ArrayAdapter<String>(NewStrategyActivity.this,
                android.R.layout.simple_spinner_item, listMaxOrders);
        defaultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMaxOrdersPerDay.setAdapter(defaultAdapter);
    }

    private void finishActivity(boolean refreshNeeded){
        Intent resultIntent = new Intent();
        if(refreshNeeded)
            setResult(Activity.RESULT_OK, resultIntent);
        else
            setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_strategy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_strategy_save) {
            saveStrategy();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buildFilterUI(){
        Map<String, Filter> mapFilters = new FilterUI().getFilterDefinition();
        Log.v(MainApplication.TAG, "here");
        boolean wasHalf = false;
        LinearLayout subContainer = null;

        int counter = 0;
        for(Map.Entry<String, Filter> filter : mapFilters.entrySet()){

            //for each entry create a label, and then the control
            TextView txtLabel = new TextView(NewStrategyActivity.this);
            txtLabel.setText(filter.getValue().getCaption().toUpperCase());
            txtLabel.setTextAppearance(NewStrategyActivity.this, android.R.style.TextAppearance_Small);


            View control = getControl((Filter)filter.getValue());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 10, 0, 60); //substitute parameters for left, top, right, bottom
            control.setLayoutParams(params);

            if (filter.getValue().isHalfWidth())
            {
                if (subContainer == null || counter == 2) {
                    subContainer = new LinearLayout(NewStrategyActivity.this);
                    mLLContainer.addView(subContainer);
                    counter = 0;
                }
                subContainer.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout colLinearLayout = new LinearLayout(NewStrategyActivity.this);
                colLinearLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.weight = 1;
                colLinearLayout.setLayoutParams(p);

                colLinearLayout.addView(txtLabel);
                colLinearLayout.addView(control);

                subContainer.addView(colLinearLayout);
                counter++;
            } else if (filter.getValue().getFilterType() == Filter.FilterType.ftRange) {
                mLLContainer.addView(txtLabel);
                mLLContainer.addView(control);
            } else {
                mLLContainer.addView(txtLabel);
                mLLContainer.addView(control);
            }
        }
    }

    private View getControl(Filter filter){
        View view = null;
        String baseFilterName = filter.getBaseFilterName();
        if (filter.getFilterType() == Filter.FilterType.ftNumber) {
            //Simple Number input type
            view = new EditText(NewStrategyActivity.this);
            ((EditText)view).setInputType(InputType.TYPE_CLASS_NUMBER);
            view.setId(View.generateViewId());
            view.setTag(filter);
            view.setBackgroundResource(R.drawable.automatique_textfield_disabled_holo_light);
            ((EditText) view).setHint(getString(R.string.any_value));
        } else if(filter.getFilterType() == Filter.FilterType.ftChoice) {
            //multi choice select list (checkbox list)
            view = new MultiSelectSpinner(NewStrategyActivity.this);
            ((MultiSelectSpinner)view).setPropertyName(filter.getCaption());
            ((MultiSelectSpinner)view).setItems(filter.getFilterChoice());
            int id = View.generateViewId();
            view.setId(id);
            view.setTag(filter);
            view.setBackgroundResource(R.drawable.automatique_spinner_disabled_holo_light);
        } else if(filter.getFilterType() == Filter.FilterType.ftRange) {
            //a simple number input, but with low nad high ranges, we render 2 inputs for such filters
            view = new LinearLayout(NewStrategyActivity.this);
            ((LinearLayout)view).setOrientation(LinearLayout.HORIZONTAL); //inner layout container

            EditText etLowerRange = new EditText(NewStrategyActivity.this);
            etLowerRange.setInputType(InputType.TYPE_CLASS_NUMBER);
            ((EditText) etLowerRange).setHint(getString(R.string.any_value));

            FilterWrapper wrapperLow = new FilterWrapper(filter, true);
            etLowerRange.setTag(wrapperLow);

            EditText etHighRange = new EditText(NewStrategyActivity.this);
            etHighRange.setInputType(InputType.TYPE_CLASS_NUMBER);
            FilterWrapper wrapperHigh = new FilterWrapper(filter, false);
            etHighRange.setTag(wrapperHigh);
            ((EditText) etHighRange).setHint(getString(R.string.any_value));

            TextView tvLabel = new TextView(NewStrategyActivity.this);
            tvLabel.setText(" to ");

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 1;

            ((LinearLayout)view).setLayoutParams(p);
            etLowerRange.setLayoutParams(p);
            etHighRange.setLayoutParams(p);
            etHighRange.setBackgroundResource(R.drawable.automatique_textfield_disabled_holo_light);
            etLowerRange.setBackgroundResource(R.drawable.automatique_textfield_disabled_holo_light);

            ((LinearLayout) view).addView(etLowerRange);
            ((LinearLayout) view).addView(tvLabel);
            ((LinearLayout) view).addView(etHighRange);

        }

        return view;
    }


    private boolean saveStrategy(){
        // Create new strategy configuration object and save it.
        final StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setStrategyName(mtxtStrategyName.getText().toString());
        strategyConfig.setFilter(getFiltersValue());

        try {
            String testValue = strategyConfig.getFilter().toString(4);
            int count = testValue.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // TODO - Work here target portfolio needs to save numeric number
        // and for None it should be empty.
        DropdownValue targetPortfolio = (DropdownValue) mTargetPortfolio.getSelectedItem();
        if (targetPortfolio != null) {
            strategyConfig.setTargetPortfolio(targetPortfolio.getSaveValue());
        }
        Object amountPerNote = mAmountPerNote.getSelectedItem();
        if (amountPerNote != null) {
            strategyConfig.setAmountPerNote(Integer.parseInt(amountPerNote.toString().substring(1)));
        }

        Object maxOrdersInDay = mMaxOrdersPerDay.getSelectedItem();
        if (maxOrdersInDay != null && !"Any".equals(maxOrdersInDay.toString())) {
            strategyConfig.setMaxLoansPerDay(Integer.parseInt(maxOrdersInDay.toString()));
        }
        Util.doAsyncTask(new SimpleAsyncTask() {
            @Override
            public void doWork() {
                long strategyId = DBHelper.getInstance(NewStrategyActivity.this).saveNewStrategy(strategyConfig);
                if(strategyId > 0){
                    finishActivity(true);
                }else{
                    //alert user??
                }
            }
        });
        return true;
    }

    private JSONObject getFiltersValue() {
        JSONObject filters = new JSONObject();
        for (int i = 0; i < mLLContainer.getChildCount(); i++) {
            View v = mLLContainer.getChildAt(i);
            List<View> views = new ArrayList<View>();
            if (!(v instanceof LinearLayout)) {
                views.add(v);
            } else {
                traverseUIHierarchy((LinearLayout) v, views);
            }
            if (views.size() > 0) {
                for (View view : views) saveViewValues(view, filters);
            }
        }
        return filters;
    }

    private void saveViewValues(View v, JSONObject filters) {
        Object tagObject = v.getTag();
        if (tagObject == null) {
            Log.d(MainApplication.TAG, "TagObject is null view is " + v + " class: " + v.getClass().getName());
            return;
        }

        Filter filter = null;
        FilterWrapper wrapper = null;

        if (tagObject instanceof FilterWrapper) {
            wrapper = (FilterWrapper) tagObject;
            filter = wrapper.getFilter();
        } else if (tagObject instanceof Filter) {
            filter = (Filter) tagObject;
        }
        if (filter != null) {
            //we are interested in this View.
            String filterName = filter.getBaseFilterName();
            Log.w(MainApplication.TAG, "Filter name is: " + filterName);
            String filterValue = null;
            if (v instanceof EditText) {
                if (filter.getFilterType() == Filter.FilterType.ftRange) {
                    filterValue = ((EditText) v).getText().toString();
                    if (wrapper != null && filterValue != null && filterValue.length() > 0) {
                        try {
                            JSONObject filterObject = null;
                            if (!filters.isNull(filterName)) {
                                filterObject = filters.getJSONObject(filterName);
                            } else {
                                filterObject = new JSONObject();
                            }
                            // We are dealing with a range, check if we are lower range or higher range
                            if (wrapper.isLowerRange()) {
                                filterObject.put("lessThan", filterValue);
                            } else {
                                filterObject.put("greaterThan", filterValue);
                            }
                            filters.put(filterName, filterObject);
                        } catch (JSONException e) {
                            // TODO - We need to following standard UI practise here
                            e.printStackTrace();
                        }
                    }
                }
            } else if (v instanceof MultiSelectSpinner) {
                JSONArray selectedItemsArray = new JSONArray();
                List<DropdownValue> selectedItems = ((MultiSelectSpinner) v).getSelectedItems();
                if (selectedItems.size() > 0) {
                    for (DropdownValue selectedItem : selectedItems) {
                        selectedItemsArray.put(selectedItem.getSaveValue());
                    }
                    try {
                        JSONObject includeJsonObject = new JSONObject();
                        includeJsonObject.put("include", selectedItemsArray);
                        filters.put(filterName, includeJsonObject);
                    } catch (JSONException e) {
                        // TODO - We need to following standard UI practise here
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void traverseUIHierarchy(ViewGroup viewGroup, List<View> views) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof EditText) {
                views.add(view);
            } else if (view instanceof MultiSelectSpinner) {
                views.add(view);
            } else if (view instanceof ViewGroup) {
                traverseUIHierarchy((ViewGroup) view, views);
            }
        }
    }

    private static class FilterWrapper extends Filter{

        private boolean mLowerRange = false;
        private Filter mFilter;
        public FilterWrapper(String caption, String baseFilterName, FilterType filterType) {
            super(caption, baseFilterName, filterType);
            mLowerRange = false;
        }

        public FilterWrapper(Filter filter, boolean lowerRange){
            super(filter.getCaption(), filter.getBaseFilterName(), filter.getFilterType());
            mFilter = filter;
            mLowerRange = lowerRange;
        }

        public Filter getFilter(){
            return mFilter;
        }

        public boolean isLowerRange(){
            return mLowerRange;
        }
    }
}
