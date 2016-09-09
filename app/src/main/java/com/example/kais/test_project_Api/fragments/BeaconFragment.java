package com.example.kais.test_project_Api.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.MacAddress;
import com.estimote.sdk.Region;
import com.estimote.sdk.SecureRegion;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.cloud.CloudCallback;
import com.estimote.sdk.cloud.EstimoteCloud;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.exception.EstimoteServerException;
import com.example.kais.test_project_Api.R;
import com.example.kais.test_project_Api.activity.IconTextTabsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BeaconFragment extends Fragment {

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }

    private BeaconManager beaconManager;
    private static final int BACKGROUND_KAI = Color.rgb(155, 186, 160);
    private static final int BACKGROUND_NO_BEACON_IN_RANGE = Color.parseColor("#FFA0A9AC");

    public Boolean getCheckIfUserrefreched() {
        return checkIfUserrefreched;
    }

    public void setCheckIfUserrefreched(Boolean checkIfUserrefreched) {
        this.checkIfUserrefreched = checkIfUserrefreched;
    }

    private Boolean checkIfUserrefreched = false;

    public BeaconFragment() {
        // Required empty public constructor
    }

    public String IsLeft = "";

    public String getIsArrived() {
        return IsArrived;
    }

    public void setIsArrived(String isArrived) {
        IsArrived = isArrived;
    }

    public String IsArrived = "";



    public void setIsLeft(String isLeft) {
        IsLeft = isLeft;
    }
    public String getIsLeft() {
        return IsLeft;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myView = inflater.inflate(R.layout.fragment_one, container, false);


        ProfilFragment profilFragment = new ProfilFragment();



        IconTextTabsActivity iconTextTabsActivity = new IconTextTabsActivity();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SystemRequirementsChecker.checkWithDefaultDialogs(getActivity());
        EstimoteSDK.initialize(getActivity().getApplicationContext(), "kiwi-o5p", "891f978947c33dcc8a189ba3df0cd31f"); // Ici j'ai mis le App Id et le Token Secure UUID
        EstimoteSDK.enableDebugLogging(true);
        final boolean[] timerCheck = {false};
        final SecureRegion ALL_SECURE_ESTIMOTE_BEACONS = new SecureRegion("Le beacon Icy", null, null, null);
        final String[] text = new String[1];
        final RelativeLayout relativeLayout = (RelativeLayout) myView.findViewById(R.id.relativeLayout);
        final TextView recherche = (TextView) myView.findViewById(R.id.recherche);
        final Button imHereButton = (Button) myView.findViewById(R.id.imHereButton);
        final Button imLeavingButton = (Button) myView.findViewById(R.id.imLeavingButton);
        beaconManager = new BeaconManager(getActivity().getApplicationContext());

        ///////// TOKEN
        // Bundle bundle = getArguments().getBundle("tokenKais");
        Bundle b = getActivity().getIntent().getExtras();
        final String token = b.getString("UserToken");
        String UserLogin = b.getString("UserLogin");
        final View view = inflater.inflate(R.layout.fragment_one, container, false);
        //////// TOKEN






        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) myView.findViewById(R.id.swipeRefrechBeaconPage);



        try {
            userAlreadyCheckedIn(token,myView, mSwipeRefreshLayout);
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }




        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                mSwipeRefreshLayout.setRefreshing(true);
                try
                {
                    mSwipeRefreshLayout.setRefreshing(true);
                    userAlreadyCheckedIn(token,myView,mSwipeRefreshLayout);
                    setCheckIfUserrefreched(true);


                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });





        Toast.makeText(getActivity().getApplicationContext(), "Bienvenue sur votre espace personnel !", Toast.LENGTH_LONG).show();

        TextView userLogin = (TextView)  myView.findViewById(R.id.textViewLogin);
        userLogin.setText(UserLogin);

        final Button ImHereButton = (Button) myView.findViewById(R.id.imHereButton);
        final Button ImLeavingButton = (Button) myView.findViewById(R.id.imLeavingButton);


        ImLeavingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(getIsArrived() == "true")
                        userIsLeaving(token,myView);
                    else
                        Toast.makeText(getActivity().getApplicationContext(),"Vous ne vous êtes pas présentés ce matin !",Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ImHereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    userIsHere(token,myView);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 1);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady()
            {
                System.out.println("Le service est prêt !");
                beaconManager.startMonitoring(ALL_SECURE_ESTIMOTE_BEACONS);
                new CountDownTimer(20000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        if(timerCheck[0] == false)
                            ((TextView) myView.findViewById(R.id.textView)).setText("Pas de Beacon détecté dans cette zone");
                        ((TextView) myView.findViewById(R.id.textView)).setGravity(Gravity.CENTER);
                    }
                }.start();

            }
        });

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener()
        {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 1);
                Toast.makeText(getActivity().getApplicationContext(), "Bonjour vous êtes bien à l'ETNA", Toast.LENGTH_SHORT).show();
                timerCheck[0] = true;
                text[0] = "Vous êtes dans une zone beacon autorisée";
                recherche.setText("Détecté");
                relativeLayout.setBackgroundColor(BACKGROUND_KAI);

                ImHereButton.setTextColor(Color.parseColor("#ffffff"));
                ImHereButton.setEnabled(true);

                ImLeavingButton.setTextColor(Color.parseColor("#ffffff"));
                ImLeavingButton.setEnabled(true);


                System.out.println("Beacons list : \n"+beacons);
