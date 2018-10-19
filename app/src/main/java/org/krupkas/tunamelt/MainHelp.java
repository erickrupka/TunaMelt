package org.krupkas.tunamelt;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainHelp extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_help);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        TextView aboutText = findViewById(R.id.main_help);
        aboutText.setText(
            "Tap \"Map\" to display the locations of your tuna melts on a map.\n"
            + "\n"
            + "Tap \"Add\" to add a new tuna melt.\n"
            + "\n"
            + "Tap on a tuna melt to get details (or delete it).\n"
            + "\n"
            + "Tap the upper right menu button for sorting and filtering options,"
            + "and information about TunaMelt.\n"
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_help, menu);
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
