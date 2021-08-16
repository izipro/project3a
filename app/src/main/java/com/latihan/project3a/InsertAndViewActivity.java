package com.latihan.project3a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class InsertAndViewActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE_STORAGE = 100;
    int eventID = 0;
    EditText editFilename, editContent;
    Button btnSimpan;
    String filename = "", tempcatatan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_and_view);

        editFilename = findViewById(R.id.editFilename);
        editContent = findViewById(R.id.editContent);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            getSupportActionBar().setTitle("Ubah Catatan");
            filename = extras.getString("filename");
            editFilename.setText(filename);
            editFilename.setEnabled(false);

        } else {
            getSupportActionBar().setTitle("Tambah Catatan");
        }
        eventID = 1;
        if (Build.VERSION.SDK_INT >=23){
            if(periksaIzinPenyimpanan()){
                bacaFile();
            }
        } else{
            bacaFile();
        }
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnSimpan:
                eventID = 2;
                if (!tempcatatan.equals(editContent.getText().toString())){
                    if (Build.VERSION.SDK_INT >=23){
                        if (periksaIzinPenyimpanan()){
                            tampilkanDialogKonfirmasiPenyimpanan();
                        }else{
                            tampilkanDialogKonfirmasiPenyimpanan();
                        }
                    }else {
                        Toast.makeText(InsertAndViewActivity.this,"tidak ada perubahan yang dilakukan",Toast.LENGTH_SHORT).show();


                    }
                    break;
                }

        }
    }
    public boolean periksaIzinPenyimpanan() {
        if (Build.VERSION.SDK_INT >=23 ) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE);
                return false;
            }
        } else {
            return true;
        }
    }
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantresults){
        super.onRequestPermissionsResult(requestCode,permissions,grantresults);
        switch (requestCode){
            case REQUEST_CODE_STORAGE:
                if (grantresults[0] == PackageManager.PERMISSION_GRANTED){
                    if (eventID == 1){
                        bacaFile();
                    } else {
                        tampilkanDialogKonfirmasiPenyimpanan();
                    }
                }
                break;
        }
    }
    public void bacaFile(){
        String path = getExternalFilesDir(null)+"/catatan";
        File file = new File(path, editFilename.getText().toString());
        if (file.exists()){
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                while (line != null){
                    text.append(line);
                    line = br.readLine();
                }
                br.close();
            } catch (IOException e){
                System.out.println("error" + e.getMessage());
            }
            tempcatatan = text.toString();
            editContent.setText(text.toString());

        }

    }
    void buatDanUbah(){
        String state = Environment.getExternalStorageState();
        if(!Environment.MEDIA_MOUNTED.equals(state)){
            return;
        }
        String path = getExternalFilesDir(null)+"/catatan";
        File parent = new File(path);
        if (parent.exists()){
            File file = new File(path,editFilename.getText().toString());
            FileOutputStream outputStream = null;
            try{
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                streamWriter.append(editContent.getText());
                streamWriter.flush();
                streamWriter.close();
                streamWriter.flush();
                streamWriter.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            try {
                Log.i("Path","CREATE DIR");
                parent.mkdir();
                File file = new File(path, editFilename.getText().toString());
                FileOutputStream outputStream = null;
                file.createNewFile();
                outputStream = new FileOutputStream(file, false);
                outputStream.write(editContent.getText().toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        this.finish();
    }
    void tampilkanDialogKonfirmasiPenyimpanan(){
        new AlertDialog.Builder(this)
                .setTitle("Simpan Catatan")
                .setMessage("Apakah anda yakin ingin menyimpan catatan ini? ")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        buatDanUbah();
                    }
                }).setNegativeButton(android.R.string.no,null).show();
    }
    @Override
    public void onBackPressed(){
        if (!tempcatatan.equals(editContent.getText().toString())){
            tampilkanDialogKonfirmasiPenyimpanan();
        }
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}