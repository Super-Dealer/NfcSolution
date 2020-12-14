package com.independant.cashpoint.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.independant.cashpoint.CentralActivity;
import com.independant.cashpoint.HomeActivity;
import com.independant.cashpoint.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class login extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT=15000;
    public static final int READ_TIMEOUT=10000;
    private EditText etTelephone, etPassword;
    public static final String SESSION_PREFERENCE = "SESSION_PREFERENCE";
    public static final String SESSION_TELEPHONE = "SESSION_TELEPHONE";
    SharedPreferences  sharedPreferences;
    Intent in;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(SESSION_PREFERENCE, login.MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences("SESSION_PREFERENCE", MODE_PRIVATE);
        String telphone = preferences.getString("SESSION_TELEPHONE", "");

        if(!telphone.equals(""))
        {
            Intent welcomIntent = new Intent(login.this, CentralActivity.class);
            startActivity(welcomIntent);
            login.this.finish();
        }

        etPassword = (EditText) findViewById(R.id.pwd);
        etTelephone = (EditText) findViewById(R.id.telephone);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    public void gotoInscription(View arg0)
    {
        Intent welcomIntent = new Intent(login.this, InscriptionActivity.class);
        startActivity(welcomIntent);
        login.this.finish();
    }

    public void Login(View arg0)
    {
        //Connexion au serveur
        final String telephone = etTelephone.getText().toString();
        final String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(telephone)) {
            etTelephone.setError("Entrez un numéro de téléphone.");
            etTelephone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Le mot de passe est requis");
            etPassword.requestFocus();
            return;
        }
        // Initialize  AsyncLogin() class with telephone and password
        new AsyncLogin().execute(telephone,password);
    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(login.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tVeuillez patienter...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://app.e-solutions-rdc.com/MySolution-Cloud/Api_cashpay/get_action.php?action=login");


            } catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("telephone", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK)
                {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            final String message;

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                in = new Intent(login.this, CentralActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_rigth,
                        R.anim.slide_out_left);
                login.this.finish();
            }else if(result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(login.this, "Téléphone ou Mot de passe incorrect", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("exception")) {

                //Toast.makeText(login.this, "Imposible d'éffectuer la demande. Assurez-vous que votre téléphone est connecté à Internet et réessayer.", Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(login.this)
                        .setMessage("Imposible d'éffectuer la demande. Assurez-vous que votre téléphone est connecté à Internet et réessayer.")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
            }
        }

    }
}
