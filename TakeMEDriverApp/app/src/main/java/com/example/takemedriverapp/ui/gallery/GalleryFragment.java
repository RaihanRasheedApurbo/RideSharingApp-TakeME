package com.example.takemedriverapp.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.takemedriverapp.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    ListView listView_ride_history;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);


        listView_ride_history = root.findViewById(R.id.list_ride_history);

        ArrayList<RideHistory> rideHistories = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            rideHistories.add(new RideHistory("Uttara", "Banani", "12/07/2021",
                    "12:10", 220.71));
        }

        RideHistoryAdapter adapter = new RideHistoryAdapter(this.getContext(), R.layout.ride_history_list_items,
                rideHistories);

        listView_ride_history.setAdapter(adapter);





        //System.out.println("snackbar");

        //final TextView textView = root.findViewById(R.id.text_gallery);
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });




        return root;
    }
}