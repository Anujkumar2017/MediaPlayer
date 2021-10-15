package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    View parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        parentLayout =findViewById(android.R.id.content);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Snackbar.make(parentLayout, "Welcome to Play Music!", Snackbar.LENGTH_LONG).show();
                        ArrayList<File> mysongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String [] items = new String[mysongs.size()];
                        for(int i=0;i<mysongs.size();i++){
                            int k= i+1;
                            items[i] = "("+k+") "+mysongs.get(i).getName().replace(".mp3","");
                        }

                        ArrayAdapter<String>  adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this,Player.class);
                                String currentSong = listView.getItemAtPosition(position).toString();
                                intent.putExtra("songName",currentSong);
                                intent.putExtra("songs",mysongs);
                                intent.putExtra("position",position);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    public ArrayList<File> fetchSongs(File file){
        Log.e("msg","in fetchSongs: "+file.getName().toString());
        ArrayList<File> arrayList= new ArrayList<File>();
        File[] songs= file.listFiles();
        if(songs != null){
            Log.e("msg","in not null");
            for(File myfile:songs){
                if(!myfile.isHidden() && myfile.isDirectory()){
                    arrayList.addAll(fetchSongs(myfile));
                }else{
                    if(myfile.getName().endsWith(".mp3") && !myfile.getName().startsWith(".")){
                        arrayList.add(myfile);
                    }
                }
            }
        }else{
            Log.e("msg","in null");
        }
        return arrayList;
    }
}
