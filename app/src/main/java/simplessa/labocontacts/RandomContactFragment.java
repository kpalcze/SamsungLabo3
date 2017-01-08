package simplessa.labocontacts;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RandomContactFragment extends Fragment implements View.OnClickListener{


    private int index;
    private ProgressBar progressBar;
    private Contact contact;
    private boolean isDownloadingRandomContactDone = false;
    private String lastname, firstname, email, phone, company;
    private TextView t1, t2, t3, t4, t5, t31, t41, t51;
    private ImageButton b1, b2, b3;

    public RandomContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_detail, container, false);
        MainActivity.currentFragmentDisplayed = 1;

        Log.d("Random fragment", "random fragment");

        Boolean isDownloadingDone = this.getArguments().getBoolean("isDownloaded");
        lastname = this.getArguments().getString("lastname");
        firstname = this.getArguments().getString("firstname");
        email = this.getArguments().getString("email");
        phone = this.getArguments().getString("phone");
        company = this.getArguments().getString("company");

        t1 = (TextView) view.findViewById(R.id.lastname);
        t2 = (TextView) view.findViewById(R.id.firstname);
        t3 = (TextView) view.findViewById(R.id.email);
        t31 = (TextView) view.findViewById(R.id.emailText);
        t4 = (TextView) view.findViewById(R.id.phone);
        t41 = (TextView) view.findViewById(R.id.phoneText);
        t5 = (TextView) view.findViewById(R.id.company);
        t51 = (TextView) view.findViewById(R.id.companyText);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        ImageButton b1 = (ImageButton) view.findViewById(R.id.messageButton);
        b1.setVisibility(View.INVISIBLE);
        b1.setOnClickListener(this);
        ImageButton b2 = (ImageButton) view.findViewById(R.id.phoneButton);
        b2.setVisibility(View.INVISIBLE);
        b2.setOnClickListener(this);
        ImageButton b3 = (ImageButton) view.findViewById(R.id.emailButton);
        b3.setVisibility(View.INVISIBLE);
        b3.setOnClickListener(this);

        if (isDownloadingDone){
            t31.setText(R.string.emailText);
            t41.setText(R.string.phoneText);
            t51.setText(R.string.companyText);
            t1.setText(lastname);
            t2.setText(firstname);
            t3.setText(phone);
            t4.setText(email);
            t5.setText(company);
            b1.setVisibility(View.VISIBLE);
            b2.setVisibility(View.VISIBLE);
            b3.setVisibility(View.VISIBLE);
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        PackageManager packageManager = getActivity().getPackageManager();
        switch (v.getId()) {
            case R.id.messageButton:
                Intent sendMessage = new Intent(Intent.ACTION_VIEW);
                sendMessage.setData(Uri.fromParts("sms", phone, null));

                List<ResolveInfo> activities = packageManager.queryIntentActivities(sendMessage, 0);
                boolean isSendMessageSafe = activities.size() > 0;

                if (isSendMessageSafe)
                    startActivity(sendMessage);
                else
                    Toast.makeText(getActivity(), "Couldn't execute intent", Toast.LENGTH_SHORT).show();
                break;
            case R.id.phoneButton:
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:" + phone));

                packageManager = getActivity().getPackageManager();
                List<ResolveInfo> activities2 = packageManager.queryIntentActivities(call, 0);
                boolean isCallSafe = activities2.size() > 0;

                if (isCallSafe)
                    startActivity(call);
                else
                    Toast.makeText(getActivity(), "Couldn't execute intent", Toast.LENGTH_SHORT).show();
                break;
            case R.id.emailButton:
                Intent sendEmail = new Intent(Intent.ACTION_SENDTO);
                sendEmail.setData(Uri.parse("mailto:" + email));

                packageManager = getActivity().getPackageManager();
                List<ResolveInfo> activities3 = packageManager.queryIntentActivities(sendEmail, 0);
                boolean isSendEmailSafe = activities3.size() > 0;

                ComponentName emailApp = sendEmail.resolveActivity(getActivity().getPackageManager());
                ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
                boolean hasEmailApp = emailApp != null && !emailApp.equals(unsupportedAction);

                //sendEmail.setType("message/rfc822");
                //Intent sendEmailChooser = Intent.createChooser(sendEmail, "Send E-mail...");
                if (isSendEmailSafe && hasEmailApp) {
                    Log.d("essa", "isSendEmailSafe");
                    startActivity(sendEmail);
                } else
                    Toast.makeText(getActivity(), "Couldn't execute intent", Toast.LENGTH_SHORT).show();
                //startActivity(Intent.createChooser(sendEmail, "Send E-mail..."));
                break;
            default:
                break;
        }
    }


    /*
    private class DownloadRandomContact extends AsyncTask<String, Void, Contact> {
        protected void onPreExecute() {
            try {
                progressBar.setVisibility(View.VISIBLE);
            } catch (NullPointerException e) {
                Log.d("error", "NullPointerException");
            }
            super.onPreExecute();
        }
        protected Contact doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                JSONObject json = new JSONObject(builder.toString());
                urlConnection.disconnect();

                Gson gson = new Gson();

                contact = gson.fromJson(json.toString(), Contact.class);
                Log.d("fragment ", "contact: " + contact.getLastname());
                lastname = contact.getLastname();
                firstname = contact.getFirstname();
                email = contact.getEmail();
                phone = contact.getPhone();
                company = contact.getCompany();

                return contact;
            } catch (IOException e) {
                Log.e("error", "JSON file could not be read");
            } catch (JSONException e) {
                Log.e("error", "String could not be converted to JSONObject");
            }
            return null;
        }

        protected void onPostExecute(Contact contact){
            if (contact != null) {
                isDownloadingRandomContactDone = true;
                try {
                    progressBar.setVisibility(View.GONE);
                    b1.setVisibility(View.VISIBLE);
                    b2.setVisibility(View.VISIBLE);
                    b3.setVisibility(View.VISIBLE);
                    t1.setText(contact.getLastname());
                    t2.setText(contact.getFirstname());
                    t3.setText(contact.getEmail());
                    t4.setText(contact.getPhone());
                    t5.setText(contact.getCompany());
                } catch (NullPointerException e) {
                    Log.d("error", "NullPointerException");
                }

            }
        }
    }
    */
}
