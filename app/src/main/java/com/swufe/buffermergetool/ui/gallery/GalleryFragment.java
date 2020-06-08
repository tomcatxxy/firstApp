package com.swufe.buffermergetool.ui.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.swufe.buffermergetool.DBHelper;
import com.swufe.buffermergetool.DataItem;
import com.swufe.buffermergetool.DataManager;
import com.swufe.buffermergetool.R;
import com.swufe.buffermergetool.ui.home.HomePageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private static final String TAG="GalleryFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        ViewPager viewPager = root.findViewById(R.id.GalleryViewPager);
        GalleryPageAdapter pageAdapter = new GalleryPageAdapter(getChildFragmentManager());
        viewPager.setAdapter(pageAdapter);

        TabLayout tabLayout = root.findViewById(R.id.sliding_tabs_gallery);
        tabLayout.setupWithViewPager(viewPager);

        Log.i(TAG,"onCreateView:done");

        return root;
    }
}