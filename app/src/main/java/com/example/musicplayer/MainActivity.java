package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //  ListView songlist;
      // String[] items;
      private static final int myrequest = 1;


    private Button btn;
    private boolean playPause;
               private MediaPlayer mediaplayer;
                 private Button playbutton;

                  private Button voladd;
                    private Button volless;

                    private SeekBar seekbar;
                     private AudioManager audioManager;
    private ProgressDialog progressDialog;
    private boolean initialStage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

          mediaplayer = new MediaPlayer();

        btn = (Button) findViewById(R.id.stream);

              //mediaplayer = MediaPlayer.create(getApplicationContext(),R.raw.song);
                //  seekbar = (SeekBar) findViewById(R.id.seek);


        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(    this);
                  //audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                        //seekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                      //seekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                  playbutton = (Button)findViewById(R.id.start);
                    voladd = (Button)findViewById(R.id.more);
                       volless = (Button) findViewById(R.id.less);
                      voladd.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {

                              upButton(v);
                          }

                      });
                        volless.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DownButton(v);
                            }
                        });
                       playbutton.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                                  if(mediaplayer.isPlaying())
                                  {
                                      // stop  method
                                         stopmusic();
                                   }
                                    else
                                          startmusic();
                           }
                       });

        // songlist  = (ListView)findViewById(R.id.musicid);

        // runpermission();

     /*   if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions   (MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        myrequest);
            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        myrequest);
            }
        } else {
            dostuff();
        }  */

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playPause) {
                    btn.setText("Pause Streaming");

                    if (initialStage) {
                        new Player().execute("https://www.ssaurel.com/tmp/mymusic.mp3");
                    } else {
                        if (!mediaplayer.isPlaying())
                            mediaplayer.start();
                    }

                    playPause = true;

                } else {
                    btn.setText("Launch Streaming");

                    if (mediaplayer.isPlaying()) {
                        mediaplayer.pause();
                    }

                    playPause = false;
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mediaplayer != null) {
            mediaplayer.reset();
            mediaplayer.release();
            mediaplayer = null;
        }
    }

      class Player extends AsyncTask<String,Void,Boolean>
      {

          @Override
          protected Boolean doInBackground(String... strings) {

                Boolean prepared= false;
                    try
                    {
                          mediaplayer.setDataSource(strings[0]);
                           mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                               @Override
                               public void onCompletion(MediaPlayer mp) {

                                    initialStage = true;
                                     playPause = false;
                                   btn.setText("Launch Streaming");
                                   mp.stop();
                                   mp.reset();
                               }
                           });

                        mediaplayer.prepare();
                        prepared = true;

                    }
                      catch(Exception e)
                      {

                          Log.e("MyAudioStreamingApp", e.getMessage());
                          prepared = false;
                      }

              return prepared;
          }

          @Override
          protected void onPostExecute(Boolean aBoolean) {
              super.onPostExecute(aBoolean);

              if (progressDialog.isShowing()) {
                  progressDialog.cancel();
              }

              mediaplayer.start();
              initialStage = false;
          }

          @Override
          protected void onPreExecute() {
              super.onPreExecute();
              progressDialog.setMessage("Buffering...");
              progressDialog.show();
          }
      }

      public void upButton(View view)
       {
             audioManager.adjustVolume(AudioManager.ADJUST_RAISE,AudioManager.FLAG_PLAY_SOUND);
               seekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                Toast.makeText(this,"Volume up",Toast.LENGTH_SHORT).show();
       }

         public void  DownButton(View view)
         {
              audioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_PLAY_SOUND);
                seekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

                  Toast.makeText(this,"Volume Down",Toast.LENGTH_SHORT).show();
         }

        public void startmusic()
        {

              if(mediaplayer!=null)
                    mediaplayer.start();
                      playbutton.setText("Pause");
        }

          public void stopmusic()
          {
                if(mediaplayer!=null)
                      mediaplayer.stop();
                   playbutton.setText("Play");
          }
  /* public void dostuff()
  {
            listview = (ListView) findViewById(R.id.musicid);
              arrayList = new ArrayList<>();

               getmusic();
                adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
                 listview.setAdapter(adapter);

                   listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                  //
                       }


                   });
  }
      public void getmusic()
      {
          ContentResolver  contentresolver  = getContentResolver();

          Uri   songuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
          Cursor songcursor = contentresolver.query(songuri,null,null,null,null);

             if(songcursor!=null && songcursor.moveToFirst())
             {
                   int songtitle = songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                 int songartist = songcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

                   do {

                         String currenttitle = songcursor.getString(songtitle);
                           String currentartist = songcursor.getString(songartist);
                          arrayList.add(currenttitle + " "+ currentartist);

                   }while(songcursor.moveToNext());

             }

      }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case myrequest:{
                   if(grantResults.length >0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED)
                   {
                       if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                       {
                           Toast.makeText(this,"permission_granted",Toast.LENGTH_SHORT).show();

                            dostuff();
                       }
                        else
                       {
                           Toast.makeText(this,"permission_denied",Toast.LENGTH_SHORT).show();
                            finish();
                       }

                   }
            }
        }
    }

   /*   public void runpermission()
      {
          Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                  .withListener(new PermissionListener() {
                      @Override
                      public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {


                               display();
                      }

                      @Override
                      public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {


                      }

                      @Override
                      public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                         permissionToken.continuePermissionRequest();
                      }
                  }).check();
      }

       public ArrayList<File> findsong(File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();
          File[] files= file.listFiles();

             for(File songfile : files)
             {
                   if(songfile.isDirectory() !=songfile.isHidden())
                   {
                       arrayList.addAll(findsong(songfile));
                   }
                    else
                   {
                        if(songfile.getName().endsWith(".mp3") || songfile.getName().endsWith(".wav"))
                        {

                             arrayList.add(songfile);
                        }
                   }
             }

   return arrayList;
    }

      void display()
      {
             final ArrayList<File> mysong =   findsong(Environment.getExternalStorageDirectory());
                 items = new String[mysong.size()];

                   for(int i=0;i<mysong.size();i++)
                   {
                          items[i] =  mysong.get(i).getName().toString().replace(".mp3","").replace(".wav",
                                  "");

                       ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,items);
                        songlist.setAdapter(arrayAdapter);


                   }
      }

    */


}