//                printOut(beacons.toString());

                System.out.println("Bonjour vous êtes bien à l'ETNA ! ");

                if (beacons.isEmpty())
                {
                    // This is where the process enters and i get an empty list everytime
                    System.out.println("LIST IS EMPTY !");
                    Log.d("DEBUG : ", "Region: " + region.toString());
                }

                else
                {
                    EstimoteCloud.getInstance().fetchBeaconDetails(MacAddress.fromString("db23d4ac94bf"), new CloudCallback<BeaconInfo>() {
                        @Override
                        public void success(BeaconInfo beaconInfo)
                        {
                            System.out.println(beaconInfo.toString());
                        }

                        @Override
                        public void failure(EstimoteServerException e) {

                        }
                    });

                }
                ((TextView) myView.findViewById(R.id.textView)).setText(text[0]);

            }

            @Override
            public void onExitedRegion(Region region) {
                text[0] = "Pas de beacon detecté";
                ImLeavingButton.setEnabled(false);
                ImHereButton.setEnabled(false);
                relativeLayout.setBackgroundColor(BACKGROUND_NO_BEACON_IN_RANGE);
                ((TextView) myView.findViewById(R.id.textView)).setText(text[0]);
                recherche.setText("Recherche en cours...");
                Toast.makeText(getActivity().getApplicationContext(), "Vous n'êtes plus dans la zone du Beacon !", Toast.LENGTH_LONG).show();
            }
        });
        return myView;
    }

    private void userIsLeaving(final String token, final View myView) throws IOException, JSONException
    {

        final ProgressBar CheckOutProgressBar = (ProgressBar) myView.findViewById(R.id.CheckOutProgressBar);
        CheckOutProgressBar.setVisibility(myView.VISIBLE);

        RequestQueue RequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://api.kiwi.chapron.io/authorized/to/left";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (!response.isNull("message")) {
                        String message = (String) response.getString("message");
                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                    if (!response.isNull("success")) {
                        String success = (String) response.getString("success");

                        if (success.equals("true")) {
                            ImageView CheckIn = (ImageView) myView.findViewById(R.id.CheckOut);
                            Toast.makeText(getActivity().getApplicationContext(), "Vous pouvez partir ! Au revoir", Toast.LENGTH_LONG).show();
                            CheckIn.setVisibility(View.VISIBLE);
                        }
                    }

                    CheckOutProgressBar.setVisibility(myView.INVISIBLE);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        },
                new  com.android.volley.Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        String bodyError = null;
                        String message = null;

                        if(isNetworkAvailable())
                        {
                            try {
                                bodyError = new String(error.networkResponse.data, "UTF-8");
                                message = trimMessage(bodyError, "message");
                                System.err.println(bodyError);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            CheckOutProgressBar.setVisibility(myView.INVISIBLE);
                            Toast.makeText(getActivity().getApplication(), message.toString(), Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            CheckOutProgressBar.setVisibility(myView.INVISIBLE);
                            Toast.makeText(getActivity().getApplication(), "Pas de connexion internet", Toast.LENGTH_LONG).show();
                        }
                    }
                }


        )

        {
            public Map<String, String> getHeaders() throws AuthFailureError
            {

                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("Content-Type", "application/json");
                MyData.put("Authorization", "Bearer "+token);
                return MyData;
            }
        };
        RequestQueue.add(jsonObjectRequest);




    }


    public String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




    private void userIsHere(final String token, final View myView) throws IOException, JSONException {

        final ProgressBar CheckInProgressBar = (ProgressBar) myView.findViewById(R.id.CheckInProgressBar);
        CheckInProgressBar.setVisibility(myView.VISIBLE);

        RequestQueue RequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://api.kiwi.chapron.io/authorized/to/arrived";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    System.out.println();
                    if(!response.isNull("message"))
                    {
                        String message = (String) response.getString("message");
                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                    if(!response.isNull("success"))
                    {
                        String success = (String) response.getString("success");

                        if (success.equals("true"))
                        {
                            setIsArrived("true");
                            Toast.makeText(getActivity().getApplicationContext(), "Vous êtes arrivé.", Toast.LENGTH_LONG).show();
                            ImageView CheckIn = (ImageView) myView.findViewById(R.id.CheckIn);
                            CheckIn.setVisibility(myView.VISIBLE);
                            CheckInProgressBar.setVisibility(myView.INVISIBLE);
                        }
                    }

                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        },
                new  com.android.volley.Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        String bodyError = null;
                        String message = null;

                        if(isNetworkAvailable())
                        {
                            try {
                                bodyError = new String(error.networkResponse.data, "UTF-8");
                                message = trimMessage(bodyError, "message");

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            CheckInProgressBar.setVisibility(myView.INVISIBLE);
                            Toast.makeText(getActivity().getApplication(), message.toString(), Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            CheckInProgressBar.setVisibility(myView.INVISIBLE);
                            Toast.makeText(getActivity().getApplication(), "Pas de connexion internet", Toast.LENGTH_LONG).show();
                        }




                    }
                }


        )

        {
            public Map<String, String> getHeaders() throws AuthFailureError
            {

                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("Content-Type", "application/json");
                MyData.put("Authorization", "Bearer "+token);
                return MyData;
            }
        };
        RequestQueue.add(jsonObjectRequest);
    }


    private void userAlreadyCheckedIn(final String token, final View myView, final SwipeRefreshLayout mSwipeRefreshLayout) throws IOException, JSONException {

        RequestQueue RequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://api.kiwi.chapron.io/me";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, new com.android.volley.Response.Listener<JSONObject>()
        {

            @Override
            public void onResponse(JSONObject response) {

                try {



                    String FullName = response.getString("fullname");

                    String IsArrived = response.getString("is_arrived");
                    setIsArrived(IsArrived);

                    String IsLeft = response.getString("is_left");
                    setIsLeft(IsLeft);




                    ImageView CheckIn = (ImageView) myView.findViewById(R.id.CheckIn);
                    ImageView CheckOut = (ImageView) myView.findViewById(R.id.CheckOut);

                    if(IsArrived.equals("false"))
                    {
                        CheckIn.setVisibility(View.INVISIBLE);
                    }

                    else
                    {
                        CheckIn.setVisibility(View.VISIBLE);
                    }

                    if(IsLeft.equals("false"))
                    {
                        CheckOut.setVisibility(View.INVISIBLE);
                    }

                    else
                    {
                        CheckOut.setVisibility(View.VISIBLE);
                    }

                    if(getCheckIfUserrefreched() == true)
                        Toast.makeText(getActivity().getApplicationContext(), "votre présence a bien été mise à jour", Toast.LENGTH_LONG).show();

                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                mSwipeRefreshLayout.setRefreshing(false);


            }
        },
                new  com.android.volley.Response.ErrorListener()
                { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getActivity().getApplication(),"Vérifiez votre connexion internet !",Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }


        )

        {
            public Map<String, String> getHeaders() throws AuthFailureError
            {

                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("Authorization", "Bearer "+token);
                return MyData;
            }
        };
        RequestQueue.add(jsonObjectRequest);

    }


}
