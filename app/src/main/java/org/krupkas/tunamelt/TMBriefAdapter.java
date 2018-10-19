package org.krupkas.tunamelt;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class TMBriefAdapter extends ArrayAdapter<TMRecord>{

    int resource;
    Context context;
    
    public TMBriefAdapter(Context _context, int _resource, List<TMRecord> _items){
        super(_context,_resource,_items);
        resource = _resource;
        context = _context;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        RelativeLayout tmRecBriefView;
        
        TMRecord tmRec = getItem(position);
        
        String restaurant = tmRec.getRestaurant();
        String dishName = tmRec.getDishName();
        double rating = tmRec.getRating();
        String ratingString = String.valueOf(rating);
        double price = tmRec.getPrice();
        String priceString = String.valueOf(price);
        
        if (convertView == null){
            tmRecBriefView = new RelativeLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(resource,tmRecBriefView,true);
        }
        else {
            tmRecBriefView = (RelativeLayout)convertView;
        }
        
        ImageView photoView = (ImageView)tmRecBriefView.findViewById(R.id.tmrec_brief_photo);
        Utils.setImageInView(context, photoView, tmRec.getPhotoFile());
        
        TextView restaurantView = (TextView)tmRecBriefView.findViewById(R.id.tmrec_brief_restaurant);  
        restaurantView.setText(restaurant);
        
        TextView dishNameView = (TextView)tmRecBriefView.findViewById(R.id.tmrec_brief_dish_name);  
        dishNameView.setText(dishName);
        
        TextView ratingView = (TextView)tmRecBriefView.findViewById(R.id.tmrec_brief_rating);  
        ratingView.setText("Rating: " + ratingString);
        
        TextView priceView = (TextView)tmRecBriefView.findViewById(R.id.tmrec_brief_price);  
        priceView.setText("$ " + priceString);
        
        return tmRecBriefView;
    }
}
