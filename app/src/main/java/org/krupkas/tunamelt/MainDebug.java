package org.krupkas.tunamelt;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class MainDebug extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_debug);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        TextView aboutText = (TextView)findViewById(R.id.main_debug);

        // number of tmRecords in DB
        DbAdapter db = new DbAdapter(this);
        db.open();
        ArrayList<TMRecord> tmRecords = db.getAllRecords(Utils.getSortKey(this),Utils.getSortAscending(this));
        db.close();
        int numTmRecs = tmRecords == null ? 0 : tmRecords.size();

        // number of photos
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String mediaStoragePath = mediaStorageDir.getPath();
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
        // can't use removeAll - strings may have same characters but they're not the same objects
        //orphanFilenames.removeAll(goodPhotoFilenames);
        Iterator<String> orphanIterator = orphanFilenames.iterator();
        while (orphanIterator.hasNext()) {
            for (String goodPhotoFilename : goodPhotoFilenames) {
                String orphanFilename = orphanIterator.next();
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

        aboutText.setText(
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
     
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
