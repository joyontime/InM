package edu.mit.media.inm;
 
import java.util.ArrayList;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class MainNavigationAdapter extends BaseAdapter {
 
    private ArrayList<String> spinnerNavItem;
    private Context context;
    
    private TextView txtTitle;
 
    public MainNavigationAdapter(Context context,
            ArrayList<String> spinnerNavItem) {
        this.spinnerNavItem = spinnerNavItem;
        this.context = context;
    }
 
    @Override
    public int getCount() {
        return spinnerNavItem.size();
    }
 
    @Override
    public Object getItem(int index) {
        return spinnerNavItem.get(index);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_nav, null);
        }
         
        txtTitle = (TextView) convertView.findViewById(R.id.nav_text);
         
        txtTitle.setText(spinnerNavItem.get(position));
        return convertView;
    }
     
 
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_nav, null);
        }
         
        txtTitle = (TextView) convertView.findViewById(R.id.nav_text);
         
        txtTitle.setText(spinnerNavItem.get(position));
        return convertView;
    }
 
}