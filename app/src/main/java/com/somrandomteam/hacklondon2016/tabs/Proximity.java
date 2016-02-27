package com.somrandomteam.hacklondon2016.tabs;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.somrandomteam.hacklondon2016.R;

/**
 * Created by abhinavmishra on 27/02/2016.
 */

public class Proximity extends Fragment{

    public Proximity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_proximity, container, false);
    }
}
