package org.krupkas.tunamelt;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class MainDebug extends Activity {

    private final static int INTENT_ID_CREATE_DEFAULT_RECORDS = 1;

    private TextView debugTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_debug);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        debugTextView = findViewById(R.id.main_debug);
        debugTextView.setMovementMethod(new ScrollingMovementMethod());
        displayDebugInfo(debugTextView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_debug, menu);
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

            case R.id.menu_main_debug_create_default_records:
                // make sure the user really wanted to delete it
                DialogFragment newFragment = CreateDefaultRecordsFragment.newInstance();
                newFragment.show(getFragmentManager(), "dialog");
                return true;

            case R.id.menu_main_debug_send_data:
                Intent i = new Intent(this,MainDebugSendData.class);
                startActivity(i);
                return true;

        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void noop() {
        // do nothing
    }

    public void addDefaultRecords(){
        long result;

        DbAdapter db = new DbAdapter(getApplicationContext());
        db.open();

        TMRecord tmRec = new TMRecord("Abe","Alaska Melt",10.0,10.0,"ABC Avenue",10.0,1.0,"Awful",
                "Jan 1, 2001",null,"www.abe.com");
        result = db.insertTMRec(tmRec);
        if (result == -1){
            Log.e("TunaMelt","cannot add record to DB");
        }

        tmRec = new TMRecord("Billy","Big Bite",20.0,20.0,"B Boulevard",20.0,2.0,"Blech!",
                "Feb 2, 2002",null,"http://billy.com");
        result = db.insertTMRec(tmRec);
        if (result == -1){
            Log.e("TunaMelt","cannot add record to DB");
        }

        tmRec = new TMRecord("Cathy's","Chewy Chewy",30.0,30.0,"C Court",30.0,3.0,"Cruddy but edible",
                "Mar 3, 2003",null,"");
        result = db.insertTMRec(tmRec);
        if (result == -1){
            Log.e("TunaMelt","cannot add record to DB");
        }

        tmRec = new TMRecord("Dan's","Daily gut-buster",40.0,40.0,"D street",40.0,4.0,"Disgusting",
                "Apr 4, 2004",null,"dan.com");
        result = db.insertTMRec(tmRec);
        if (result == -1){
            Log.e("TunaMelt","cannot add record to DB");
        }

        tmRec = new TMRecord("Evan's","Edam special",50.0,50.0,"E way",50.0,5.0,"Edible",
                "May 5, 2005",null,"www.evans.com");
        result = db.insertTMRec(tmRec);
        if (result == -1){
            Log.e("TunaMelt","cannot add record to DB");
        }

        db.close();

        // update the debug text in the current view
        displayDebugInfo(debugTextView);
    }

    private void displayDebugInfo(TextView debugTextView)
    {
        // number of tmRecords in DB
        DbAdapter db = new DbAdapter(this);
        db.open();
        ArrayList<TMRecord> tmRecords = db.getAllRecords(Utils.getSortKey(this),Utils.getSortAscending(this));
        db.close();
        int numTmRecs = tmRecords == null ? 0 : tmRecords.size();

        // number of photos
        String mediaStoragePath = Utils.getPhotosPath(this);
        File mediaStorageDir = new File(mediaStoragePath);
        File[] fileList = mediaStorageDir.listFiles();
        ArrayList<String> filenames = new ArrayList<>();
        int numPhotos = 0;
        for (File f: fileList){
            String name = f.getPath();
            filenames.add(name);
            if (name.endsWith(".jpg") || name.endsWith(".JPG")) {
                numPhotos++;
            }
        }

        // orphaned photos - not associated with a tmRec
        ArrayList<String> orphanFilenames = new ArrayList<>(filenames);
        // make collection of photo file names found in tmRecs
        ArrayList<String> goodPhotoFilenames = new ArrayList<>();
        for (TMRecord tmRec : tmRecords) {
            if ((tmRec.getPhotoFile() != null) && !tmRec.getPhotoFile().isEmpty()) {
                goodPhotoFilenames.add(tmRec.getPhotoFile());
            }
        }
        // can't use List.removeAll() - strings may have same characters but they're not the same objects
        Iterator<String> orphanIterator = orphanFilenames.iterator();
        while (orphanIterator.hasNext()) {
            String orphanFilename = orphanIterator.next();
            for (String goodPhotoFilename : goodPhotoFilenames) {
                if (orphanFilename.equals(goodPhotoFilename)) {
                    orphanIterator.remove();
                    break;
                }
            }
        }

        // delete all the orphan files
        int numOrphansDeleted = 0;
        int numOrphansNotDeleted = 0;
        for (String orphanFilename : orphanFilenames) {
            boolean ok = Utils.safeDeleteFile(orphanFilename);
            if (ok) {
                numOrphansDeleted++;
            } else {
                numOrphansNotDeleted++;
            }
        }

        // test json-to-object
        //TMRecord testTmRec = gson.fromJson(tmRecJson, TMRecord.class);

        debugTextView.setText(
                "Number of tmRecs = " + numTmRecs + "\n"
                        + "Photo directory: " + mediaStoragePath + "\n"
                        + "Number of photos = " + numPhotos + "\n"
                        + "Number of orphan photos = " + orphanFilenames.size() + "\n"
                        + "Number of orphans deleted = " + numOrphansDeleted + "\n"
                        + "Number of orphans not deleted = " + numOrphansNotDeleted + "\n"

                //+ "Good file names:" + goodPhotoFilenames + "\n"
                //+ "Orphan file names:" + orphanFilenames + "\n"
        );
    }

}
