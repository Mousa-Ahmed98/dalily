package com.example.dalily;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {

    private List<Model> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public PagerAdapter(List<Model> models, Context context){
        this.models = models;
        this.context = context;
    }
    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview, container, false);

        ImageView imageView = view.findViewById(R.id.image_view);
        imageView.setImageResource(models.get(position).getImage());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToActivity(position);
            }
        });
        container.addView(view, 0);
        return view;
    }

    private void moveToActivity(int position) {
        String name = "";
        Intent intent = new Intent(context, ServicesActivity.class);
        if (position == 0){
            name = "Cafe";
        }
        else if (position == 1){
            name = "Restaurant";
        }

        else if (position == 2){
            name = "Grocery";
        }
        
        else if (position == 3){
            name = "Super market";
        }

        else if (position == 4){
            name = "Bakeries";
        }

        else if (position == 5){
            name = "Pharmacy";
        }

        else if (position == 6){
            name = "Playstation";
        }

        else if (position == 7){
            name = "Meat store";
        }

        else if (position == 8){
            name = "Gym";
        }

        else if (position == 9){
            name = "Laundry";
        }

        else if (position == 10){
            name = "Clothes store";
        }
        
        intent.putExtra("Name", name);
        startActivity(context, intent, null);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

}
