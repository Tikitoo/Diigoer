package com.tikitoo.diigo.app2.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tikitoo.diigo.app2.R;

/**
 * 
 * Created by tikitoo on 1/7/15.
 */
public class AddMarkFragment extends Fragment {

    private Context mCtx;

    public AddMarkFragment() {

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_mark, container, false);
     return rootView;
            
    }



}
