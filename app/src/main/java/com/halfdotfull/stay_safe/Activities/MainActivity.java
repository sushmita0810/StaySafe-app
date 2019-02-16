package com.halfdotfull.stay_safe.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.halfdotfull.stay_safe.FakeCallReceiver;
import com.halfdotfull.stay_safe.Permissions;
import com.halfdotfull.stay_safe.R;
import com.halfdotfull.stay_safe.Services.MessageService;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 2804;
    CardView message, contacts, fakecall, helpline, police, isSafe;
    SharedPreferences mSharedPreferences;
    String number, serial;

    //SmsManager smsManager = SmsManager.getDefault();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = (CardView) findViewById(R.id.cardView2);
        isSafe = (CardView) findViewById(R.id.cardView7);
        contacts = (CardView) findViewById(R.id.cardView1);
//        fakecall = (CardView) findViewById(R.id.cardView3);
        police = (CardView) findViewById(R.id.cardView5);
        helpline = (CardView) findViewById(R.id.cardView4);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED)
        {
            Permissions.askforPermission(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.CALL_PHONE
                            },
                    new Permissions.OnpermissionResultListner() {
                        @Override
                        public void OnGranted(String fperman) {
                           // Toast.makeText(MainActivity.this, fperman, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void OnDenied(String fperman) {
                          //  Toast.makeText(MainActivity.this, fperman, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        startService();
        isSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLocationAvailable()) {
                    Intent intent = new Intent(MainActivity.this, IsSafe.class);
                    startActivity(intent);
                }
                else{
                LocationPermissionCheck();
                }


            }
        });
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLocationAvailable()) {
                    Intent intent = new Intent(MainActivity.this, Police.class);
                    startActivity(intent);
                }
                else
                {
                   LocationPermissionCheck();
                }

            }
        });
/*
        fakecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 10);
                long currentFakeTime = calendar.getTimeInMillis();

                String fakeNameEntered = "Papa";
                String fakeNumberEntered = "9412168792";
                Log.d("TAGGER", "onClick: "+String.valueOf(currentFakeTime));
                setUpAlarm(currentFakeTime, fakeNameEntered, fakeNumberEntered);
            }
        });
*/
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Contact.class);
                startActivity(i);
            }
        });
        helpline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "1091"));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.input)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("Enter Here", null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                mSharedPreferences=getSharedPreferences("staysafe", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=mSharedPreferences.edit();
                                editor.putString("Message",input.toString());
                                editor.apply();
                            }
                        });
                MaterialDialog dialog = builder.build();
                dialog.show();
            }
        });
        Intent intent = new Intent(this, FakeCallReceiver.class);

        intent.putExtra("FAKENAME", "Papa");
        intent.putExtra("FAKENUMBER", "9412164248");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0 ,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.staysafe)
                .setContentText("Fake call after 10 seconds")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(R.drawable.phone,"CALL",pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

/*    public void setUpAlarm(long selectedTimeInMilliseconds, String fakeName, String fakeNumber){

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, FakeCallReceiver.class);

        intent.putExtra("FAKENAME", fakeName);
        intent.putExtra("FAKENUMBER", fakeNumber);

        PendingIntent fakePendingIntent = PendingIntent.getBroadcast(this, 0,  intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, 0, fakePendingIntent);
        Toast.makeText(getApplicationContext(), "Your fake call time has been set", Toast.LENGTH_SHORT).show();
    }*/
    private void startService() {
        Intent intent=new Intent(MainActivity.this,MessageService.class);
       /* intent.putExtra("number",number);
        intent.putExtra("serial",serial);
        Toast.makeText(this, "SERVICE STARTED", Toast.LENGTH_SHORT).show();*/
        startService(intent);
    }

 /*   @Override
    protected void onStart() {
        super.onStart();
        if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            if(!isLocationAvailable()){
                LocationPermissionCheck();
            }
            if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    startService();
    }*/
    boolean isLocationAvailable(){
        boolean gpsenabled = false;
        if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            LocationManager location = (LocationManager) MainActivity.this.getSystemService(LOCATION_SERVICE);
            if (location.isProviderEnabled(location.GPS_PROVIDER)) {
                gpsenabled = true;
            }
        }
        return gpsenabled;
    }

    void LocationPermissionCheck()
    {
        if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Permissions.askforPermission(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    new Permissions.OnpermissionResultListner() {
                        @Override
                        public void OnGranted(String fperman) {

                        }

                        @Override
                        public void OnDenied(String fperman) {

                        }
                    });
        }

        else
        {
            AlertDialog.Builder build=new AlertDialog.Builder(MainActivity.this);
            String msg="To continue ,let your device turn on location using Google's Location Service."+
                    System.getProperty("line.separator")+System.getProperty("line.separator");
            build.setCancelable(false).setMessage(msg)
                    .setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            AlertDialog.Builder build=new AlertDialog.Builder(MainActivity.this);
                            String msg="StaySafe services will not work properly please give the loaction permission to work"+
                                    System.getProperty("line.separator")+System.getProperty("line.separator");
                            build.setCancelable(false).setMessage(msg).setTitle("ALERT")
                                    .setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            build.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(i);
                                    dialog.dismiss();
                                }
                            });
                            build.show();
                        }
                    });
            build.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                    dialog.dismiss();
                }
            });
            build.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Permissions.OnPermResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
