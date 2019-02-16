package com.halfdotfull.stay_safe.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.halfdotfull.stay_safe.Database.ContactDataBase;
import com.halfdotfull.stay_safe.Model.AddedContacts;
import com.halfdotfull.stay_safe.R;
import com.halfdotfull.stay_safe.Services.MessageService;

import java.util.ArrayList;

public class Contact extends AppCompatActivity {
    RecyclerView mRecyclerView;
    ArrayList<AddedContacts> list;
    ContactDataBase db;
    Button addContact;
    String number;
    int serial;
    String name;
    Adapter adapter;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        sharedpreferences=getSharedPreferences("StaySafe", Context.MODE_PRIVATE);
        addContact= (Button) findViewById(R.id.AddContact);
        mRecyclerView= (RecyclerView) findViewById(R.id.Recycle);
        list=new ArrayList<>();
        db=new ContactDataBase(Contact.this);
        populateArray();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new Adapter();
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1234);
            }
        });


    }

    private void populateArray() {
        if(list.size()==0){
            Cursor res=db.getAllData();
            if(res.getCount()==0) Toast.makeText(this, "Please enter some data", Toast.LENGTH_SHORT).show();
            else{
                while (res.moveToNext()) {
                    list.add(new AddedContacts(res.getString(0),
                            res.getString(1)));
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
    class contactHolder extends RecyclerView.ViewHolder{

        TextView name,number;
        ImageView ivDelete;

        public contactHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.nameOf);
            number= (TextView) itemView.findViewById(R.id.numberOf);
            ivDelete= (ImageView) itemView.findViewById(R.id.ivDelete);
        }
    }
    class Adapter extends RecyclerView.Adapter<contactHolder>{

        @Override
        public contactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater lf=getLayoutInflater();
            View v=lf.inflate(R.layout.container,parent,false);
            return new contactHolder(v);
        }

        @Override
        public void onBindViewHolder(final contactHolder holder, final int position) {
            final AddedContacts a=list.get(position);
            holder.name.setText(a.getNameAdded());
            holder.number.setText(a.getNumberAdded());
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert=new AlertDialog.Builder(Contact.this);
                    alert.setMessage("Do u really want to delete"+" ?").setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteFromFile(a.getNumberAdded(),a.getNameAdded());
                            list.remove(position);
                            adapter.notifyDataSetChanged();
                        }


                    });
                    AlertDialog alertBox=alert.create();
                    alertBox.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private void deleteFromFile(String number,String name) {
        db.deleteContact(number,name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234){
            if(resultCode == RESULT_OK){

                Uri contactData = data.getData();
                Cursor cursor =  managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                name=   cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                //hello=cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                list.add(new AddedContacts(number,name));
                adapter.notifyDataSetChanged();
                db.insertData(number,name);
                //startService();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void startService() {
        Intent intent=new Intent(Contact.this,MessageService.class);
        intent.putExtra("number",number);
        intent.putExtra("serial",serial);
        startService(intent);
    }
}
