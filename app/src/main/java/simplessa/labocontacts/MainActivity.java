package simplessa.labocontacts;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String eLogTag = "essa";
    private String URL   = "http://4bc5d76d.ngrok.io/db";
                        //= "http://192.168.0.101:3000/db";
    private String randomURL = "http://4bc5d76d.ngrok.io/contacts/5";

    public static ArrayList<Contact> contacts = new ArrayList<>();
    public static int currentFragmentDisplayed = 0;

    private boolean canAddFragment = false;
    private boolean firstTime = true;
    private boolean firstTimeList = true;
    private boolean firstTimeRandom = true;
    private boolean onlyOneRandom = true;
    private boolean isDownloadingDone = false;
    private boolean isDownloadingRandomDone = false;
    private boolean isDownloadingListDone = false;
    private boolean randomContactClicked = false;
    private boolean firstTimeDownload = true;
    private boolean allContactsClicked = false;
    private boolean isConnectedToInternet = false;
    private boolean wasRefreshed = false;

    private String lastname, firstname, phone, email, company;

    private Button button1, button2;
    private ProgressBar progressBar;

    private Contact contact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        getSupportActionBar();
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            canAddFragment = true;
            //checkInternetConnection();
        }

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                if (canAddFragment){
                    randomContactClicked = true;
                    Log.d("random", "clicked " + randomContactClicked);
                    showRandomContactFragment();
                }

                break;
            case R.id.button2:
                if (canAddFragment){
                    allContactsClicked = true;
                    Log.d("list", "clicked " + allContactsClicked);
                    showContactListFragment();
                }
                break;
            default:
                break;
        }
    }

    private void showRandomContactFragment() {
        RandomContactFragment randomContactFragment = new RandomContactFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        boolean isDownloaded = isDownloadingRandomDone;
        bundle.putBoolean("isDownloaded", isDownloaded);
        bundle.putString("lastname", lastname);
        bundle.putString("firstname", firstname);
        bundle.putString("email", email);
        bundle.putString("phone", phone);
        bundle.putString("company", company);
        randomContactFragment.setArguments(bundle);
        checkInternetConnection();
        if (onlyOneRandom && isConnectedToInternet) {
            Log.d("essa", "random connected to internet");
            DownloadList();
        }
        if (firstTime) {
            transaction.add(R.id.fragment_container, randomContactFragment);
        }
        else {
            transaction.replace(R.id.fragment_container, randomContactFragment);
        }
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showContactListFragment() {
        ContactListFragment contactListFragment = new ContactListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        boolean isDownloadDone = isDownloadingListDone;
        bundle.putBoolean("isDownloaded", isDownloadDone);
        contactListFragment.setArguments(bundle);
        Log.d("first time list" , "" + firstTimeList);
        if (firstTimeList) {
            //download data
            checkInternetConnection();
            if (isConnectedToInternet) {
                DownloadList();
            }
        }

        if (firstTime){
            transaction.add(R.id.fragment_container, contactListFragment);
        } else {
            transaction.replace(R.id.fragment_container, contactListFragment);
        }
        //transaction.addToBackStack(null);
        transaction.commit();

        if (!firstTimeList)
            allContactsClicked = false;
    }

    private void checkInternetConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInf = connMgr.getActiveNetworkInfo();
        if (netInf != null && netInf.isConnected()){
            isConnectedToInternet= true;
        } else{
            isConnectedToInternet = false;
            randomContactClicked = false;
            allContactsClicked = false;
            Context context = getApplicationContext();
            CharSequence text = "Turn Wi-Fi or Mobile Data on";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            Log.e(eLogTag, "Connection error");
        }
    }

    private void DownloadList(){
        new DownloadWholeList().execute(URL, createRandomURL());
        //if (isDownloadingDone)
        //    firstTime=false;
        Log.e(eLogTag,"Using AsyncTask");
    }

    private String createRandomURL(){
        double randNumber =  Math.random() * 1000;
        int index = (int) randNumber;
        String url = "http://4bc5d76d.ngrok.io/contacts/" + index;
        return url;
    }

    private class DownloadWholeList extends AsyncTask<String, Void, JSONObject> {
        protected void onPreExecute() {
                progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.VISIBLE);
                super.onPreExecute();
        }
        protected JSONObject doInBackground(String... urls) {
            try {
                URL url;
                Log.d("random", "randomcontact" + randomContactClicked);
                if (randomContactClicked) {
                    onlyOneRandom = false;
                    Log.d("random", "randomcontact" + randomContactClicked);
                    url = new URL(urls[1]);
                } else {
                    url = new URL(urls[0]);
                }

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
                JSONResults results = null;
                if (randomContactClicked) {
                    contact = gson.fromJson(json.toString(), Contact.class);
                    /*
                    lastname = contact.getLastname();
                    firstname = contact.getFirstname();
                    email = contact.getEmail();
                    phone = contact.getPhone();
                    company = contact.getCompany();

                    Log.d("contact: ", "" + contact.getLastname());
                    */
                } else {
                    results = gson.fromJson(json.toString(), JSONResults.class);
                    if (results != null) {
                        contacts = results.getContacts();
                        //sort downloaded list of contacts by lastname
                        Collections.sort(contacts, new Comparator<Contact>() {
                            @Override
                            public int compare(Contact contact2, Contact contact1) {
                                return contact2.getLastname().compareTo(contact1.getLastname());
                            }
                        });
                    }
                }

                return json;
            } catch (IOException e) {
                Log.e(eLogTag, "JSON file could not be read");
            } catch (JSONException e) {
                Log.e(eLogTag, "String could not be converted to JSONObject");
            }
            return null;
        }

        protected void onPostExecute(JSONObject json){
            if (json !=null){
                isDownloadingDone = true;
                progressBar.setVisibility(View.GONE);
                if (wasRefreshed) {
                    switch (currentFragmentDisplayed) {
                        case 1:
                            showContactListFragment();
                            break;
                        case 2:
                            showContactListFragment();
                            break;
                        case 3:
                            showContactListFragment();
                            break;
                        case 0:
                            showContactListFragment();
                            break;
                    }
                    Toast.makeText(getApplicationContext(), "Whole list refreshed", Toast.LENGTH_SHORT).show();
                    wasRefreshed = false;
                } else if (allContactsClicked){
                    isDownloadingListDone = true;
                    firstTimeList = false;
                    firstTime = false;
                    showContactListFragment();
                    Log.d("allcontacts", "all");
                    allContactsClicked = false;
                } else if (randomContactClicked) {
                    firstTime = false;
                    isDownloadingRandomDone = true;
                    randomContactClicked = false;
                    lastname = contact.getLastname();
                    firstname = contact.getFirstname();
                    email = contact.getEmail();
                    phone = contact.getPhone();
                    company = contact.getCompany();
                    onlyOneRandom = false;
                    showRandomContactFragment();
                    onlyOneRandom = true;
                    Log.d("random", "clicked");
                }
            } else {
                firstTime = true;
                firstTimeList = true;
                Context context = getApplicationContext();
                CharSequence text = "Could not successfully download Json, try again";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                isDownloadingDone = false;
                checkInternetConnection();
                allContactsClicked = true;
                randomContactClicked = false;
                if (isConnectedToInternet) {
                    wasRefreshed = true;
                    DownloadList();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (currentFragmentDisplayed == 3){
            showContactListFragment();
            Log.d("debug", "back pressed when 3");
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }

    }

}
