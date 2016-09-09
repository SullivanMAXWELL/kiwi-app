package com.example.kais.test_project_Api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.sdk.SystemRequirementsChecker;
import com.example.kais.test_project_Api.activity.IconTextTabsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    private Toolbar toolbar;
    private Button btnIconTextTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        SystemRequirementsChecker.checkWithDefaultDialogs(this);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        btnIconTextTabs = (Button) findViewById(R.id.ConnexionButton);

        btnIconTextTabs.setOnClickListener(this);

        context = getContext();
        setContext(context);
        connectUserButton();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ConnexionButton:
                startActivity(new Intent(MainActivity.this, IconTextTabsActivity.class));
                this.finish();
                break;
        }
    }


    private void userAuthentificate(final String emailEditText, String passwordEditText) throws IOException, JSONException {

       final ProgressBar spinner = (ProgressBar) findViewById(R.id.progress);

        spinner.setVisibility(View.VISIBLE);

        RequestQueue RequestQueue = Volley.newRequestQueue(this);
        String url = "http://api.kiwi.chapron.io/authenticate";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("_username", emailEditText);
        jsonBody.put("_password", passwordEditText);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, url,jsonBody,
        new com.android.volley.Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(getApplicationContext(),"Connexion..",Toast.LENGTH_SHORT).show();
                    if(response.has("token"))
                    {
                        final String token = response.getString("token");
                        Intent intent = new Intent(MainActivity.this, IconTextTabsActivity.class);
                        intent.putExtra("UserToken",token);
                        intent.putExtra("UserLogin",emailEditText);
                        startActivity(intent);
                        spinner.setVisibility(View.INVISIBLE);
                    }

                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        },
        new com.android.volley.Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        spinner.setVisibility(View.INVISIBLE);
                        String warning = "Connexion impossible identifiants incorrects !\nSi le problème persiste contactez le service administratif !";
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Erreur 403");
                        builder.setMessage(warning);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }

                }
        );
        RequestQueue.add(jsonObjectRequest);
    }






    private void connectUserButton()
    {
        Button ConnectButton = (Button) findViewById(R.id.ConnexionButton);
        if (ConnectButton != null)
        {
            ConnectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final EditText emailEditText = (EditText) findViewById(R.id.etEmail);
                    final EditText passwordEditText = (EditText) findViewById(R.id.etPass);
                    final String emailEditTextInsider = emailEditText.getText().toString();
                    final String passwordEditTextInsider = passwordEditText.getText().toString();
                    ConnectivityManager InternetManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(TextUtils.isEmpty(emailEditTextInsider))
                    {
                        emailEditText.setError("Merci d'indiquer votre Login");
                    }
                    else if(TextUtils.isEmpty(passwordEditTextInsider))
                    {
                        passwordEditText.setError("merci d'indiquer votre mot de passse");
                    }
                    else
                    {
                        try {
                            userAuthentificate(emailEditTextInsider,passwordEditTextInsider);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }


}
