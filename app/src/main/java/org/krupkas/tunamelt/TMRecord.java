package org.krupkas.tunamelt;

import java.io.Serializable;

import android.util.Log;

/*
 * NOTE: SQL DB does not like it if fields are null, so change any nulls to empty strings.
 */


public class TMRecord implements Serializable {
    
    static final long serialVersionUID = 1;
    
    private String      restaurant;
    private String      dishName;
    private double      latitude;     // using these because LatLng is not serializable
    private double      longitude;
    private String      address;
    private double      price;
    private double      rating;
    private String      comments;
    private String      date;
    private String      photoFile;
    private String      urlString;
    
    public TMRecord(String _restaurant, String _dishName, double _latitude, double _longitude, 
            String _address, double _price, double _rating, String _comments, String _date,
            String _photoFile, String _urlString){
        
        // restaurant and dishName are keys in the DB, so do not allow them to be null
        if ((_restaurant == null) || (_dishName == null)){
            Log.e("TMRecord constructor","null input values");
            return;
        }
        
        restaurant = _restaurant;
        dishName = _dishName;
        latitude = _latitude;
        longitude = _longitude;
        
        if (_address == null){
            address = "";
        }
        else {
            address = _address;
        }
        
        price = _price;
        rating = _rating;
        
        if (_comments == null){
            comments = "";
        }
        else {
            comments = _comments;
        }
        
        if (_date == null){
            date = "";
        }
        else {
            date = _date;
        }
    
        if (_photoFile == null){
            photoFile = "";
        }
        else {
            photoFile = _photoFile;
        }
        
        if (_urlString == null){
            urlString = "";
        }
        else {
            urlString = _urlString;
        }
    }
    
    
    public TMRecord(){
        restaurant = "";
        dishName = "";
        latitude = 0.0; 
        longitude = 0.0;
        address = "";
        price = 0.0;
        rating = 0.0;
        comments = "";
        date = "";
        photoFile = "";
        urlString = "";
    }
    
    
    public  TMRecord(TMRecord tmRec){  
        restaurant = tmRec.restaurant;
        dishName = tmRec.dishName;
        latitude = tmRec.latitude;
        longitude = tmRec.longitude;
        address = tmRec.address;
        price = tmRec.price;
        rating = tmRec.rating;
        comments = tmRec.comments;
        date = tmRec.date;
        photoFile = tmRec.photoFile; 
        urlString = tmRec.urlString;
    }
    
    
    public String getRestaurant(){
        return restaurant;
    }
    
    public String getDishName(){
        return dishName;
    }
    
    public double getLatitude(){
        return latitude;
    }
    
    public double getLongitude(){
        return longitude;
    }
    
    public String getAddress(){
        return address;
    }
    
    public double getPrice(){
        return price;
    }
    
    public double getRating(){
        return rating;
    }
    
    public String getComments(){
        return comments;
    }
    
    public String getDate(){
        return date;
    }
    
    public String getPhotoFile(){
        return photoFile;
    }
    
    public String getUrlString(){
        return urlString;
    }

    
    public void setRestaurant(String _restaurant){
        if ((_restaurant == null) || (_restaurant.length() == 0)){
            return;
        }
        restaurant = _restaurant;
    }
    
    public void setDishName(String _dishName){
        if ((_dishName == null) || (_dishName.length() == 0)){
            return;
        }
        dishName = _dishName;
    }
    
    public void setLatitude(double _latitude){
        latitude = _latitude;
    }
    
    public void setLongitude(double _longitude){
        longitude = _longitude;
    }
    
    public void setAddress(String _address){
        if (_address == null){
            address = "";
        }
        else {
            address = _address;
        }
    }
    
    public void setPrice(double _price){
        price = _price;
    }
    
    public void setRating(double _rating){
        rating = _rating;
    }
    
    public void setComments(String _comments){
        if (_comments == null){
            comments = "";
        }
        else {
            comments = _comments;
        }
    }
    
    public void setDate(String _date){
        if (_date == null){
            date = "";
        }
        else {
            date = _date;
        }
    }
    
    public void setPhotoFile(String _photoFile){
        if (_photoFile == null){
            photoFile = "";
        }
        else {
            photoFile = _photoFile;
        }
    }
    
    public void setUrlString(String _urlString){
        if (_urlString == null){
            urlString = "";
        }
        else {
            urlString = _urlString;
        }
    }
    
    
    public String toString(){
        String s = new String();
        
        s = restaurant + " " + dishName + " " + rating;
        return s;
    }   

    
    @Override
    public boolean equals(Object object){
        if (object == null){
            return false;
        }
        if (!(object instanceof TMRecord)){
            return false;
        }
        
        TMRecord b = (TMRecord)object;
    
        if (!restaurant.equals(b.restaurant)){
            return false;
        }
        if (!dishName.equals(b.dishName)){
            return false;
        }
        if (latitude != b.latitude){
            return false;
        }
        if (longitude != b.longitude){
            return false;
        }
        if (!address.equals(b.address)){
            return false;
        }
        if (price != b.price){
            return false;
        }
        if (rating != b.rating){
            return false;
        }
        if (!comments.equals(b.comments)){
            return false;
        }
        if (!date.equals(b.date)){
            return false;
        }
        if (!photoFile.equals(b.photoFile)){
            return false;
        }
        if (!urlString.equals(b.urlString)){
            return false;
        }
        return true;
    }

}
