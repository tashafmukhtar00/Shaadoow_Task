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


    public static String VIDEO_PATH_ONE;

    private static final int REQUEST_TAKE_GALLERY_VIDEO_ONE = 1;
    private static final int REQUEST_TAKE_GALLERY_VIDEO_TWO = 2;
    public static String VIDEO_PATH_TWO;
    public String savingPath = "";
    FFtask mFFtask;
    private TextView mVideoViewOne;

    private Button mVideoOneButton;
    private Button mVideoTwoButton;
    private TextView mVideoViewTwo;
    private Button mMergeVideoSideBySide;
    private VideoView mFinalVideoView;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        settingUpOnclickListener();

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


        mMergeVideoSideBySide = findViewById(R.id.button_merge_video_side_by_side);
        mFinalVideoView = findViewById(R.id.videoViewFinal);

        mProgressBar = findViewById(R.id.progressBar);

    }

    private void settingUpOnclickListener() {

        mMergeVideoSideBySide.setOnClickListener(v -> {


            if (VIDEO_PATH_ONE != null && VIDEO_PATH_TWO != null) {


                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "final.mp4");
                try {
                    file.createNewFile();
                    savingPath = file.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // final String[] commands = {"-version"};
                //  String complexCommand[] = {"-y", "-i", "/mnt/m_external_sd/Videos/VID-20161221-WA0000.mp4", "-i", "/mnt/m_external_sd/Videos/Brodha V - Aathma Raama [Music Video]_HD.mp4", "-strict", "experimental", "-filter_complex",
                //         "[0:v]scale=1920x1080,setsar=1:1[v0];[1:v] scale=iw*min(1920/iw\\,1080/ih):ih*min(1920/iw\\,1080/ih), pad=1920:1080:(1920-iw*min(1920/iw\\,1080/ih))/2:(1080-ih*min(1920/iw\\,1080/ih))/2,setsar=1:1[v1];[v0][0:a][v1][1:a] concat=n=2:v=1:a=1",
                //         "-ab", "48000", "-ac", "2", "-ar", "22050", "-s", "1920x1080", "-vcodec", "libx264","-crf","27","-q","4","-preset", "ultrafast", savingPath};

//            String mergeCommand[] = {"-y", "-i", VIDEO_PATH_ONE, "-i", VIDEO_PATH_TWO, "-strict", "experimental", "-filter_complex",
//                    "[0:v]scale=1920x1080,setsar=1:1[v0];[1:v] scale=iw*min(1920/iw\\,1080/ih):ih*min(1920/iw\\,1080/ih), pad=1920:1080:(1920-iw*min(1920/iw\\,1080/ih))/2:(1080-ih*min(1920/iw\\,1080/ih))/2,setsar=1:1[v1];[v0][0:a][v1][1:a] concat=n=2:v=1:a=1",
//                    "-ab", "48000", "-ac", "2", "-ar", "22050", "-s", "1920x1080", "-vcodec", "libx264", "-crf", "27", "-q", "4", "-preset", "ultrafast", savingPath};
//
//
//             String mergeVideoSideBy[] = {"-y", "-ss", "0", "-t", "5", "-i", VIDEO_PATH_ONE, "-ss", "0", "-t", "5", "-i", VIDEO_PATH_TWO, "-i", "-filter_complex", "nullsrc=size=720*720[base];[base][2:v]overlay=1,format=yuv420p[base1];[0:v]setpts=PTS-STARTPTS,scale=345*700[upperleft];[1:v]setpts=PTS-STARTPTS,scale=345*700[upperright];[base1][upperleft]overlay=shortest=1:x=10:y=10[tmp1];[tmp1][upperright]overlay=shortest=1:x=366:y=10", "-c:a", "copy", "-strict", "experimental", "-ss", "0", "-t", "5", "-preset ultrafast", savingPath};
//
//
//            String[] fun = {"ffmpeg", "-y",  "-i", VIDEO_PATH_ONE,  "-i", VIDEO_PATH_TWO , "-filter_complex", "[0:v][1:v]hstack=inputs=2[v]; [0:a][1:a]amerge[a]", "-map", "[v]", "-map", "[a]" ,"-ac", savingPath};


                String[] mergeVideoSideBySide = {"-y", "-i", VIDEO_PATH_ONE, "-i", VIDEO_PATH_TWO, "-filter_complex", "[0:v]scale=480:640,setsar=1[l];[1:v]scale=480:640,setsar=1[r];[l][r]hstack=shortest=1", "-c:v", "libx264", "-crf", "23", "-preset", "veryfast", savingPath};

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

                    Uri uri = Uri.parse(savingPath);
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


