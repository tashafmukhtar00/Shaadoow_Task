package com.tashi.shaadoow.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.tashi.shaadoow.R;

import java.io.File;
import java.io.IOException;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFtask;

public class MainActivity extends AppCompatActivity {


    public String VIDEO_PATH_ONE;
    public String MERGE_VIDEO_PATH;
    public String VIDEO_PATH_TWO;
    private static final int REQUEST_TAKE_GALLERY_VIDEO_ONE = 1;
    private static final int REQUEST_TAKE_GALLERY_VIDEO_TWO = 2;


    FFtask mFFtask;
    private TextView mVideoViewOne;

    private Button mVideoOneButton;
    private Button mVideoTwoButton;
    File mfile;
    private Button mMergeVideoSideBySide;
    private Button mKalvinFilterButton;


    private TextView mVideoViewTwo;

    private VideoView mFinalVideoView;


    private ProgressBar mProgressBar;

    private String kalvinVideoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        initViews();
        settingUpOnclickListener();
        getFilePath();

        // checkFFmpegSupport();


    }


    private void initViews() {

        mVideoViewOne = findViewById(R.id.videoViewOne);
        mVideoViewTwo = findViewById(R.id.videoViewTwo);

        mVideoOneButton = findViewById(R.id.buttonVideoOne);
        mVideoTwoButton = findViewById(R.id.buttonVideoTwo);
        mKalvinFilterButton = findViewById(R.id.buttonKalvinFilter);


        mMergeVideoSideBySide = findViewById(R.id.button_merge_video_side_by_side);
        mFinalVideoView = findViewById(R.id.videoViewFinal);

        mProgressBar = findViewById(R.id.progressBar);

    }

    private void settingUpOnclickListener() {

        mMergeVideoSideBySide.setOnClickListener(v -> {
            if (VIDEO_PATH_ONE != null && VIDEO_PATH_TWO != null) {
                String[] mergeVideoSideBySide = {"-y", "-i", VIDEO_PATH_ONE, "-i", VIDEO_PATH_TWO, "-filter_complex", "[0:v]scale=480:640,setsar=1[l];[1:v]scale=480:640,setsar=1[r];[l][r]hstack=shortest=1", "-c:v", "libx264", "-crf", "23", "-preset", "veryfast", MERGE_VIDEO_PATH};
                executeMergeVideoCommand(mergeVideoSideBySide);
            } else {
                Toast.makeText(getApplicationContext(), "Please Select Both Videos , Thanks", Toast.LENGTH_SHORT).show();
            }

        });


        mVideoOneButton.setOnClickListener(v -> {


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

        mKalvinFilterButton.setOnClickListener(v -> {

            if (MERGE_VIDEO_PATH != null) {


                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "finalkalvin.mp4");
                try {
                    file.createNewFile();
                    kalvinVideoPath = file.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //  String[] mergeVideoSideBySide = {"-y", "-i", VIDEO_PATH_ONE, "-i", VIDEO_PATH_TWO, "-filter_complex", "[0:v]scale=480:640,setsar=1[l];[1:v]scale=480:640,setsar=1[r];[l][r]hstack=shortest=1", "-c:v", "libx264", "-crf", "23", "-preset", "veryfast", savingPath};


                String[] kalvinFilterCommand = {"-y", "-i", MERGE_VIDEO_PATH, "-c:v", "libx264", "-c:a", "libfaac", "-filter_complex", "[0:v]eq=1.0:0:1.3:2.4:0.175686275:0.103529412:0.031372549:0.4[outv]", "-map", "[outv]", kalvinVideoPath};

                executeKalvinFilterCommand(kalvinFilterCommand);
            } else {
                Toast.makeText(getApplicationContext(), "Merge video first", Toast.LENGTH_SHORT).show();
            }

        });


    }

    private void getFilePath() {
        mfile = new File(Environment.getExternalStorageDirectory() + File.separator + "final.mp4");
        try {
            mfile.createNewFile();
            MERGE_VIDEO_PATH = mfile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO_ONE) {
                Uri selectedImageUri = data.getData();


                VIDEO_PATH_ONE = getPath(selectedImageUri);
                mVideoViewOne.setText(VIDEO_PATH_ONE);

                Toast.makeText(getApplicationContext(), "" + VIDEO_PATH_ONE, Toast.LENGTH_SHORT).show();


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


                VIDEO_PATH_TWO = getPath(selectedImageUri);
                mVideoViewTwo.setText(VIDEO_PATH_TWO);

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
        String[] projection = {MediaStore.Video.Media.DATA};
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
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

    // for merging two videos side by side
    private void executeMergeVideoCommand(final String[] command) {

        try {
            mFFtask = FFmpeg.getInstance(getApplicationContext()).execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onProgress(String message) {
                    super.onProgress(message);
                }

                @Override
                public void onFailure(String message) {
                    super.onFailure(message);

                    Toast.makeText(MainActivity.this, "failed" + message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);

                    Uri uri = Uri.parse(MERGE_VIDEO_PATH);
                    mFinalVideoView.setVideoURI(uri);
                    mFinalVideoView.start();

                    // Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            });

        } catch (Exception ignored) {
        }


    }


    // for applying kalvin filter
    private void executeKalvinFilterCommand(final String[] command) {

        try {
            mFFtask = FFmpeg.getInstance(getApplicationContext()).execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onProgress(String message) {
                    super.onProgress(message);
                }

                @Override
                public void onFailure(String message) {
                    super.onFailure(message);

                    Toast.makeText(MainActivity.this, "failed" + message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);

                    Uri uri = Uri.parse(kalvinVideoPath);
                    mFinalVideoView.setVideoURI(uri);
                    mFinalVideoView.start();

                    // Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            });

        } catch (Exception ignored) {
        }


    }


//    private void versionFFmpeg() {
//        FFmpeg.getInstance(this).execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
//            @Override
//            public void onSuccess(String message) {
//                Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onProgress(String message) {
//
//            }
//        });
//
//    }
}


