package com.tashi.shaadoow.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.tashi.shaadoow.R;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_GALLERY_VIDEO_ONE = 1;
    private static final int REQUEST_TAKE_GALLERY_VIDEO_TWO = 2;

    private VideoView mVideoViewOne;
    private VideoView mVideoViewTwo;

    public static  String VIDEO_PATH_ONE;
    public static  String VIDEO_PATH_TWO;

    private Button mVideoOneButton;
    private Button mVideoTwoButton;

    private Button mMediaPicker;


    private String mVideoPathOne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        settingUpOnclickListener();


    }


    private void initViews() {

        mVideoViewOne = findViewById(R.id.videoViewOne);
        mVideoViewTwo = findViewById(R.id.videoViewTwo);

        mVideoOneButton = findViewById(R.id.buttonVideoOne);
        mVideoTwoButton = findViewById(R.id.buttonVideoTwo);


        mMediaPicker = findViewById(R.id.button_merge_video_side_by_side);

    }

    private void settingUpOnclickListener() {

        mMediaPicker.setOnClickListener(v -> {


        });

        mVideoOneButton.setOnClickListener(v -> {

//            MediaPickerConfig pickerConfig = new MediaPickerConfig()
//                    .setAllowMultiSelection(true)
//                    .setUriPermanentAccess(true)
//
//                    .setShowConfirmationDialog(true)
//                    .setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//
//            MediaPicker.with(MainActivity.this, MediaPicker.MediaTypes.VIDEO)
//                    .setConfig(pickerConfig)
//                    .setFileMissingListener(() -> {
//                        //trigger when some file are missing
//                    })
//                    .onResult()
//                    .subscribe(new Observer<ArrayList<Uri>>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//
//                        }
//
//                        @Override
//                        public void onNext(ArrayList<Uri> uris) {
//
//
////                            String filePath = Environment.getExternalStorageDirectory() + File.separator +
////                                    "video" + File.separator + "video1.mp4";
////                            mVideoViewOne.setVideoURI(uris.get(0));
//                            mVideoViewOne.setVideoURI(Uri.parse(uris.get(0).getPath()));
//                            Toast.makeText(MainActivity.this, "" + uris.get(0), Toast.LENGTH_SHORT).show();
//                           // mVideoViewOne.start();
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    });


            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO_ONE);


        });


        mVideoTwoButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO_TWO);

        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO_ONE) {
                Uri selectedImageUri = data.getData();


                mVideoViewOne.setVideoURI(selectedImageUri);
                mVideoViewOne.start();
                mVideoViewOne.pause();
                VIDEO_PATH_ONE = getPath(selectedImageUri);

                Toast.makeText(getApplicationContext(), "" + VIDEO_PATH_ONE , Toast.LENGTH_SHORT).show();


//                // OI FILE Manager
//                filemanagerstring = selectedImageUri.getPath();
//
//                // MEDIA GALLERY
//                selectedImagePath = getPath(selectedImageUri);
//                if (selectedImagePath != null) {
//
//                    Intent intent = new Intent(HomeActivity.this,
//                            VideoplayAvtivity.class);
//                    intent.putExtra("path", selectedImagePath);
//                    startActivity(intent);
            }


            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO_TWO) {
                Uri selectedImageUri = data.getData();


                mVideoViewTwo.setVideoURI(selectedImageUri);

                mVideoViewTwo.start();

                VIDEO_PATH_TWO = getPath(selectedImageUri);
                Toast.makeText(getApplicationContext(), "" + VIDEO_PATH_TWO , Toast.LENGTH_SHORT).show();

//
//                // OI FILE Manager
//                filemanagerstring = selectedImageUri.getPath();
//
//                // MEDIA GALLERY
//                selectedImagePath = getPath(selectedImageUri);
//                if (selectedImagePath != null) {
//
//                    Intent intent = new Intent(HomeActivity.this,
//                            VideoplayAvtivity.class);
//                    intent.putExtra("path", selectedImagePath);
//                    startActivity(intent);
            }
        }
    }


    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}


