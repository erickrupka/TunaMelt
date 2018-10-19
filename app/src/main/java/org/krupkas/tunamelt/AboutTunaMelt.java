package org.krupkas.tunamelt;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutTunaMelt extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_tuna_melt);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        TextView aboutText = (TextView)findViewById(R.id.about_tuna_melt);
        aboutText.setText(
            "TunaMelt v0.1\n"
            + "\n"
            + "Keep track of your favorite (and least favorite) tuna melts.\n"
            + "\n"
            + "See where they are.\n"
            + "\n"
            + "Sort and filter by name, price, or rating.\n"
            + "\n"
            + "\n"
            + "\n"
            + "\n"
            + "Send comments to VelvetPalace@krupkas.org"
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about_tuna_melt, menu);
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
