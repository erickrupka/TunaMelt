package org.krupkas.tunamelt;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class UserPrefs extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new UserPrefsFrag()).commit();
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        case android.R.id.home: 
            onBackPressed();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


}
