package com.independant.cashpoint;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckCarte extends Fragment {

    NfcAdapter nfcAdapter;
    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_check_carte, container, false);

        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        textView = (TextView)view.findViewById(R.id.textView);

        // Vérifie si l'appareil a le NFC activé et s'il est activé
        if(nfcAdapter == null){
            //Toast.makeText(this, "NFC n'est pas disponible :'(", Toast.LENGTH_LONG).show();
            Snackbar.make(container, "NFC n'est pas disponible.", Snackbar.LENGTH_LONG).show();
        }else if(nfcAdapter.isEnabled()){
            //Toast.makeText(this, "NFC activé", Toast.LENGTH_LONG).show();
            Snackbar.make(container, "NFC activé.", Snackbar.LENGTH_LONG).show();
        }else{
            //Toast.makeText(this, "NFC non activé", Toast.LENGTH_LONG).show();
            Snackbar.make(container, "NFC non activé.", Snackbar.LENGTH_LONG).show();
        }
        return view;
    }

  @Override
    protected void onNewIntent(Intent intent) {
        // Permet de notifier la reconnaissance d'un TAG NFC
        Toast.makeText(getActivity(), "NFC intent reçu!", Toast.LENGTH_LONG).show();

        textView.setText(textView.getText() + "\n" + formatTime());

       super.onNewIntent(intent);

    }



    @Override
    public void onResume() {

        Intent intent = new Intent(getContext(), CheckCarte.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[]{};

        if(nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, intentFilter, null);
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        if(nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(getActivity());
        }

        super.onPause();
    }

    protected String formatTime() {
        long millis = System.currentTimeMillis();
        return String.format("%02d:%02d:%02d", (TimeUnit.MILLISECONDS.toHours(millis) % 24) + 1,
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }



}
