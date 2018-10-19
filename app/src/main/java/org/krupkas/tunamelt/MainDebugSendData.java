package org.krupkas.tunamelt;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.net.Uri;
import android.app.Activity;

import com.google.gson.Gson;

public class MainDebugSendData extends Activity {

    private EditText recipientNameEditText;
    private EditText recipientEmailEditText;

    String recipientName;
    String recipientEmail;

    Context mainDebugContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_debug_send_data);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mainDebugContext = getApplicationContext();
    }

    /* Called when the user touches the Send button */
    public void sendTunaMeltData(View view) {
        // hide the keyboard so the user can see any toasts
        Utils.hideKeyboard(this, view);

        if (collectUserInput()) {

            String dtoJson = getDTOJSON();

            Toast toast = Toast.makeText(getApplicationContext(), "Sending data to " + recipientName, Toast.LENGTH_SHORT);
            toast.show();

            /* TODO send data to outside world
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, tmRecJson);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            */
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            String[] addresses = {"eric@krupkas.org"};
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
            intent.putExtra(Intent.EXTRA_SUBJECT, "TunaMelt data");
            intent.putExtra(Intent.EXTRA_TEXT, dtoJson);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                toast = Toast.makeText(getApplicationContext(), "Cannot find email app", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private boolean collectUserInput() {
        // recipient name must be at least one char
        recipientNameEditText = findViewById(R.id.recipient_name);
        recipientName = recipientNameEditText.getText().toString();
        if (recipientName.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please the recipient's name",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // recipient email must look valid
        recipientEmailEditText = findViewById(R.id.recipient_email);
        recipientEmail = recipientEmailEditText.getText().toString();
        boolean emailOk =  android.util.Patterns.EMAIL_ADDRESS.matcher(recipientEmail).matches();
        if (!emailOk) {
            Toast.makeText(getApplicationContext(), "You must enter a valid email address",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String getDTOJSON() {

        TunaMeltDTO dto = new TunaMeltDTO();

        dto.version = "0.0.1";
        dto.owner = Utils.getOwnerName(this);

        // number of tmRecords in DB
        DbAdapter db = new DbAdapter(this);
        db.open();
        dto.tmRecords = db.getAllRecords(Utils.getSortKey(this),Utils.getSortAscending(this));
        db.close();

        // JSON representation of the first tmRec
        Gson gson = new Gson();
        String dtoJson = gson.toJson(dto);

        return dtoJson;
    }
    

    private void returnToCaller(int resultCode){
        Intent i = new Intent();
        setResult(resultCode, i);
        finish();
    }

}