package app.sky.thanhutuaung;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    SeekBar sk;
    boolean continous = true;
    Button foward,playpaus,nextsong,songlist;
    ListView lv;
    String [] musictitle = {"01.Music1","02.Music2"};
    int[] songfile = {R.raw.mc1,R.raw.mc2};
    int currentSong=0;
    boolean playing=false;
    public static MediaPlayer mp;
    Handler handler;
    Runnable runnable;
    int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = new MediaPlayer();

        sk = findViewById(R.id.seekbar);
        foward = findViewById(R.id.preForward);
        playpaus = findViewById(R.id.playpause);
        nextsong = findViewById(R.id.next);
        songlist = findViewById(R.id.songlist);



        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,200);
                sk.setProgress(mp.getCurrentPosition());
            }
        };

        songUpdate();


        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mp.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (playing)
                {
                    mp.pause();
                    handler.removeCallbacks(runnable);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (playing){
                    mp.start();
                    handler.postDelayed(runnable,200);
                }
            }
        });
    }

    public void SongList()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
       final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this,android.R.style.Theme_DeviceDefault_Light_NoActionBar).create();
        View view = inflater.inflate(R.layout.custom_list,null);
        ListView lv = view.findViewById(R.id.lv);
        TextView ti = view.findViewById(R.id.maintitle);
        ti.setText(musictitle[currentSong]);
        //setContentView(view);
        alertDialog.setView(view);
        ArrayAdapter<String > adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,musictitle);
       lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSong = position;
                songUpdate();
                //setContentView(R.layout.activity_main);
              alertDialog.dismiss();
            }
        });

       // AlertDialog dialog = alertDialog.create();
      alertDialog.show();

    }

    private void songUpdate() {
        getSupportActionBar().setTitle(musictitle[currentSong]);
        loadSong();
    }

    private void loadSong() {

        mp.reset();
        mp = MediaPlayer.create(this,songfile[currentSong]);
        ListenToCompletion(mp);
        duration = mp.getDuration();
        sk.setMax(duration);
        sk.setProgress(0);
        mp.start();
        handler.postDelayed(runnable,200);
        playing=true;
        playpaus.setText("Pause");

}



    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.next:
                currentSong++;
                if (currentSong==2){
                    currentSong=0;
                }
                songUpdate();
                break;

            case R.id.preForward:
                currentSong--;
                if (currentSong==-1){
                    currentSong=1;
                }
                songUpdate();
                break;

            case R.id.songlist:
                SongList();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        mp.stop();
        finish();
    }

    public void PlayClick(View view) {
        if (playing){
            playing=false;
            mp.pause();
            playpaus.setText("Play");
            handler.removeCallbacks(runnable);
        }else {
            playing = true;
            mp.start();
            playpaus.setText("Pause");
            handler.postDelayed(runnable,200);

        }
    }
    public void ListenToCompletion(MediaPlayer mp) {
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                if (continous) {
                    currentSong++;
                    if (currentSong == 15) {
                        currentSong = 0;
                    }
                    songUpdate();
                }
            }
        });
    }
}
