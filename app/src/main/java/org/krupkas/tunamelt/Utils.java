package org.krupkas.tunamelt;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;

public class Utils {
    
    public static void setImageInView(Context context, ImageView iv, String imageFilename){
        
        if ((imageFilename == null) || (imageFilename.length() == 0)){
            iv.setImageResource(R.drawable.icon);
            return;
        }

        Bitmap image = BitmapFactory.decodeFile(imageFilename);
        if (image != null){
            iv.setImageBitmap(image);
        }
        else {
            iv.setImageResource(R.drawable.icon);
        }   
    }
    
    
    public static int getSortKey(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortByString = prefs.getString(context.getResources().getString(R.string.pref_sort_by),null);
 
        if ((sortByString == null) || (sortByString.length() == 0)){
            return DbAdapter.DB_COLUMN_RESTAURANT;
        }
        
        if (sortByString.equals(DbAdapter.DB_KEY_RESTAURANT)){
            return DbAdapter.DB_COLUMN_RESTAURANT;
        }
        if (sortByString.equals(DbAdapter.DB_KEY_DISH_NAME)){
            return DbAdapter.DB_COLUMN_DISH_NAME;
        }
        if (sortByString.equals(DbAdapter.DB_KEY_RATING)){
            return DbAdapter.DB_COLUMN_RATING;
        }
        
        return DbAdapter.DB_COLUMN_RESTAURANT;
    }
    
    
    public static boolean getSortAscending(Context context){  
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrderString = prefs.getString(
                context.getResources().getString(R.string.pref_sort_order),null);
        
        if ((sortOrderString == null) || (sortOrderString.length() == 0)){
            return true;
        }

        return sortOrderString.equals("Ascending");
    }
    
    
    public static void updateArray(Context context, String searchString, ArrayAdapter<TMRecord> aa){ 
        
        int sortKey = Utils.getSortKey(context);
        boolean ascending = Utils.getSortAscending(context);
        ArrayList<TMRecord> tmRecords;
        
        DbAdapter db = new DbAdapter(context);
        db.open();   
        
        if ((searchString == null) || (searchString.length() == 0)){
            tmRecords = db.getAllRecords(sortKey,ascending);
        }
        else {
            tmRecords = db.getSearchRecords(searchString);
        }    
        
        db.close();
        
        // notifyDataSetChanged() only works when records are added via one of the
        // adapter's methods.  Simply updating the arrayList is not sufficient.
        aa.clear();
        aa.addAll(tmRecords);
    }

    public static boolean safeDeleteFile(String filename){
        // if the filename is empty be nice and return true (ok)
        if (filename == null){
            return true;
        }
        if (filename.length() == 0){
            return true;
        }
        File file = new File(filename);
        if (file != null) {
             return file.delete();
        } else {
            return false;
        }
    }

    public static String getPhotosPath(Context context) {
        File baseDir = context.getFilesDir();
        String photosDir = baseDir.getPath() + "/" + context.getResources().getString(R.string.photo_dir);
        return photosDir;
    }

    public static File getPhotosDir(Context context) {
        return new File(getPhotosPath(context));
    }

    public static String getTempDataPath(Context context) {
        File baseDir = context.getFilesDir();
        String imageFileDir = baseDir.getPath() + "/" + context.getResources().getString(R.string.tempData_dir);
        return imageFileDir;
    }

    public static File getTempDataDir(Context context) {
        return new File(getTempDataPath(context));
    }

    // create photos and tempData directories if they don't already exist
    public static boolean makeNeededDirectories(Context context) {
        File imageDir = Utils.getPhotosDir(context);
        if (!imageDir.exists()) {
            if (!imageDir.mkdirs()) {
                Log.e(context.getResources().getString(R.string.app_name), "failed to create photos directory");
                Toast.makeText(context.getApplicationContext(), "File system error",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        File tempDataDir = Utils.getTempDataDir(context);
        if (!tempDataDir.exists()) {
            if (!tempDataDir.mkdirs()) {
                Log.e(context.getResources().getString(R.string.app_name), "failed to create tempData directory");
                Toast.makeText(context.getApplicationContext(), "File system error",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    // see if we're allowed to access the location service
    public static boolean allowedToUseLocationService(Context context) {
        int permissionLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            Context appContext = context.getApplicationContext();
            CharSequence text = "Cannot access location service";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(appContext, text, duration);
            toast.show();
            return false;
        } else {
            return true;
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String getOwnerName(Activity activity) {
        String ownerName;
        // check if we have permission to read contacts
        PackageManager manager = activity.getPackageManager();
        int hasPermission = manager.checkPermission ("android.permission.READ_CONTACTS", "org.krupkas.tunamelt");
        if (hasPermission != manager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(activity.getApplicationContext(), "Permission to read contacts not granted", Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }
        Cursor c = activity.getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        ownerName = c.getString(c.getColumnIndex("display_name"));
        c.close();
        return ownerName;
    }

}
