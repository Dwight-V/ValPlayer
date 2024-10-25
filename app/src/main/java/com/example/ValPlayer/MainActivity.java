package com.example.ValPlayer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvDirectory;

    private MediaPlayer mp;

    private AudioFileAdapter.OnItemClickListener rvDirectoryItemClickListener;

    private static final int STORAGE_PERMISSION_CODE = 23;
    private ActivityResultLauncher<Intent> storageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>(){

                        @Override
                        public void onActivityResult(ActivityResult o) {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                                //Android is 11 (R) or above
                                if(Environment.isExternalStorageManager()){
                                    //Manage External Storage Permissions Granted
                                    Log.d("Broski", "onActivityResult: Manage External Storage Permissions Granted");
                                }else{
                                    Toast.makeText(MainActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                //Below android 11

                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvDirectory = findViewById(R.id.recycleview_directory);

        // https://github.com/codepath/android_guides/wiki/Using-the-RecyclerView#decorations
        rvDirectory.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        // Needs this janky setup so MainActivity.java has access to the clicked AudioFile from within AudioFileAdapter.java.
        // From https://stackoverflow.com/a/49969478
        rvDirectoryItemClickListener = new AudioFileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AudioFile item) {
//                Toast.makeText(MainActivity.this, item.getSong(), Toast.LENGTH_SHORT).show();
                try {
                    mp.stop();
                } catch (Exception e) {

                }

                mp = MediaPlayer.create(getApplicationContext(), ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, item.getId()));
                mp.start();
            }
        };

        // Main
        if (!checkStoragePermissions()) {
            requestForStoragePermissions();
        }

        addAudioFilesToRecycle();
    }

    // Updates both *_current_song.xml files.
    public void setCurrentSong(AudioFile audioFile) {

    }

    // region accessing files
    public void addAudioFilesToRecycle() {
        ArrayList<AudioFile> arrListAudio = new ArrayList<>();

        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = this.getContentResolver();

        // These are the columns of metadata you want.
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA // Path to file
        };

//        String selection = MediaStore.Audio.Media.DATA + "== /storage/emulated/0/Music";

//        String[] selectionArgs = new String[] {
//                values-of-placeholder-variables
//        };
//        String sortOrder = sql-order-by-clause;

        // Execute the query
        try (Cursor cursor = contentResolver.query(
                audioUri,
                projection,
                null,
                null,
                null)) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Retrieve data from the cursor
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

//                    long durInSec = duration / 1000;
//                    String strDur = String.format("%d:%d:%02d", durInSec / 3600, durInSec % 3600 / 60, durInSec % 60);

//                    Log.d("AudioFile", "ID: " + id + " Title: " + title + " Artist: " + artist +
//                            " Album: " + album + " Duration: " + strDur + " Path: " + path);

                    arrListAudio.add(new AudioFile(id, title, artist, album, duration, path));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AudioFileAdapter adapter = new AudioFileAdapter(arrListAudio, rvDirectoryItemClickListener);
        rvDirectory.setAdapter(adapter);
        rvDirectory.setLayoutManager(new LinearLayoutManager(this));
    }
    // endregion


    // region Requesting file permission
    public boolean checkStoragePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11 (R) or above
            return Environment.isExternalStorageManager();
        }else {
            //Below android 11
            int write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);

            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestForStoragePermissions() {
        //Android is 11 (R) or above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            }catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        }else{
            //Below android 11
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    STORAGE_PERMISSION_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0){
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if(read && write){
                    Toast.makeText(MainActivity.this, "Storage Permissions Granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    // endregion

}