package org.krupkas.tunamelt;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailsHelp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_help);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        TextView aboutText = findViewById(R.id.details_help);
        aboutText.setText(
            "Tap \"Edit\" to enter new information.\n"
            + "\n"
            + "Tap \"Delete\" to delete this tuna melt.\n"
            + "\n"
            + "Tap on the URL to go to that website"
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details_help, menu);
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
