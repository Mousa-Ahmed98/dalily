package com.example.dalily;

import android.animation.ArgbEvaluator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private ViewPager viewPager;
    private PagerAdapter adapter;
    private List<Model> models;
    private ArgbEvaluator evaluator;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview  = inflater.inflate(R.layout.fragment_home, container, false);
        models = new ArrayList<>();
        models.add(new Model(R.drawable.coffee));
        models.add(new Model(R.drawable.checken));
        models.add(new Model(R.drawable.grocery));
        models.add(new Model(R.drawable.supermarket));
        models.add(new Model(R.drawable.bakery));
        models.add(new Model(R.drawable.pharmacy));
        models.add(new Model(R.drawable.playstation));
        models.add(new Model(R.drawable.meatstore));
        models.add(new Model(R.drawable.gym));
        models.add(new Model(R.drawable.laundry));
        models.add(new Model(R.drawable.clothesstore));

        adapter = new PagerAdapter(models, getContext());
        viewPager = rootview.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);


        return rootview;
    }
}