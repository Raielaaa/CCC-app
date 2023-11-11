package com.example.ccc_library_app.ui.dashboard.list.selected;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ccc_library_app.R;

public class ClickedBookFragment extends Fragment {

    private ClickedBookViewModel mViewModel;

    public static ClickedBookFragment newInstance() {
        return new ClickedBookFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clicked_book, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ClickedBookViewModel.class);
        // TODO: Use the ViewModel
    }

}