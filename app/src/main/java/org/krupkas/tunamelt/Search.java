package org.krupkas.tunamelt;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Search extends Activity {
    
    private ArrayAdapter<TMRecord> aa;
    private ListView tunaMeltListView;
    private TMRecord selectedTMRec;
    private ArrayList<TMRecord> tmRecords;
    private String query = MainActivity.NO_SEARCH_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());
    }
    
    
    private void handleIntent(Intent intent){   
        
        tunaMeltListView = (ListView)this.findViewById(R.id.tunaMeltListView);   
        
        // when a TMRecord in the list is clicked, show the details of it
        tunaMeltListView.setOnItemClickListener(new OnItemClickListener(){      
            @Override
            public void onItemClick(AdapterView<?> _av, View _v, int _index, long arg3){
                selectedTMRec = tmRecords.get(_index);
                
                // show the tuna melt's details
                Intent intent = new Intent(getApplicationContext(), ShowDetails.class);
                intent.putExtra(getResources().getString(R.string.intent_extra_key_one_tm),
                        selectedTMRec);
                intent.putExtra(getResources().getString(R.string.intent_extra_key_do_add),false);
                startActivityForResult(intent,MainActivity.INTENT_ID_SHOW_DETAILS);
            }
        });         

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          query = intent.getStringExtra(SearchManager.QUERY);
          DbAdapter db = new DbAdapter(getApplicationContext());
          db.open();   
       
          if (query == null){
              tmRecords = db.getAllRecords(Utils.getSortKey(getApplicationContext()),
                      Utils.getSortAscending(getApplicationContext()));
          }
          else {
              tmRecords = db.getSearchRecords(query);
          }
          db.close();
          
          int resID = R.layout.tmrec_brief;
          aa = new TMBriefAdapter(this,resID,tmRecords);
          tunaMeltListView.setAdapter(aa);   
        }
    }

    
    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        // show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {   
        switch (item.getItemId()) {
        case android.R.id.home:  
            finish();
            break;
        }
        return true;
    }
    
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
                
            case MainActivity.INTENT_ID_SHOW_DETAILS:
                if (resultCode == Activity.RESULT_OK){
                    Bundle bundle = data.getExtras();
                    if (bundle.getBoolean(
                        getResources().getString(R.string.intent_extra_key_data_changed)) == true){
                        Utils.updateArray(getApplicationContext(),query,aa);
                        tellUI();
                    }
                }
                break;
           
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
   

    private void tellUI(){
        runOnUiThread(new Runnable() {
            public void run() {
                aa.notifyDataSetChanged();
            }
        });
    }
}
