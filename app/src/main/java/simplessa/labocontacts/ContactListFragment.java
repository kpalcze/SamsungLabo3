package simplessa.labocontacts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactListFragment extends ListFragment {


    public ContactListFragment() {
        // Required empty public constructor
    }
    
    CustomAdapter customAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("List fragment", "list fragment");
        MainActivity.currentFragmentDisplayed = 2;
        Boolean isDownloadingDone = this.getArguments().getBoolean("isDownloaded");
        if (isDownloadingDone) {
            Log.d("List fragment", "list fragment downloading done");
            ArrayList<Contact> arrayOfContacts = MainActivity.contacts;
            customAdapter = new CustomAdapter(inflater.getContext(), arrayOfContacts);
            setListAdapter(customAdapter);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setListShownNoAnimation(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        //start contactDetailFragment
        ContactDetailFragment contactDetailFragment = new ContactDetailFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        contactDetailFragment.setContact(position);
        transaction.replace(R.id.fragment_container, contactDetailFragment);
        //transaction.addToBackStack(null);
        transaction.commit();

        Log.d("essa", "lista: " + l);
        Log.d("essa", "view: " + v);
        Log.d("essa", "position: " + position);
        Log.d("essa", "id: " + id);

    }

}
