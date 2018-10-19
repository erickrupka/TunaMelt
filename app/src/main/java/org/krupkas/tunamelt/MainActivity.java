package org.krupkas.tunamelt;

import android.os.Bundle;

import java.util.ArrayList;

import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.content.ComponentName;


public class MainActivity extends Activity {

    static final public int INTENT_ID_PREFERENCES  = 1;
    static final public int INTENT_ID_VIEW_MAP     = 2;
    static final public int INTENT_ID_ADD_TMREC    = 3;
    static final public int INTENT_ID_SHOW_DETAILS = 4;

    static final public String NO_SEARCH_STRING = null;


    private ArrayList<TMRecord> tmRecords = new ArrayList<TMRecord>();
    private TMRecord selectedTMRec;
    ArrayAdapter<TMRecord> aa;
    ListView tunaMeltListView;
    Context context;
    SearchManager searchManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        // create photos and tempData directories if they don't already exist
        Utils.makeNeededDirectories(this);

        // set the default preference values so there's always a valid value
        PreferenceManager.setDefaultValues(this, R.xml.userpreferences, false);

        tunaMeltListView = (ListView)this.findViewById(R.id.tunaMeltListView);

        // when a TMRecord in the list is clicked, show the details of it
        tunaMeltListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> _av, View _v, int _index, long arg3){
                selectedTMRec = tmRecords.get(_index);

                // show the tuna melt's details
                Intent intent = new Intent(context, ShowDetails.class);
                intent.putExtra(getResources().getString(R.string.intent_extra_key_one_tm),
                        selectedTMRec);
                intent.putExtra(getResources().getString(R.string.intent_extra_key_do_add),false);
                startActivityForResult(intent,INTENT_ID_SHOW_DETAILS);
            }
        });

        handleIntent(getIntent());
    }


    private void handleIntent(Intent intent){

        String searchString;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchString = intent.getStringExtra(SearchManager.QUERY);
        }
        else {
            searchString = NO_SEARCH_STRING;
        }

        // read the TMrecords from the database
        DbAdapter db = new DbAdapter(context);
        db.open();

        if (searchString == null){
            tmRecords = db.getAllRecords(Utils.getSortKey(context),Utils.getSortAscending(context));
        }
        else {
            tmRecords = db.getSearchRecords(searchString);
        }
        db.close();

        // create the array adapter to show the TMRecords in a list
        int resID = R.layout.tmrec_brief;
        aa = new TMBriefAdapter(this,resID,tmRecords);
        tunaMeltListView.setAdapter(aa);
    }


    @Override
    protected void onStart() {
        super.onStart();

        // we shouldn't have to update the view every time we get here, but I
        // can't get search.onDismiss() to trigger when a search is dismissed.
        Utils.updateArray(context,NO_SEARCH_STRING,aa);
        tellUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_main_search).getActionView();

        ComponentName name = getComponentName();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        Intent i;

        switch (item.getItemId()){

            case R.id.menu_main_map:
                i = new Intent(this,TunaMeltMap.class);
                startActivityForResult(i,INTENT_ID_VIEW_MAP);
                return true;

            case R.id.menu_main_search:
                // do nothing, Search activity will automatically be started
                return true;

            case R.id.menu_main_add:
                i = new Intent(this,EditTMRec.class);
                i.putExtra(getResources().getString(R.string.intent_extra_key_do_add),true);
                startActivityForResult(i,INTENT_ID_ADD_TMREC);
                return true;

            case R.id.menu_main_settings:
                i = new Intent(this,UserPrefs.class);
                startActivityForResult(i,INTENT_ID_PREFERENCES);
                return true;

            case R.id.menu_main_about:
                i = new Intent(this,AboutTunaMelt.class);
                startActivity(i);
                return true;

            case R.id.menu_main_help:
                i = new Intent(this,MainHelp.class);
                startActivity(i);
                return true;

            case R.id.menu_main_debug:
                i = new Intent(this,MainDebug.class);
                startActivity(i);
                return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case INTENT_ID_ADD_TMREC:
                if (resultCode == Activity.RESULT_OK){
                    Utils.updateArray(context,NO_SEARCH_STRING,aa);
                    tellUI();
                }
                break;

            case INTENT_ID_SHOW_DETAILS:
                if (resultCode == Activity.RESULT_OK){
                    Bundle bundle = data.getExtras();
                    if (bundle.getBoolean(
                            getResources().getString(R.string.intent_extra_key_data_changed)) == true){
                        Utils.updateArray(context,NO_SEARCH_STRING,aa);
                        tellUI();
                    }
                }
                break;

            case INTENT_ID_PREFERENCES:
                Utils.updateArray(context,NO_SEARCH_STRING,aa);
                tellUI();
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

