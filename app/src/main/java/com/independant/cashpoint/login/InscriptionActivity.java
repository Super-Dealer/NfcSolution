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

public class InscriptionActivity extends AppCompatActivity {
    Intent in;
    EditText etIdenite, etMail, etPwd, etPwd2, etVille, etTelephone;
    SharedPreferences  sharedPreferences;
    public static final int CONNECTION_TIMEOUT=15000;
    public static final int READ_TIMEOUT=10000;
    public static final String SESSION_PREFERENCE = "SESSION_PREFERENCE";
    public static final String SESSION_TELEPHONE = "SESSION_TELEPHONE";
    public static final String SESSION_IDENTITE = "SESSION_IDENTITE";
    public static final String SESSION_EMAIL = "SESSION_EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        sharedPreferences = getSharedPreferences(SESSION_PREFERENCE, InscriptionActivity.MODE_PRIVATE);

        etIdenite = (EditText) findViewById(R.id.identite);
        etMail = (EditText) findViewById(R.id.email);
        etPwd = (EditText) findViewById(R.id.password);
        etPwd2 = (EditText) findViewById(R.id.password2);
        etVille = (EditText) findViewById(R.id.ville);
        etTelephone = (EditText) findViewById(R.id.telephone);
    }

    public void checkReturn(View arg0)
    {
        in = new Intent(InscriptionActivity.this, login.class);
        startActivity(in);
        overridePendingTransition(R.anim.slide_in_rigth,
                R.anim.slide_out_left);
        InscriptionActivity.this.finish();
    }

    public void checkRegister(View arg0)
    {
        final String identite = etIdenite.getText().toString();
        final String telephone = etTelephone.getText().toString();
        final String email = etMail.getText().toString();
        final String password = etPwd.getText().toString();
        final String password2 = etPwd2.getText().toString();
        final String ville = etVille.getText().toString();
        final String abonnement;
        String jk = null;

        if (TextUtils.isEmpty(identite))
        {
            etIdenite.setError("Votre Identité est requise.");
            etIdenite.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(telephone))
        {
            etTelephone.setError("Entrez le numéro de téléphone.");
            etTelephone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ville))
        {
            etVille.setError("Veuillez entrer une adresse.");
            etVille.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            etPwd.setError("Entrez un mot de passe.");
            etPwd.requestFocus();
            return;
        }
        new InscriptionActivity.AsyncRegister().execute(identite,email,telephone,password,ville);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SESSION_EMAIL, email);
        editor.putString(SESSION_TELEPHONE, telephone);
        editor.putString(SESSION_IDENTITE, identite);

        editor.commit();


    }

    private class AsyncRegister extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(InscriptionActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tChargement...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://app.e-solutions-rdc.com/Api_cashpay/add_action.php?action=new_account");


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
                        .appendQueryParameter("identite", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("telephone", params[2])
                        .appendQueryParameter("password", params[3])
                        .appendQueryParameter("ville", params[4]);

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

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                Intent intent = new Intent(InscriptionActivity.this, ConfirmationCompte.class);
                startActivity(intent);
                InscriptionActivity.this.finish();


            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(InscriptionActivity.this, "Un compte a été trouvé portant ce numéro", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception")) {

                //Toast.makeText(Resgister.this, "Imposible d'éffectuer la demande. Assurez-vous que votre téléphone est connecté à Internet et réessayer..", Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(InscriptionActivity.this)
                        .setMessage("Imposible d'éffectuer la demande. Assurez-vous que votre téléphone est connecté à Internet et réessayer.")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
            }
        }


    }

}
