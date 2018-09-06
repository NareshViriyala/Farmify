package com.example.nareshviriyala.farmifyagentfarmer.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentAgronomics;
import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentBank;
import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentCommerce;
import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentEnvironmental;
import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentIndividual;
import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentDealer;
import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentSocial;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import java.util.ArrayList;
import java.util.List;

public class Delete_MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentIndividual(), "Individual");
        adapter.addFragment(new FragmentBank(), "Bank");
        adapter.addFragment(new FragmentSocial(), "Social");
        adapter.addFragment(new FragmentCommerce(), "E-Commerce");
        adapter.addFragment(new FragmentAgronomics(), "Agronomics");
        adapter.addFragment(new FragmentEnvironmental(), "Environmental");
        adapter.addFragment(new FragmentDealer(), "Satellite");
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
