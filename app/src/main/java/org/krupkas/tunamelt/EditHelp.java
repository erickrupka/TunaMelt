package org.krupkas.tunamelt;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class EditHelp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_help);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        TextView aboutText = findViewById(R.id.edit_help);
        aboutText.setText(
            "Enter information in each field.\n"
            + "\n"
            + "Tap the back arrow to update this tuna melt and return to the Details page.\n"
            + "\n"
            + "Tap \"Cancel\" to abandon the changes and return to the Details page.\n"
            + "\n"
            + "Tap the photo (or icon) for a closer look or to take a new photo.\n"
            + "\n"
            + "To have the address automatically entered, leave it blank.  The accuracy of\n"
            + "the automatically added address may leave something to be desired."
            + "\n"
            + "To have the date automatically entered, leave it blank.\n"
            + "\n"
            + "If any information does not make sense when you tap the back arrow, "
            + "a message will appear and you will remain on this page.\n"
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_help, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {  
        switch (item.getItemId()) 
        {
        case android.R.id.home: 
            finish();
            break;
     
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
