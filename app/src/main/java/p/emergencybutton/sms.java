package p.emergencybutton;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class sms extends AppCompatActivity {
    static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 1;


    Button send;
    Button addContacts;
    String check;
    EditText message;
    TextInputLayout layoutMessage;
    CheckBox checkBox;
    public String text = " ";
    RelativeLayout relativeLayout;
    String buttonID = " ";
    int increment = 4;
    MyLocation myLocation = new MyLocation();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        send = (Button) findViewById(R.id.btn_send);
        addContacts = (Button) findViewById(R.id.btn_selectContacts);
        message = (EditText) findViewById(R.id.edt_sms);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        relativeLayout = (RelativeLayout) findViewById(R.id.view_rel);
        layoutMessage = (TextInputLayout) findViewById(R.id.layout_message);
        Window window = this.getWindow();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.toolbarColor));
        ViewCompat.setBackgroundTintList(message, colorStateList);

        if (android.os.Build.VERSION.SDK_INT > 18) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (android.os.Build.VERSION.SDK_INT > 20) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (android.os.Build.VERSION.SDK_INT > 20) {
            window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.colorWindow));
        }

        SharedPreferences sharedPref = getSharedPreferences("messageKey", 0);
        String draft = sharedPref.getString("message", "");
        if (!draft.equals("")) {
            message.setText(draft);
        }


        SharedPreferences preferences = getSharedPreferences("statusKey", 0);
        String status = preferences.getString("Status", "");
        if (!status.equals("")) {
            if (status.equals("checked")) {
                checkBox.setChecked(true);
            } else if (status.equals("unchecked")) {
                checkBox.setChecked(false);
            }
        }
//        if (checkBox.isChecked()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                buttonID = "internet";
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            }else {
//                myLocation.getLocation(getApplicationContext(), locationResult);
//                boolean r = myLocation.getLocation(getApplicationContext(),
//                        locationResult);
//            }
//        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        buttonID = "internet";
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    } else {
                        myLocation.getLocation(getApplicationContext(), locationResult);
                        boolean r = myLocation.getLocation(getApplicationContext(),
                                locationResult);
                    }
                    SharedPreferences sharedPref = getSharedPreferences("statusKey", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("Status", "checked");
                    editor.apply();
                } else {
                    SharedPreferences sharedPref = getSharedPreferences("statusKey", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("Status", "unchecked");
                    editor.apply();
                }
            }
        });

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences sharedPref = getSharedPreferences("messageKey", 0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("message", s.toString());
                editor.apply();
            }
        });
        addContacts.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    buttonID = addContacts.getText().toString();
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
                } else {
                    gotoContactsAvtivity();
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    buttonID = send.getText().toString();
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
                }
                sendSmsMethod();

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasPermission()) {

            } else {
                checkPermission();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            buttonID = "internet";
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            myLocation.getLocation(getApplicationContext(), locationResult);
            boolean r = myLocation.getLocation(getApplicationContext(),
                    locationResult);
        }

    }

    public double Longitude;
    public double Latitude;
    public MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
        @Override
        public void gotLocation(Location location) {
            // TODO Auto-generated method stub
            Longitude = location.getLongitude();
            Latitude = location.getLatitude();

        }
    };


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent to:" + phoneNo.toString(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public Activity getActivity() {
        return this;
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("loction", strReturnedAddress.toString());
            } else {
                Log.w("loction", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("loction", "Canont get Address!");
        }
        return strAdd;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)

    private void checkPermission() {
        int hasContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        int hasSmsPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
        int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> permissions = new ArrayList<>();
        if (hasContactsPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_CONTACTS);
        }
        if (hasSmsPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.SEND_SMS);
        }
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
        }
    }


    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermission() {
        int hasContactsPermission = 0;
        int hasSmsPermission = 0;
        int hasInternnetPermission = 0;
        hasContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        hasSmsPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
        hasInternnetPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        return (hasContactsPermission == PackageManager.PERMISSION_GRANTED && hasSmsPermission == PackageManager.PERMISSION_GRANTED && hasInternnetPermission == PackageManager.PERMISSION_GRANTED);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        if (buttonID.equals(addContacts.getText().toString())) {
                            gotoContactsAvtivity();
                        }
                        if (buttonID.equals(send.getText().toString())) {
                            sendSmsMethod();
                        }
                        if (buttonID.equals("internet")) {
                            MyLocation myLocation = new MyLocation();
                            myLocation.getLocation(getApplicationContext(), locationResult);
                            boolean r = myLocation.getLocation(getApplicationContext(),
                                    locationResult);
                        }

                    } else if
                            (grantResults[i] == PackageManager.PERMISSION_DENIED)
                        if (buttonID.equals("internet")) {
                            checkBox.setChecked(false);
                        }

                    {
                        Log.d("Permissions", "Permission Denied: " + permissions[i]);

                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void gotoContactsAvtivity() {
        MainActivity mainActivity = new MainActivity();
        //  mainActivity.values = " ";
        Intent intent = new Intent(sms.this, MainActivity.class);
        startActivity(intent);
    }

    public void sendSmsMethod() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            buttonID = "internet";
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }else {
//            myLocation.getLocation(getApplicationContext(), locationResult);
//            boolean r = myLocation.getLocation(getApplicationContext(),
//                    locationResult);
//        }
        SharedPreferences sharedPref = getSharedPreferences("KEY", 0);
        String values = sharedPref.getString("NUMBERS", " ");

        if (values.equals(" ") || values.isEmpty()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            Snackbar snackbar = Snackbar.make(relativeLayout, "Please first select contacts", Snackbar.LENGTH_LONG);
            snackbar.show();
            addContacts.requestFocus();
            return;
        }

        if (message.getText().toString().equals(" ") || message.getText().toString().isEmpty()) {
            layoutMessage.setError("Please write a message");
        } else {
            layoutMessage.setErrorEnabled(false);
            String phoneNumber[] = values.split(",");
            for (int i = 0; i < phoneNumber.length; i++) {
                String address = getCompleteAddressString(Latitude, Longitude);
                if (checkBox.isChecked()) {
                    if (address.equals("")) {
                        text = message.getText().toString() + "\n" + "My current location" + "\n" + "Latitude: " + Latitude + "\n" +
                                "Longitude: " + Longitude;
                    }
                    if (!address.equals("")) {
                        text = message.getText().toString() + "\n" + "My current location" + "\n" + address;
                    }
                } else {
                    text = message.getText().toString();
                }

                sendSMS(phoneNumber[i], text);

            }

        }

    }
}
