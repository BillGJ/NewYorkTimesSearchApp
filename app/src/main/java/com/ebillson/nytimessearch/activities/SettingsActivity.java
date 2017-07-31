package com.ebillson.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ebillson.nytimessearch.R;
import com.ebillson.nytimessearch.models.SearchSettings;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    EditText etBeginDate;
    RadioGroup radioGroupSort;
    RadioButton rbNewest, rbOldest, radioButtonSort;
    CheckBox cbArts, cbFashion, cbSports;
    SearchSettings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        setSearchParametersValue();
        // initialize radio objects
        initializeSettingsObjects();
    }



    public void setSearchParametersValue() {
        this.settings = (SearchSettings) getIntent().getSerializableExtra("settings");
    }

    public void initializeSettingsObjects() {
        // begin date
        etBeginDate = (EditText) findViewById(R.id.etBeginDate);

        // radio sort order objects
        radioGroupSort = (RadioGroup) findViewById(R.id.rgSort);
        rbNewest = (RadioButton) findViewById(R.id.rbNewest);
        rbOldest = (RadioButton) findViewById(R.id.rbOldest);

        if(settings.getSortOrder() == SearchSettings.Sort.newest) {
            rbNewest.setChecked(true);
        } else if(settings.getSortOrder() == SearchSettings.Sort.oldest) {
            rbOldest.setChecked(true);
        }


        // checkbox filter objects
        // fill checkboxes according to settings value
        cbArts = (CheckBox) findViewById(R.id.cbArts);
        cbFashion = (CheckBox) findViewById(R.id.cbFashion);
        cbSports = (CheckBox) findViewById(R.id.cbSports);

        for(String filter : settings.getFilters()) {
            if(filter.equals("Arts"))
                cbArts.setChecked(true);

            if(filter.equals("Fashion & Style"))
                cbFashion.setChecked(true);

            if(filter.equals("Sports"))
                cbSports.setChecked(true);
        }
    }

    public void onSave(View view) {

        // set radio sort settings
        setRadioSort();

        // set checkbox filters settings
        setCheckboxFilters();

        //Toast.makeText(this, "Radio Button: "+ this.sort, Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        i.putExtra("settings", settings);
        setResult(RESULT_OK, i);
        finish();
    }

    public void setRadioSort() {
        // get selected radio button
        int selectedId = radioGroupSort.getCheckedRadioButtonId();

        // find the radio button
        radioButtonSort = (RadioButton) findViewById(selectedId);

        switch (selectedId){
            case R.id.rbNewest:
                this.settings.setSortOrder(SearchSettings.Sort.newest);
                break;

            case R.id.rbOldest:
                this.settings.setSortOrder(SearchSettings.Sort.oldest);
                break;
        }
    }

    public void setCheckboxFilters() {
        settings.getFilters().clear();

        if(cbArts.isChecked())
            settings.addFilter("Arts");

        if(cbFashion.isChecked())
            settings.addFilter("Fashion & Style");

        if(cbSports.isChecked())
            settings.addFilter("Sports");
    }


   ///?????











    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        //String dateString = String.valueOf(c.get(c.YEAR)+""+c.get(c.MONTH)+""+c.get(c.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        // pass the date to the view
        etBeginDate.setText(sdf.format(c.getTime()));
        // pass the date to the settings
        settings.setBeginDate(sdf);

    }























}
