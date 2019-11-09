package id.ac.ui.cs.mobileprogramming.muhammadfakhruddinhafizh.enotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;

import id.ac.ui.cs.mobileprogramming.muhammadfakhruddinhafizh.enotes.database.NoteDatabase;
import id.ac.ui.cs.mobileprogramming.muhammadfakhruddinhafizh.enotes.models.Note;
import id.ac.ui.cs.mobileprogramming.muhammadfakhruddinhafizh.enotes.services.GpsService;

public class AddNoteActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 123;
    private static final int GPS_REQUEST_CODE = 124;
    private TextInputEditText title, content;
    private ImageView imageView;
    private NoteDatabase noteDatabase;
    private Note note;
    private boolean update;
    private String imagePath, location;
    private BroadcastReceiver broadcastReceiver;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        imageView = findViewById(R.id.imageView);
        noteDatabase = NoteDatabase.getInstance(AddNoteActivity.this);
        locationTextView = findViewById(R.id.location);
        imagePath = null;
        ImageButton addImageBtn = findViewById(R.id.add_img_btn);
        Button saveNoteBtn = findViewById(R.id.save_btn);
        if ( (note = (Note) getIntent().getSerializableExtra("note"))!=null ){
            getSupportActionBar().setTitle(getString(R.string.update));
            update = true;
            title.setText(note.getTitle());
            content.setText(note.getContent());
            locationTextView.setText(getString(R.string.location) + " " +note.getLocation());
            if (note.getImagePath()!=null) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
            }
        }

        if (isGpsPermissionGranted()) {
            getLocation();
        }

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReadStoragePermissionGranted()) {
                    pickFromGallery();
                }
            }
        });

        saveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (update){
                    note.setContent(content.getText().toString());
                    note.setTitle(title.getText().toString());
                    setNoteImage(note, imagePath);
                    note.setLocation(location);
                    noteDatabase.getNoteDao().updateNote(note);
                    Log.v("NOTES IMAGE", note.getImagePath()==null? "null":note.getImagePath());
                    setResult(note,2);
                }else {
                    note = new Note(content.getText().toString(), title.getText().toString(), imagePath, location);
                    new InsertTask(AddNoteActivity.this,note).execute();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Log.v("RESUME", "ON RESUME");
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    location = (String) intent.getExtras().get("coordinates");
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }


    private void setResult(Note note, int flag){
        setResult(flag,new Intent().putExtra("note",note));
        finish();
    }

    private static class InsertTask extends AsyncTask<Void,Void,Boolean> {

        private WeakReference<AddNoteActivity> activityReference;
        private Note note;

        // only retain a weak reference to the activity
        InsertTask(AddNoteActivity context, Note note) {
            activityReference = new WeakReference<>(context);
            this.note = note;
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {
            // retrieve auto incremented note id
            long j = activityReference.get().noteDatabase.getNoteDao().insertNote(note);
            note.setNote_id(j);
            Log.e("ID ", "doInBackground: "+j );
            return true;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool){
                activityReference.get().setResult(note,1);
                activityReference.get().finish();
            }
        }
    }

    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData return the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
                    imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                    imagePath = imgDecodableString;
                    break;
            }

    }

    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("READ STORAGE","Permission is granted1");
                return true;
            } else {

                Log.v("READ STORAGE","Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("READ STORAGE","Permission is granted1");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==3) {
            Log.d("READ STORAGE", "External storage1");
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.v("READ STORAGE","Permission: "+permissions[0]+ "was "+grantResults[0]);
                //resume tasks needing this permission
                pickFromGallery();
            }
        }
        if(requestCode == 124){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }else {
                isGpsPermissionGranted();
            }
        }
    }

    public void setNoteImage(Note note, @Nullable String imagePath) {
        if (imagePath != null) {
            note.setImagePath(imagePath);
        }
        else {
            note.setImagePath(note.getImagePath());
        }
    }

    public boolean isGpsPermissionGranted() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},124);

            return true;
        }
        return false;
    }

    public void getLocation() {
        Intent intent = new Intent(getApplicationContext(), GpsService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }
}