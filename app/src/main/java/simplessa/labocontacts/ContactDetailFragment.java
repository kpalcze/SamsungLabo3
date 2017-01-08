package simplessa.labocontacts;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailFragment extends Fragment implements View.OnClickListener {


    private int contactId;
    private ImageButton b1, b2, b3;

    public ContactDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact_detail, container, false);

        MainActivity.currentFragmentDisplayed = 3;

        b1 = (ImageButton)view.findViewById(R.id.messageButton);
        b2 = (ImageButton)view.findViewById(R.id.phoneButton);
        b3 = (ImageButton)view.findViewById(R.id.emailButton);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            TextView t1 = (TextView) view.findViewById(R.id.lastname);
            t1.setText(MainActivity.contacts.get(contactId).getLastname());
            TextView t2 = (TextView) view.findViewById(R.id.firstname);
            t2.setText(MainActivity.contacts.get(contactId).getFirstname());
            TextView t3 = (TextView) view.findViewById(R.id.email);
            t3.setText(MainActivity.contacts.get(contactId).getEmail());
            TextView t31 = (TextView) view.findViewById(R.id.emailText);
            t31.setText(R.string.emailText);
            TextView t4 = (TextView) view.findViewById(R.id.phone);
            t4.setText(MainActivity.contacts.get(contactId).getPhone());
            TextView t41 = (TextView) view.findViewById(R.id.phoneText);
            t41.setText(R.string.phoneText);
            TextView t5 = (TextView) view.findViewById(R.id.company);
            t5.setText(MainActivity.contacts.get(contactId).getCompany());
            TextView t51 = (TextView) view.findViewById(R.id.companyText);
            t51.setText(R.string.companyText);
        }

    }

    @Override
    public void onClick(View v) {
        String phoneNumber = MainActivity.contacts.get(contactId).getPhone();
        String email = MainActivity.contacts.get(contactId).getEmail();
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities;
        switch (v.getId()) {
            case R.id.messageButton:
                Intent sendMessage = new Intent(Intent.ACTION_VIEW);
                sendMessage.setData(Uri.fromParts("sms", phoneNumber, null));

                activities = packageManager.queryIntentActivities(sendMessage, 0);
                boolean isSendMessageSafe = activities.size() > 0;

                if (isSendMessageSafe)
                    startActivity(sendMessage);
                else
                    Toast.makeText(getActivity(),"Couldn't execute intent",Toast.LENGTH_SHORT).show();
                break;
            case R.id.phoneButton:
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:"+ phoneNumber));

                packageManager = getActivity().getPackageManager();
                activities = packageManager.queryIntentActivities(call, 0);
                boolean isCallSafe = activities.size() > 0;

                if (isCallSafe)
                    startActivity(call);
                else
                    Toast.makeText(getActivity(),"Couldn't execute intent",Toast.LENGTH_SHORT).show();
                break;
            case R.id.emailButton:
                Intent sendEmail = new Intent(Intent.ACTION_SENDTO);
                sendEmail.setData(Uri.parse("mailto:" + email));

                packageManager = getActivity().getPackageManager();
                activities = packageManager.queryIntentActivities(sendEmail, 0);
                boolean isSendEmailSafe = activities.size() > 0;

                //sendEmail.setType("message/rfc822");
                //Intent sendEmailChooser = Intent.createChooser(sendEmail, "Send E-mail...");
                if (isSendEmailSafe)
                    startActivity(sendEmail);
                else
                    Toast.makeText(getActivity(),"Couldn't execute intent",Toast.LENGTH_SHORT).show();
                //startActivity(Intent.createChooser(sendEmail, "Send E-mail..."));

                break;
            default:
                break;
        }
    }

    public void setContact(int id) {
        this.contactId = id;
    }

}
