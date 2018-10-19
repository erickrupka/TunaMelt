package org.krupkas.tunamelt;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;


public class ShowDetails extends Activity implements View.OnClickListener {
    
    private final static int INTENT_ID_EDIT_TMREC = 1;
    
    private TMRecord tmRec;
    private boolean dataChanged;
    private TextView urlView;
    private ImageView photoView;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
          
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmrec_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        Intent i = getIntent();
        tmRec = (TMRecord)i.getSerializableExtra(
                getResources().getString(R.string.intent_extra_key_one_tm));
        
        dataChanged = false;
        
        displayTmRec(tmRec);
        
        urlView = (TextView)findViewById(R.id.tmrec_details_url);
        urlView.setOnClickListener(this);
        photoView = (ImageView)findViewById(R.id.tmrec_details_photo);
        photoView.setOnClickListener(this);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {  
        Intent i;
        
        switch (item.getItemId()) 
        {
        case android.R.id.home: 
            returnToCaller(Activity.RESULT_OK,dataChanged);
            break;
        case R.id.menu_details_edit:
            i = new Intent(this,EditTMRec.class);
            i.putExtra(getResources().getString(R.string.intent_extra_key_orig_tm_rec),tmRec);
            i.putExtra(getResources().getString(R.string.intent_extra_key_do_add),false);
            startActivityForResult(i,INTENT_ID_EDIT_TMREC);
            return true;
        case R.id.menu_details_delete:
            // make sure the user really wanted to delete it
            DialogFragment newFragment = DeleteFragment.newInstance();
            newFragment.show(getFragmentManager(), "dialog");
            return true;
        case R.id.menu_details_help:
            i = new Intent(this,DetailsHelp.class);
            startActivity(i);
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    public void doPositiveClick() {
        // yes, delete the tmRec
        deleteTmRec(tmRec);
    }
    
    public void doNegativeClick() {
        ; // do nothing
    }


 // Connect actions to buttons
    @Override
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.tmrec_details_url){
            String urlString = urlView.getText().toString();
            if (urlString == null){
                return;
            }
            if (!urlString.startsWith("http://")){
                urlString = "http://" + urlString;
            }
            Uri uri = Uri.parse(urlString);
            Intent i = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(i);
        }
        else if (id == R.id.tmrec_details_photo){
            Intent i = new Intent(getApplicationContext(), FullScreenPhoto.class);
            i.putExtra(getResources().getString(R.string.intent_extra_key_full_screen_photo_filename),
                    tmRec.getPhotoFile());
            startActivity(i);
        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case INTENT_ID_EDIT_TMREC:
                if (resultCode == Activity.RESULT_OK){
                    tmRec = (TMRecord)data.getSerializableExtra(
                            getResources().getString(R.string.intent_extra_key_one_tm));
                    dataChanged = true;
                    if (tmRec != null){
                        displayTmRec(tmRec);
                    }
                    else {
                        Log.e("ShowDetails","tmRec is null");
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    private void deleteTmRec(TMRecord tmRec){
        long result;
            
        DbAdapter db = new DbAdapter(getApplicationContext());
        db.open();
        result = db.deleteTMRec(tmRec);
        db.close();
            
        if (result < 0){
            Toast.makeText(this.getBaseContext(),"Failed to delete tuna melt", 
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this.getBaseContext(),"Deleted tuna melt", 
                    Toast.LENGTH_SHORT).show();
            // tell caller whether we have removed a record
            // delete the photo file
            Utils.safeDeleteFile(tmRec.getPhotoFile());
            returnToCaller(Activity.RESULT_OK,true);
        }
    }
    
    
    private void returnToCaller(int resultCode, boolean changedFlag){
        Intent result = new Intent();   
        result.putExtra(getResources().getString(R.string.intent_extra_key_data_changed),
                changedFlag);
        setResult(resultCode, result);
        finish();
    }
    
    
    private void displayTmRec(TMRecord tmRec){  
        String      restaurant;
        String      address;
        String      dishName;
        double      rating;
        String      ratingString;
        double      price;
        String      priceString;     
        String      comments;
        String      date;
        String      urlString;
    
        restaurant   = tmRec.getRestaurant();
        address      = tmRec.getAddress();
        dishName     = tmRec.getDishName();
        rating       = tmRec.getRating();
        ratingString = String.valueOf(rating);
        price        = tmRec.getPrice();
        priceString  = "$" + String.valueOf(price);
        date         = tmRec.getDate();
        comments     = tmRec.getComments();
        urlString    = tmRec.getUrlString();
    
        TextView tv;
        ImageView photoView;
    
        // show the tuna melt's details
        tv = (TextView)this.findViewById(R.id.tmrec_details_restaurant);
        tv.setText(restaurant);
    
        tv = (TextView)this.findViewById(R.id.tmrec_details_dish_name);
        tv.setText(dishName);
    
        tv = (TextView)this.findViewById(R.id.tmrec_details_address);
        tv.setText(address);
    
        tv = (TextView)this.findViewById(R.id.tmrec_details_rating);
        tv.setText(ratingString);
    
        tv = (TextView)this.findViewById(R.id.tmrec_details_price);
        tv.setText(priceString);
    
        tv = (TextView)this.findViewById(R.id.tmrec_details_date);
        tv.setText(date);
        
        tv = (TextView)this.findViewById(R.id.tmrec_details_url);
        tv.setText(urlString);
    
        tv = (TextView)this.findViewById(R.id.tmrec_details_comments);
        tv.setText(comments);
    
        // display the photo
        photoView = (ImageView)findViewById(R.id.tmrec_details_photo);
        Utils.setImageInView(this, photoView, tmRec.getPhotoFile());
    }
    
}
