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

import static com.independant.cashpoint.login.InscriptionActivity.SESSION_PREFERENCE;
import static com.independant.cashpoint.login.InscriptionActivity.SESSION_TELEPHONE;

public class ConfirmationCompte extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT=15000;
    public static final int READ_TIMEOUT=10000;
    SharedPreferences sharedPreferences;
    Intent in;
    EditText etCode, etTelephone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_compte);
        sharedPreferences = getSharedPreferences(SESSION_PREFERENCE ,MODE_PRIVATE);

        etCode = (EditText) findViewById(R.id.code);
        //etTelephone.setText());

    }

    public void checkCode(View arg0)
    {
        final String code = etCode.getText().toString();
        final String telephone =sharedPreferences.getString(SESSION_TELEPHONE, null);
        if (TextUtils.isEmpty(code)) {
            etCode.setError("Entrez le code.");
            etCode.requestFocus();
            return;
        }
        new ConfirmationCompte.AsyncConfirm().execute(code,telephone);
    }

    private class AsyncConfirm extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ConfirmationCompte.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tConfirmation en cours...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://app.e-solutions-rdc.com/Api_cashpay/add_action.php?action=confirmation_compte");


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
                        .appendQueryParameter("code", params[0])
                        .appendQueryParameter("telephone", params[1]);
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
                in = new Intent(ConfirmationCompte.this, HomeActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_rigth,
                        R.anim.slide_out_left);
                ConfirmationCompte.this.finish();
            }else if(result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(ConfirmationCompte.this, "Code de confirmation incorrect", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("exception")) {

                //Toast.makeText(login.this, "Imposible d'éffectuer la demande. Assurez-vous que votre téléphone est connecté à Internet et réessayer.", Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(ConfirmationCompte.this)
                        .setMessage("Imposible d'éffectuer la demande. Assurez-vous que votre téléphone est connecté à Internet et réessayer.")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
            }
        }

    }
}
