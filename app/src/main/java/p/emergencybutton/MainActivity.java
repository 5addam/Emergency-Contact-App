package p.emergencybutton;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static p.emergencybutton.R.drawable.contact;


public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<String> contactName;
    List<String> contactNumber;
    List<Bitmap> contactPhotos;
    public List<String> values;
    FloatingActionButton fab;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        values = new ArrayList<String>();
        recyclerView = (RecyclerView) findViewById(R.id.list_view);
        contactName = new ArrayList<String>();
        contactNumber = new ArrayList<String>();
        contactPhotos = new ArrayList<Bitmap>();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.view_coordinator);

        readContacts();
        SharedPreferences preferences = getSharedPreferences("KEY",0);
        String getValue = preferences.getString("NUMBERS"," ");
        if(!getValue.equals(" ")){
            String[] numbers = getValue.split(",");
            values.addAll(Arrays.asList(numbers));
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(new adapter(contactName, contactNumber,contactPhotos,values, MainActivity.this));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(values.size() <= 0){
                    Snackbar snackbar = Snackbar.make(coordinatorLayout,"Please select contacts first",Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(getResources().getColor(R.color.toolbarColor));
                    snackbar.show();
                }
                else {
                    String numbers="";
                    for(int i=0;i<values.size();i++){
                        if(numbers.equals("")){
                            numbers = values.get(i);
                        }
                        else{
                            numbers = numbers + "," + values.get(i);
                        }
                    }
                    SharedPreferences sharedPref = getSharedPreferences("KEY", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("NUMBERS", numbers);
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Main", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(MainActivity.this, sms.class);
                    startActivity(intent);
                }
            }
        });
    }


    @SuppressLint("ResourceType")
    public void readContacts(){
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        while (phones != null && phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactName.add(name);
            contactNumber.add(phoneNumber);

            Bitmap photo = null;

            try {
                long id = phones.getLong(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(MainActivity.this.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id)));

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                }

                if (inputStream != null) inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photo == null) {
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.contac, null);
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                contactPhotos.add(bitmap);

            } else {
                contactPhotos.add(photo);
            }
        }
        phones.close();
    }

    public Activity getActivity() {
        return this;
    }


}

