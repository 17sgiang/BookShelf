package edu.temple.bookshelf;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class ControlFragment extends Fragment {


    Button pauseButton, playButton, stopButton;
    TextView nowPlayingTextView;
    SeekBar seekBar;

    public ControlFragment() {
        // Required empty public constructor
    }

    public static ControlFragment newInstance() {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_control, container, false);

        nowPlayingTextView = v.findViewById(R.id.now_playing);
        pauseButton = v.findViewById(R.id.pause_button);
        playButton = v.findViewById(R.id.play_button);
        stopButton = v.findViewById(R.id.stop_button);
        seekBar = v.findViewById(R.id.seek_bar);


        return v;
    }
}