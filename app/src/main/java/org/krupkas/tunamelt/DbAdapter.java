package org.krupkas.tunamelt;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbAdapter {
    
    // the DB table
    public static final String DB_NAME =            "TunaMeltDB";
    public static final String DB_TABLE_NAME =      "tunaMeltDB";
    public static final String DB_TABLE_INFO =      "info";
    public static final int    DB_VER =             1;
    
    public static final String DB_KEY_ROWID =      "_id";           
    public static final String DB_KEY_RESTAURANT = "restaurant";
    public static final String DB_KEY_DISH_NAME =  "dishName";
    public static final String DB_KEY_LATITUDE =   "latitude";
    public static final String DB_KEY_LONGITUDE =  "longitude";
    public static final String DB_KEY_ADDRESS =    "address";
    public static final String DB_KEY_PRICE =      "price";
    public static final String DB_KEY_RATING =     "rating";
    public static final String DB_KEY_COMMENTS =   "comments";
    public static final String DB_KEY_DATE =       "date";
    public static final String DB_KEY_PHOTO_FILE =  "photoFile";
    public static final String DB_KEY_URL =         "url";
    
    public static final int    DB_COLUMN_RESTAURANT = 1;
    public static final int    DB_COLUMN_DISH_NAME =  2;
    public static final int    DB_COLUMN_LATITUDE =   3;
    public static final int    DB_COLUMN_LONGITUDE =  4;
    public static final int    DB_COLUMN_ADDRESS =    5;
    public static final int    DB_COLUMN_PRICE =      6;
    public static final int    DB_COLUMN_RATING =     7;
    public static final int    DB_COLUMN_COMMENTS =   8;
    public static final int    DB_COLUMN_DATE =       9;
    public static final int    DB_COLUMN_PHOTO_FILE = 10;
    public static final int    DB_COLUMN_URL =        11;
   
    // SQL statement to create the table
    private static final String DB_CREATE_DB = 
        "CREATE TABLE " + DB_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
         DB_KEY_RESTAURANT + " TEXT NOT NULL, " +
         DB_KEY_DISH_NAME + " TEXT NOT NULL, " +
         DB_KEY_LATITUDE + " FLOAT NOT NULL, " +
         DB_KEY_LONGITUDE + " FLOAT NOT NULL, " +
         DB_KEY_ADDRESS + " TEXT NOT NULL, " +
         DB_KEY_PRICE + " FLOAT NOT NULL, " +
         DB_KEY_RATING + " FLOAT NOT NULL, " +
         DB_KEY_COMMENTS + " TEXT NOT NULL, " +
         DB_KEY_DATE +" TEXT NOT NULL, " +
         DB_KEY_PHOTO_FILE +" TEXT NOT NULL, " +
         DB_KEY_URL + " TEXT NOT NULL);";
    
  
    
    // Helper class for our DB
    private static class DatabaseHelper extends SQLiteOpenHelper {

        // Class constructor
        DatabaseHelper(Context c) {
            // instantiate a SQLiteOpenHelper by passing it
            // the context, the database's name, a CursorFactory 
            // (null by default), and the database version.
            super(c, DB_NAME, null, DB_VER);
        }

        // called by the parent class when a DB doesn't exist
        public void onCreate(SQLiteDatabase db) {
            // create the three tables
            db.execSQL(DB_CREATE_DB);
        }
        
        // called by the parent when a DB needs to be upgraded
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
            Log.w("TunaMelt","upgrading DB");
            // remove the old version and create a new one.
            // If we were really upgrading we'd try to move data over
            db.execSQL("DROP TABLE IF EXISTS "+DB_TABLE_INFO);
            onCreate(db);
        }
    }


 
    private final Context context;  
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    // DBAdapter class constructor
    public DbAdapter(Context c) {
        this.context = c;
    }
    
   
   
    // open the  DB
    public DbAdapter open() throws SQLException {
        try {
            helper = new DatabaseHelper(context);

            // the SQLiteOpenHelper class (a parent of DatabaseHelper)
            // has a "getWritableDatabase" method that returns an
            // object of type SQLiteDatabase that represents an open
            // connection to the database we've opened (or created).
            db = helper.getWritableDatabase();
            return this;
        }
        catch (Exception e){
            Log.e("SQL open adapter",e.getMessage());
            return null;
        }
    }
    
    // Close the DB
    public void close() {
        helper.close();
    }
    
    
    // Remove the contents of the DB
    public void clearDb(){
        try {
             db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
             db.execSQL(DB_CREATE_DB);
        }
        catch (Exception e){
            Log.e("SQL",e.getMessage());
        }
    }
   
 
    

    // insert a  record into the table
    public long insertTMRec(TMRecord tmRec){
        long result;
        
        // check for duplicates
        ArrayList<TMRecord> matches = getSearchRecords(tmRec.getRestaurant());
        if (matches.contains(tmRec)){
            // the record is already in the DB
            return 1;
        }
        
        try {
            ContentValues vals = new ContentValues();
            vals.put(DB_KEY_RESTAURANT, tmRec.getRestaurant());
            vals.put(DB_KEY_DISH_NAME, tmRec.getDishName());
            vals.put(DB_KEY_LATITUDE, tmRec.getLatitude());
            vals.put(DB_KEY_LONGITUDE, tmRec.getLongitude());
            vals.put(DB_KEY_ADDRESS, tmRec.getAddress());
            vals.put(DB_KEY_PRICE, tmRec.getPrice());
            vals.put(DB_KEY_RATING, tmRec.getRating());
            vals.put(DB_KEY_COMMENTS, tmRec.getComments());
            vals.put(DB_KEY_DATE, tmRec.getDate());
            vals.put(DB_KEY_PHOTO_FILE, tmRec.getPhotoFile());
            vals.put(DB_KEY_URL, tmRec.getUrlString());
            
            result = db.insert(DB_TABLE_NAME, null, vals);
            Log.d("SQL insert, result = ",String.valueOf(result));
            return result;
        }
        catch (Exception e){
            Log.e("SQL insert",e.getMessage());
            return -1;
        }
    }
    
    
    // insert a  record into the table
    public long deleteTMRec(TMRecord tmRec){
        long result;
        
        try {
            result = db.delete(DB_TABLE_NAME, DB_KEY_RESTAURANT + " = ? AND " 
                    + DB_KEY_DISH_NAME + " = ?",
                    new String[] { tmRec.getRestaurant(), tmRec.getDishName() });
            return result;
        }
        catch (Exception e){
            Log.e("SQL delete",e.getMessage());
            return -1;
        }
    }
    
    
    // retrieve all the TMRecords from the table
    public ArrayList<TMRecord> getAllRecords(int sortByColumn, boolean ascending){
        ArrayList<TMRecord> results = new ArrayList<TMRecord>();
        
        // ORDER BY column_name(s) ASC|DESC
        String key;
        switch (sortByColumn){
            case DB_COLUMN_RESTAURANT:   key = DB_KEY_RESTAURANT; break;
            case DB_COLUMN_DISH_NAME:    key = DB_KEY_DISH_NAME; break;
            case DB_COLUMN_ADDRESS:      key = DB_KEY_ADDRESS; break;
            case DB_COLUMN_RATING:       key = DB_KEY_RATING; break;
            case DB_COLUMN_PRICE:        key = DB_KEY_PRICE; break;
            case DB_COLUMN_DATE:         key = DB_KEY_DATE; break;
            default:                     key = DB_KEY_RESTAURANT; break;
        }
        
        if (ascending){
            key += " ASC";
        }
        else {
            key += " DESC";
        }
        
        try {
            Cursor cursor = db.query(
                DB_TABLE_NAME, 
                null, // projection
                null, // selection
                null, // selection args
                null, // groupBy
                null, //having
                key   //orderBy)
            ); 
            
            if (cursor.moveToFirst()){
                TMRecord tmRec;
                do {
                    tmRec = new TMRecord();
                    tmRec.setRestaurant(cursor.getString(DB_COLUMN_RESTAURANT));
                    tmRec.setDishName(cursor.getString(DB_COLUMN_DISH_NAME));
                    tmRec.setLatitude(cursor.getDouble(DB_COLUMN_LATITUDE));
                    tmRec.setLongitude(cursor.getDouble(DB_COLUMN_LONGITUDE));
                    tmRec.setAddress(cursor.getString(DB_COLUMN_ADDRESS));
                    tmRec.setPrice(cursor.getDouble(DB_COLUMN_PRICE));
                    tmRec.setRating(cursor.getDouble(DB_COLUMN_RATING));
                    tmRec.setComments(cursor.getString(DB_COLUMN_COMMENTS));
                    tmRec.setDate(cursor.getString(DB_COLUMN_DATE));
                    tmRec.setPhotoFile(cursor.getString(DB_COLUMN_PHOTO_FILE));
                    tmRec.setUrlString(cursor.getString(DB_COLUMN_URL));
                    results.add(tmRec);
                } while (cursor.moveToNext());
            }
            
            cursor.close();
        
            return results;
    
        }
        catch (Exception e){
           Log.e("SQL query",e.getMessage());
           return null ;
        }
    }
    
    
    // retrieve a filtered set of wines from the Cellar table
    public ArrayList<TMRecord> getSearchRecords(String searchString){
        ArrayList<TMRecord> results = new ArrayList<TMRecord>();
        
        /*
        String where;
        switch (filterByColumn){
            case DB_COLUMN_RESTAURANT:  where = DB_KEY_RESTAURANT; break;
            case DB_COLUMN_DISH_NAME:   where = DB_KEY_DISH_NAME; break;
            case DB_COLUMN_ADDRESS:     where = DB_KEY_ADDRESS; break;
            case DB_COLUMN_COMMENTS:    where = DB_KEY_COMMENTS; break;
            default:                    where = DB_KEY_RESTAURANT; break;
        }
        */
        searchOneKey(DB_KEY_RESTAURANT,searchString,results);
        searchOneKey(DB_KEY_DISH_NAME,searchString,results);
        searchOneKey(DB_KEY_COMMENTS,searchString,results);
       
        return results;
    }
        
  
    private void searchOneKey(String key, String searchString, ArrayList<TMRecord>records){
        String where = key + " LIKE ?";
        
        try {
            Cursor cursor = db.query(
                DB_TABLE_NAME, 
                null, // projection
                where, // selection
                new String[]{"%" + searchString + "%"}, // selection args
                null, // groupBy
                null, //having
                null   //orderBy)
            ); 
           
            if (cursor.moveToFirst()){
                TMRecord tmRec;
                do {
                    tmRec = new TMRecord();             
                    tmRec.setRestaurant(cursor.getString(DB_COLUMN_RESTAURANT));
                    tmRec.setDishName(cursor.getString(DB_COLUMN_DISH_NAME));
                    tmRec.setLatitude(cursor.getDouble(DB_COLUMN_LATITUDE));
                    tmRec.setLongitude(cursor.getDouble(DB_COLUMN_LONGITUDE));
                    tmRec.setAddress(cursor.getString(DB_COLUMN_ADDRESS));
                    tmRec.setPrice(cursor.getDouble(DB_COLUMN_PRICE));
                    tmRec.setRating(cursor.getDouble(DB_COLUMN_RATING));
                    tmRec.setComments(cursor.getString(DB_COLUMN_COMMENTS));
                    tmRec.setDate(cursor.getString(DB_COLUMN_DATE));
                    tmRec.setPhotoFile(cursor.getString(DB_COLUMN_PHOTO_FILE));
                    tmRec.setUrlString(cursor.getString(DB_COLUMN_URL));
                    
                    if (!records.contains(tmRec)){
                        records.add(tmRec);
                    }
                    else {
                        Log.d("searchOneKey","duplicate: " + tmRec.getRestaurant());
                    }
                } while (cursor.moveToNext());
            }
            
            cursor.close();
        
            return;
    
        }
        catch (Exception e){
           Log.e("SQL query",e.getMessage());
           return;
        }
    }
    
}


