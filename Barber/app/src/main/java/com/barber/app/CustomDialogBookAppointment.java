package com.barber.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.barber.app.constants.Constants;
import com.barber.app.entities.ShopDetails;
import com.barber.app.utils.UtilsImpl;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CustomDialogBookAppointment extends Dialog implements
        android.view.View.OnClickListener {

    private ShopDetails shopDetails;
    public Activity activity;
    public Dialog dialog;
    public Spinner spinner;
    List<String> spinnerArray;
    Button bookNow;
    Button cancel;
    Button btnDatePicker, btnTimePicker;
    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText etComments;


    public CustomDialogBookAppointment(Activity activity, ShopDetails shopDetails) {
        super(activity, android.R.style.Theme_Material_NoActionBar_Fullscreen);
        this.activity = activity;
        this.shopDetails = shopDetails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_book);
        spinner = (Spinner) findViewById(R.id.spinner);
        bookNow = (Button) findViewById(R.id.bookNow);
        cancel = (Button) findViewById(R.id.cancel);
        btnDatePicker = (Button) findViewById(R.id.btn_date);
        btnTimePicker = (Button) findViewById(R.id.btn_time);

        spinnerArray = new ArrayList<String>(Arrays.asList(shopDetails.getProviderServices().split(";")));
        spinnerArray.add("Other");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (activity, R.layout.simple_spinner_item_services, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        bookNow.setOnClickListener(this);
        cancel.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btn_date:
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                btnDatePicker.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;

            case R.id.btn_time:
                final Calendar cal = Calendar.getInstance();
                mHour = cal.get(Calendar.HOUR_OF_DAY);
                mMinute = cal.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                btnTimePicker.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                break;

            case R.id.bookNow:
                if (UtilsImpl.isUserSignedIn(activity)) {
                    //TODO CALL BOOK APPOINTMENT
                } else {
                    showInformationDialog();
                }
                break;

            case R.id.cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void showInformationDialog() {
        new MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme)
                .setTitle("Booking Appointments")
                .setMessage("In order to be able to book an appointment, first you need to sign in to your account.")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent loginPage = new Intent(getContext(), LoginActivity.class);
                        loginPage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, "BOOK_APPOINTMENT_ACTIVITY");
                        getContext().startActivity(loginPage);
                    }
                }).show();

    }
}