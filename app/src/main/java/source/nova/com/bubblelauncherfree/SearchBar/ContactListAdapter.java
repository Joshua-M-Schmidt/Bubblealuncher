package source.nova.com.bubblelauncherfree.SearchBar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Util.Contacts;

public class ContactListAdapter extends ArrayAdapter<Contacts.ContactInfo> {
    public ContactListAdapter(Context context, ArrayList<Contacts.ContactInfo> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Contacts.ContactInfo contact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_item, parent, false);
        }
        // Lookup view for data population

        TextView appName =  convertView.findViewById(R.id.app_name);

        ImageView appicon = convertView.findViewById(R.id.app_icon);
        // Populate the data into the template view using the data object
        appName.setText(contact.getName());
        appicon.setImageResource(R.drawable.contacts_image);

        // Return the completed view to render on screen
        return convertView;
    }
}
