package com.example.kais.test_project_Api.fragments;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kais.test_project_Api.R;
import com.example.kais.test_project_Api.RoundedImage.Round;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ProfilFragment extends Fragment {

    public ProfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View myView2 = inflater.inflate(R.layout.fragment_two, container, false);

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) myView2.findViewById(R.id.swipeRefrech);

        ImageView imViewAndroid = (ImageView) myView2.findViewById(R.id.imageView4);
        imViewAndroid.setImageBitmap(Round.roundCornerImage(BitmapFactory.decodeResource(getResources(), R.drawable.photo), 10));


        Bundle b = getActivity().getIntent().getExtras();
        final String token = b.getString("UserToken");
        String UserLogin = b.getString("UserLogin");
        TextView Login = (TextView) myView2.findViewById(R.id.Login);
        Login.setText(UserLogin);


        try
        {
            volleyrequest(mSwipeRefreshLayout, myView2, token);
        } catch (IOException e) {
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
                    volleyrequest(mSwipeRefreshLayout, myView2,token);
                    Toast.makeText(getActivity().getApplication(), "Votre statut a été mise à jour", Toast.LENGTH_SHORT).show();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        // Inflate the layout for this fragment
        return myView2;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public String getIsLeft() {
        return IsLeft;
    }

    public void setIsLeft(String isLeft) {
        IsLeft = isLeft;
    }

    private String IsLeft = "";

    public void volleyrequest(final SwipeRefreshLayout mSwipeRefreshLayout, View myView, final String token) throws IOException, JSONException
    {
        final  TextView FullNameText = (TextView) myView.findViewById(R.id.FullName);
        final  TextView StatusText = (TextView) myView.findViewById(R.id.Status);
        final  TextView TelephoneText = (TextView) myView.findViewById(R.id.Telephone);
        final  TextView EmailText = (TextView) myView.findViewById(R.id.Mail);
        final  TextView PromotionText = (TextView) myView.findViewById(R.id.Promotion);

        RequestQueue RequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://api.kiwi.chapron.io/me";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<JSONObject>()
        {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    if(response.has("is_arrived") && response.has("is_left"))
                    {
                        String IsArrived = response.getString("is_arrived");
                        IsLeft = response.getString("is_left");
                        setIsLeft(IsLeft);
                        if(IsArrived.equals("true") && IsLeft.equals("true"))
                        {
                            StatusText.setText("Parti pour la journée");
                            StatusText.setTextColor(Color.GREEN);
                        }



                    }
                    String FullName = response.getString("fullname");
                    FullNameText.setText(FullName);
                    String Phone = response.getString("phone_mobile");
                    TelephoneText.setText(Phone);
                    String Email = response.getString("email");
                    EmailText.setText(Email);
                    JSONObject Promotion = response.getJSONObject("promotion");
                    String PromotionName = Promotion.getString("name");
                    PromotionText.setText(PromotionName);

                    String Status = response.getString("status");
                    if (!(Status.isEmpty())) {
                        if (Status.equals("present") && getIsLeft().equals("false")) {
                            StatusText.setText("Présent");
                            StatusText.setTextColor(Color.GREEN);
                        }

                        else if (Status.equals("waiting")) {
                            StatusText.setText("En attente");
                            StatusText.setTextColor(Color.YELLOW);
                        } else if(Status.equals("late")) {
                            StatusText.setText("Vous étiez en retard");
                            StatusText.setTextColor(Color.YELLOW);
                        }
                        else if(Status.equals("absent")){
                            StatusText.setText("Absent");
                            StatusText.setTextColor(Color.RED);
                        }

                    }
                    else
                    {
                        StatusText.setText("Server error");
                        StatusText.setTextColor(Color.RED);
                    }

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
