package com.example.eventapp.fragments.reports;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TabsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter adapter;
    public static TabsFragment newInstance() {
        TabsFragment fragment = new TabsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        adapter = new ViewPagerAdapter(getActivity());
        setupViewPager(viewPager);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(adapter.getTitle(position))).attach();

        return view;
    }

    private void setupViewPager(ViewPager2 viewPager) {
        adapter.addFragment(new Reports(), "Users");
        adapter.addFragment(new ReportReviewsOverviewFragment(), "Reviews");
        //adapter.addFragment(new ThirdFragment(), "Third Tab");
        viewPager.setAdapter(adapter);
    }
}
