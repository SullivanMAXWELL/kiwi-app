package com.example.kais.test_project_Api.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.estimote.sdk.SecureRegion;
import com.example.kais.test_project_Api.R;
import com.example.kais.test_project_Api.fragments.BeaconFragment;
import com.example.kais.test_project_Api.fragments.ProfilFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IconTextTabsActivity extends AppCompatActivity {

    long lStartTime;
    long lEndTime;
    long difference;
    boolean firstPressed = false;
    // getter et setter

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String token;


//    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_big_icon_beacon,
            R.drawable.ic_big_icon_user,
    };
    private BeaconFragment beacon = null;
    final SecureRegion ALL_SECURE_ESTIMOTE_BEACONS = new SecureRegion("Le beacon Icy", null, null, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_text_tabs);


        /// Il faut pas toucher a ça
        Intent intent = getIntent();
        String token = intent.getStringExtra("UserToken");
        Bundle bundle = new Bundle();
        bundle.putString("tokenKais",token);
        BeaconFragment bc = new BeaconFragment();
        bc.setArguments(bundle);
       ///////// j'envoie le Token vers le BeaconFragment ici



//        android.app.FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.viewpager, beaconFragment).commit();
//



        ///////////// A ne pas toucher


//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        beacon = new BeaconFragment();
        adapter.addFrag(beacon, "Ma situation");
        adapter.addFrag(new ProfilFragment(), "Mon Profil");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            setToken(token);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed()
    {

        if(firstPressed == false)
        {
            Toast.makeText(this, "Cliquer une Deuxième fois pour quitter !", Toast.LENGTH_SHORT).show();
            firstPressed = true;
            lStartTime = new Date().getTime();
        }
        else
        {
            lEndTime = new Date().getTime();
            difference = lEndTime - lStartTime;
            System.out.println(difference);

            if(difference < 2000)
            {
                super.onBackPressed();
                DestroyEverything();
            }

            else
            {
                firstPressed = false;
                onBackPressed();
            }
        }

    }

    public void DestroyEverything()
    {
        beacon.getBeaconManager().stopMonitoring(ALL_SECURE_ESTIMOTE_BEACONS);
        finish();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());

    }
}
