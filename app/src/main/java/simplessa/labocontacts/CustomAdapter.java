package simplessa.labocontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by K on 2017-01-03.
 */

public class CustomAdapter extends ArrayAdapter<Contact> {

    public CustomAdapter(Context context, ArrayList<Contact> contacts){
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Contact contact = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
        }
        TextView lastname = (TextView) convertView.findViewById(R.id.lastname);
        TextView firstname = (TextView) convertView.findViewById(R.id.firstname);

        lastname.setText(contact.getLastname()+ " ");
        firstname.setText(contact.getFirstname());
        return convertView;
    }
}
