package org.krupkas.tunamelt;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v4.content.FileProvider;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.app.Activity;

public class EditTMRec extends Activity implements View.OnClickListener {
    
    static private final int INTENT_ID_TAKE_PHOTO = 1;
    
    static private final int MAX_IMAGE_PIXELS = 1024;
    
    // do not need to save these across pause/resume
    private EditText restaurantEditText;
    private EditText dishNameEditText;
    private EditText addressEditText;
    private EditText priceEditText;
    private EditText ratingEditText;
    private EditText dateEditText;
    private EditText commentsEditText;
    private ImageView photoView;
    private EditText urlEditText;

    // save these across pause/resume
    private TMRecord origTmRec;  // from calling activity - needed to delete old photo
    private TMRecord currTmRec;  // current values
    private boolean  doAdd;
    private String   newPhotoFilename;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_tmrec);
        
        // If we are coming from a calling activity then there should not be
        // a savedInstanceState.  In that case we expect the origTmRec to be
        // passed to us via an Intent.
        // 
        // If we are resuming, then there should be an original and current
        // TMRecord in the savedInstanceState.
        
        if (savedInstanceState == null){
            // directly from a caller, not pause/stop/start/resume
            Intent i = getIntent();
            doAdd = i.getBooleanExtra(getResources().getString(R.string.intent_extra_key_do_add),true);
            if (doAdd){
                origTmRec = new TMRecord();
                currTmRec = new TMRecord();
            }
            else {
                origTmRec = (TMRecord)i.getSerializableExtra(
                    getResources().getString(R.string.intent_extra_key_orig_tm_rec));
                currTmRec = new TMRecord(origTmRec);
            }     
        }
        else {
            // pause/resume     
            origTmRec = (TMRecord)savedInstanceState.getSerializable(
                getResources().getString(R.string.intent_extra_key_orig_tm_rec));
            currTmRec = (TMRecord)savedInstanceState.getSerializable(
                getResources().getString(R.string.intent_extra_key_curr_tm_rec));  
            doAdd = savedInstanceState.getBoolean(
                getResources().getString(R.string.intent_extra_key_do_add),true);
            newPhotoFilename = savedInstanceState.getString(
                    getResources().getString(R.string.intent_extra_key_new_photo_filename));
        }
        
        if (origTmRec == null){
            Log.e("Edit","origTmRec is null");
            finish();
        }
        if (currTmRec == null){
            Log.e("Edit","currTmRec is null");
            finish();
        }
        
        // find all the input views
        restaurantEditText = (EditText)findViewById(R.id.edit_restaurant);  
        dishNameEditText = (EditText)findViewById(R.id.edit_dish_name);
        addressEditText = (EditText)findViewById(R.id.edit_address);
        priceEditText = (EditText)findViewById(R.id.edit_price);
        ratingEditText = (EditText)findViewById(R.id.edit_rating);
        dateEditText = (EditText)findViewById(R.id.edit_date);
        commentsEditText = (EditText)findViewById(R.id.edit_comments);  
        photoView = (ImageView)findViewById(R.id.tmrec_edit_photo);
        urlEditText = (EditText)findViewById(R.id.edit_url);
        
        // fill the views with the current values
        restaurantEditText.setText(currTmRec.getRestaurant());
        dishNameEditText.setText(currTmRec.getDishName());
        addressEditText.setText(currTmRec.getAddress());
        priceEditText.setText(String.valueOf(currTmRec.getPrice()));
        ratingEditText.setText(String.valueOf(currTmRec.getRating()));
        dateEditText.setText(currTmRec.getDate());
        commentsEditText.setText(currTmRec.getComments());      
        Utils.setImageInView(this, photoView, currTmRec.getPhotoFile());
        photoView.setOnClickListener(this);
        urlEditText.setText(currTmRec.getUrlString());
    }
    

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) 
    {
        Log.d("Edit","onSaveInstanceState");
        savedInstanceState.putSerializable(
              getResources().getString(R.string.intent_extra_key_orig_tm_rec), origTmRec);
        savedInstanceState.putSerializable(
              getResources().getString(R.string.intent_extra_key_curr_tm_rec), currTmRec);
        savedInstanceState.putBoolean(
                getResources().getString(R.string.intent_extra_key_do_add), doAdd);
        savedInstanceState.putString(
                getResources().getString(R.string.intent_extra_key_new_photo_filename),
                newPhotoFilename);
        super.onSaveInstanceState(savedInstanceState);
    } 
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_tuna_melt, menu);
        // show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }
   
        
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        boolean resultOk;
        
        switch (item.getItemId()) {
        case R.id.menu_edit_cancel:
            returnToCaller(Activity.RESULT_CANCELED,null);
            break;
    
        case android.R.id.home:        
            // collect info and return if it is valid
            if (!collectInfo()){
                // some of the user input is invalid - do not go back to caller
                return true;
            }
            resultOk = insertNewTMRecord(origTmRec,currTmRec);
            if (resultOk){
                // delete original image file from disk
                Utils.safeDeleteFile(origTmRec.getPhotoFile());
                returnToCaller(Activity.RESULT_OK,currTmRec);
            }
            else {
                returnToCaller(Activity.RESULT_CANCELED,null);
            }
            break;
            
        case R.id.menu_edit_help:
            Intent i = new Intent(this,EditHelp.class);
            startActivity(i);
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    
    // Connect actions to buttons
    @Override
    public void onClick(View v){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        int id = v.getId();
        if (id == R.id.tmrec_edit_photo){
            dispatchTakePictureIntent();
        }
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "org.krupkas.tunamelt.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
            catch (IOException ex) {
                // Error occurred while creating the File
                Context context = getApplicationContext();
                CharSequence text = "Cannot save photo";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Log.d("Error creating file" + newPhotoFilename, ex.getMessage());
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File photoDir = Utils.getPhotosDir(this);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                photoDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        newPhotoFilename = image.getAbsolutePath();
        return image;
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == INTENT_ID_TAKE_PHOTO){
            if (resultCode == Activity.RESULT_OK){     
                // photo file name was saved when the camera was launched
                // redisplay photo 
                resizeImage();
                Bitmap image = BitmapFactory.decodeFile(newPhotoFilename);
                photoView.setImageBitmap(image);
                
                // delete old file
                Utils.safeDeleteFile(currTmRec.getPhotoFile());
                
                // set currTmRec's filename
                currTmRec.setPhotoFile(newPhotoFilename);
            }
        }
    }
    
    
    private void resizeImage(){
        Bitmap image = BitmapFactory.decodeFile(newPhotoFilename);
        
        int width = image.getWidth();
        int height = image.getHeight();
        int maxDimension = (width > height) ? width : height;
        double scaleFactor = maxDimension/MAX_IMAGE_PIXELS;
        int scaledWidth = (int)(((double)width / scaleFactor) + 0.5);
        int scaledHeight = (int)(((double)height / scaleFactor) + 0.5);

        // Recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(image, scaledWidth, scaledHeight, false);
        
        // save the image as a jpg
        Utils.safeDeleteFile(newPhotoFilename);
        try {
            FileOutputStream out = new FileOutputStream(newPhotoFilename);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
        }
        catch (Exception e){
            Log.e("file system error",e.getMessage());
        }
    }
    
    
    private boolean collectInfo(){
        String restaurant;
        String dishName;
        String addressString;
        String priceString;
        double price;
        String ratingString;
        double rating;
        String date;
        String comments;
        String urlString;
           
        // validate input
        
        // restaurant name must be at least one char
        restaurant = restaurantEditText.getText().toString();
        if (restaurant.length() == 0){
            Toast.makeText(getApplicationContext(), "Please enter a restaurant name", 
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // restaurant name must be at least one char
        dishName = dishNameEditText.getText().toString();
        if (dishName.length() == 0){
            Toast.makeText(getApplicationContext(), "Please enter a dish name", 
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    
        // price must be a valid double
        priceString = priceEditText.getText().toString();
        // remove the $ if the user entered it
        if (priceString.charAt(0) == '$'){
            priceString = priceString.substring(1,priceString.length()-1);
        }
        if (priceString.length() == 0){
            Toast.makeText(getApplicationContext(), "Please enter a price", 
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            price = Double.parseDouble(priceString);
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "Please enter a price", 
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // rating must be an double between 0 and 10 inclusive
        ratingString = ratingEditText.getText().toString();
        if (ratingString.length() == 0){
            Toast.makeText(getApplicationContext(), "Please enter a rating between 0 and 10", 
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            rating = Double.parseDouble(ratingString);
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "Please enter a rating between 0 and 10", 
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if ((rating < 0.0) || (rating > 10.0)){
            Toast.makeText(getApplicationContext(), "Please enter a rating between 0 and 10", 
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // no restrictions on date but if it's null then use current date
        date = dateEditText.getText().toString();
        if (date.length() == 0){
            Date currentDate = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy",Locale.US);
            date = sdf.format(currentDate);
        }
        
        // no restrictions on comments
        comments = commentsEditText.getText().toString();
       
        // no restrictions on address
        // if it is null then use lat/lng to guess at it
        // if it is given then convert it to lat/lng
        addressString = addressEditText.getText().toString();
        
        double latitude = 0.0;
        double longitude = 0.0;
        // if user does not provide address, try to figure out from GPS
        if ((addressString == null) || (addressString.length() == 0)){
            addressString = latLgnToAddress();
        }
        // Convert address to lat/lng.  If user did not enter address then we
        // will have used GPS to find lat/lng, then converted lat/lng to address,
        // then used address to lat/lng.  That is ok because it will make the
        // address and lat/lng consistent.
        Address addr = addrToLatLng(addressString);
        latitude = addr.getLatitude();
        longitude = addr.getLongitude();
        
        String photoFilename;
        if (newPhotoFilename != null){
            photoFilename = newPhotoFilename;
        }
        else {
            photoFilename = currTmRec.getPhotoFile();
        }
        
        // no restrictions on URL
        urlString = urlEditText.getText().toString();
  
        currTmRec = new TMRecord(restaurant,dishName,latitude,longitude,addressString,price,
            rating,comments,date,photoFilename,urlString);
        return true;
    }
    
    
    
    private boolean insertNewTMRecord(TMRecord origTmRec, TMRecord newTmRec){
        long result;
        
        DbAdapter db = new DbAdapter(getApplicationContext());
        db.open();   
        
        if (origTmRec != null){
            result = db.deleteTMRec(origTmRec);
            Log.d("DB insert, result = ",String.valueOf(result));
        }
        result = db.insertTMRec(newTmRec);
        
        db.close();
        
        if (result > 0){
            return true;
        }
        else {
            return false;
        }
    }
    
    
    private void returnToCaller(int resultCode, TMRecord tmRec){  
        Intent i = new Intent();
        i.putExtra(getResources().getString(R.string.intent_extra_key_one_tm),tmRec);
        setResult(resultCode, i);
        finish();
    }
    

    private String latLgnToAddress(){
        Geocoder gc = new Geocoder(this,Locale.getDefault());
        List<Address> addresses = null;
        
        LocationManager locationManager;
        String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(context);
        
        String provider = LocationManager.GPS_PROVIDER;
        // Check if we have permission to access location
        int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(getApplicationContext(), "Location permission not granted", Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        
        try {
            addresses = gc.getFromLocation(lat,lng,10);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0){
                Address address = addresses.get(0);
            
                for (int i=0; i<address.getMaxAddressLineIndex(); i++){
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                return sb.toString();
            }
            return null;
        }
        catch (Exception e){
            return null;
        }
    }


    private Address addrToLatLng(String addressString){
        List<Address> addressList;
        
        if (addressString != null){
            Geocoder gc = new Geocoder(this,Locale.getDefault());
            try {
                addressList = gc.getFromLocationName(addressString,1);
                if (addressList != null) {
                    Address address = addressList.get(0);
                    Log.d("addrToLatLng","Lat=" + address.getLatitude() + " Lng=" +
                            address.getLongitude());
                    return address;
                }
            }
            catch (Exception e){
                ;
            }
        }
        
        // conversion from string did not work, use GPS/wifi
        MyLocation myLocation = new MyLocation((Context)this);
        Location location = myLocation.getLocation();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Address address = new Address(Locale.getDefault());
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        return address;
    }

}