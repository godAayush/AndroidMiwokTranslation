package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class PhrasesFragment extends Fragment {

    MediaPlayer mediaPlayer;
    AudioManager aud;
    WordAdapter adapter;
    AudioManager.OnAudioFocusChangeListener chng = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Log.i("audiofocus","transientloss");
                // Pause playback because your Audio Focus was
                // temporarily stolen, but will be back soon.
                // i.e. for a phone call
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                //currlength = mediaPlayer.getCurrentPosition();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Stop playback, because you lost the Audio Focus.
                // i.e. the user started some other playback app
                // Remember to unregister your controls/buttons here.
                // And release the kra — Audio Focus!
                // You’re done.
                Log.i("audiofocus","focus loss");
                mediaPlayer.stop();
                releaseMediaPlayer();
                aud.abandonAudioFocus(chng);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.i("audiofocus","start");
                //mediaPlayer.seekTo(currlength);
                mediaPlayer.start();
                // Resume playback, because you hold the Audio Focus
                // again!
                // i.e. the phone call ended or the nav directions
                // are finished
                // If you implement ducking and lower the volume, be
                // sure to return it to normal here, as well.
            }
        }
    };
    MediaPlayer.OnCompletionListener mcomplete = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mmediaPlayer) {
            releaseMediaPlayer();
            Log.i("audiofocus","completion");
            aud.abandonAudioFocus(chng);
        }
    };

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mediaPlayer=null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.word_list, container, false);

        ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("Where are you going?", "minto wuksus",R.raw.phrase_where_are_you_going));
        words.add(new Word("What is your name?", "tinnә oyaase'nә",R.raw.phrase_what_is_your_name));
        words.add(new Word("My name is...", "oyaaset...",R.raw.phrase_my_name_is));
        words.add(new Word("How are you feeling?", "michәksәs?",R.raw.phrase_how_are_you_feeling));
        words.add(new Word("I’m feeling good.", "kuchi achit",R.raw.phrase_im_feeling_good));
        words.add(new Word("Are you coming?", "әәnәs'aa?",R.raw.phrase_are_you_coming));
        words.add(new Word("Yes, I’m coming.", "hәә’ әәnәm",R.raw.phrase_yes_im_coming));
        words.add(new Word("I’m coming.", "әәnәm",R.raw.phrase_im_coming));
        words.add(new Word("Let’s go.", "yoowutis",R.raw.phrase_lets_go));
        words.add(new Word("Come here.", "әnni'nem",R.raw.phrase_come_here));

        adapter = new WordAdapter(getActivity() , words, R.color.category_phrases);

        ListView listView = (ListView)rootview.findViewById(R.id.list);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int songid = adapter.getItem(position).getMusicResourceId();
                releaseMediaPlayer();


                aud = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

                int result = aud.requestAudioFocus(chng,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
                if(result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    Log.i("audiofocus","mediaplay.start()");
                    mediaPlayer = MediaPlayer.create(getActivity(),songid);
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(mcomplete);
                }
            }
        });

        return rootview;
    }


}