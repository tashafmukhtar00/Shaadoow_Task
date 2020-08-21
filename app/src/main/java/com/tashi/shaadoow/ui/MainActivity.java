package com.tashi.shaadoow.ui;

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
    private Button mWillowFilterButton;

    private TextView mVideoViewTwo;

    private VideoView mFinalVideoView;


    private ProgressBar mProgressBar;
    private Button mFadeInOutFilterButton;
    private String kalvinVideoPath;
    private String willowVideoPath;
    private String FADE_IN_OUT_VIDEO_PATH;

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

    private void checkFFmpegSupport() {

        if (FFmpeg.getInstance(getApplicationContext()).isSupported()) {
            Toast.makeText(this, "ffmpeg is supported", Toast.LENGTH_SHORT).show();
        } else {
            // ffmpeg is not supported
            Toast.makeText(this, "ffmpeg is not supported", Toast.LENGTH_SHORT).show();

        }
    }


    private void initViews() {

        mVideoViewOne = findViewById(R.id.videoViewOne);
        mVideoViewTwo = findViewById(R.id.videoViewTwo);

        mVideoOneButton = findViewById(R.id.buttonVideoOne);
        mVideoTwoButton = findViewById(R.id.buttonVideoTwo);
        mKalvinFilterButton = findViewById(R.id.buttonKalvinFilter);
        mWillowFilterButton = findViewById(R.id.buttonWillowFilter);
        mFadeInOutFilterButton = findViewById(R.id.buttonFadeInOut);


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

        mFadeInOutFilterButton.setOnClickListener(v -> {

            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "fadeInOutVideo.mp4");
            try {
                file.createNewFile();
                FADE_IN_OUT_VIDEO_PATH = file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (MERGE_VIDEO_PATH != null) {


                final String[] command = new String[]{"-y", "-i", MERGE_VIDEO_PATH, "-acodec", "copy", "-vf", "fade=t=in:st=0:d=1, fade=t=out:st=12:d=2", FADE_IN_OUT_VIDEO_PATH};
                executeMergeVideoCommand(command);
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


        mWillowFilterButton.setOnClickListener(v -> {

            if (MERGE_VIDEO_PATH != null) {


                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "finalWillow.mp4");
                try {
                    file.createNewFile();
                    willowVideoPath = file.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //  String[] mergeVideoSideBySide = {"-y", "-i", VIDEO_PATH_ONE, "-i", VIDEO_PATH_TWO, "-filter_complex", "[0:v]scale=480:640,setsar=1[l];[1:v]scale=480:640,setsar=1[r];[l][r]hstack=shortest=1", "-c:v", "libx264", "-crf", "23", "-preset", "veryfast", savingPath};


                String[] kalvinFilterCommand = {"-y", "-i", MERGE_VIDEO_PATH, "-vf", "curves=preset=lighter", "-c:a", "copy", willowVideoPath};

                executeWillowFilterCommand(kalvinFilterCommand);
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

        } catch (Exception e) {
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

        } catch (Exception e) {
        }


    }


    // for applying kalvin filter
    private void executeWillowFilterCommand(final String[] command) {

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

                    Uri uri = Uri.parse(willowVideoPath);
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

        } catch (Exception e) {
        }


    }

    private void executeFadeInOutCommand(final String[] command) {

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
                    if (FADE_IN_OUT_VIDEO_PATH != null) {
                        Uri uri = Uri.parse(FADE_IN_OUT_VIDEO_PATH);
                        mFinalVideoView.setVideoURI(uri);
                        mFinalVideoView.start();
                    }

                    // Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            });

        } catch (Exception e) {
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


