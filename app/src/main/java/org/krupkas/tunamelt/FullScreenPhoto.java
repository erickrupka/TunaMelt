package org.krupkas.tunamelt;

import java.io.File;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.app.Activity;

public class FullScreenPhoto extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        setContentView(R.layout.full_screen_photo);
        ImageView photoView = (ImageView)findViewById(R.id.tmrec_full_screen_photo);
          
        Intent i = getIntent();
        String photoFilename = i.getStringExtra(
                getResources().getString(R.string.intent_extra_key_full_screen_photo_filename));

        /*
        File mediaStorageDir;
        mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), 
                getResources().getString(R.string.app_name));
        */
        File mediaStorageDir = Utils.getPhotosDir(this);

        /* TODO remove and check for dirs at start up
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.e(getResources().getString(R.string.app_name), "failed to create directory");
                Toast.makeText(getApplicationContext(), "File system error", 
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
        */
        Bitmap image = BitmapFactory.decodeFile(mediaStorageDir.getPath() 
                 + File.separator + photoFilename);
        if (image != null){
             photoView.setImageBitmap(image);
        }
        else {
             photoView.setImageResource(R.drawable.icon);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.full_screen_photo, menu);
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
