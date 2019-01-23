package com.kcar.callcamera;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;


import static android.os.Environment.DIRECTORY_PICTURES;

public class MainActivity extends AppCompatActivity {

    private String mCapturedImageUrl;
    private Uri mCapturedImageURI;
    private ImageView imageView;
    private String imagePath;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            }
        });

    }


    void mRequestPermission(String... permissions){
        boolean gotPermissionsFlag = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
                    gotPermissionsFlag = false;
                    break;
                }
            }
        }

        //hash
        if (gotPermissionsFlag){
            //기능 실행
            callImageChooser();
        } else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder
                        .setTitle("권한 요청")
                        .setMessage("저장용량 접근 권한을 허가해야 해당 기능을 사용할 수 있습니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {

                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                .setData(Uri.parse("package:" + getApplication().getPackageName()));
                                        startActivity(intent);

                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                });

                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();
                // 다이얼로그 보여주기
                alertDialog.show();

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, permissions,0);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //String[] permissions로 허용/거절한 permission들 명이 넘어옴 ex)android.permission.READ_EXTERNAL_STORAGE (== Manifest.permission.READ_EXTERNAL_STORAGE)
        //int[] grantResults로 허용은 0(==PERMISSION_GRANTED), 거부는 -1(==PERMISSION_DENIED, 다시묻지않기 체크해도 동일)로 넘어옴

//        Log.d("dongy," ,"permissionsSize : "+permissions.length);

        if (requestCode == 400) {
            boolean shouldShowRequestPermissionRationaleFlag = false;
            for (String permission : permissions) {
                //다시 묻지 않기를 체크한 경우와 허가 된 경우만 shouldShowRequestPermissionRationale가 false를 리턴
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission) && !(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED))
                if (!(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED))
                {
//                    if(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
//                    shouldShowRequestPermissionRationaleFlag = true;

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("권한 요청");

                    alertDialogBuilder
                            .setMessage("권한 허가해줘야 기능 쓸 수 있어 니마 설정으로 가버렷")
                            .setCancelable(false)
                            .setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                    .setData(Uri.parse("package:" + getApplication().getPackageName()));
                                            startActivity(intent);

                                            // 다이얼로그를 취소한다
                                            dialog.cancel();
                                        }
                                    });

                    // 다이얼로그 생성
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // 다이얼로그 보여주기
                    alertDialog.show();
                }

                return;
//                    break;
//                    }
                }
            }

//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

//            if (shouldShowRequestPermissionRationaleFlag) {
//                //다이어로그로 사용자에게 해당 권한이 필요한 이유에 대해 설명
//                // 한 번 거부한 이후부터 true를 리턴(이 분기를 타게 되는 경우가 무조건 한 번 거부한 이후다.)
//                // 다시 보지 않음 하면 false를 리턴 -> 이 경우만 실행하게 됨!
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//                alertDialogBuilder.setTitle("권한 요청");
//
//                alertDialogBuilder
//                        .setMessage("권한 허가해줘야 기능 쓸 수 있어 니마 설정으로 가버렷")
//                        .setCancelable(false)
//                        .setPositiveButton("확인",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                                                .setData(Uri.parse("package:" + getApplication().getPackageName()));
//                                        startActivity(intent);
//
//                                        // 다이얼로그를 취소한다
//                                        dialog.cancel();
//                                    }
//                                });
//
//                // 다이얼로그 생성
//                AlertDialog alertDialog = alertDialogBuilder.create();
//
//                // 다이얼로그 보여주기
//                alertDialog.show();
//            }

//        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                Log.d("dongy", "onActivityResult");

                // data.getData() - content://com.android.providers.media.documents/document/image:889
                if (data != null && data.getData() != null) {
                    // 선택한 이미지에서 비트맵 생성
//                    InputStream in = null;
//                    try {
//                        in = getContentResolver().openInputStream(data.getData());
                    Uri currImageUri = data.getData();

                    GetFilePathFromDevice getFilePathFromDevice = new GetFilePathFromDevice();
                    imagePath = getFilePathFromDevice.getPath(MainActivity.this, currImageUri);
//                        Bitmap image = BitmapFactory.decodeFile(imagePath);
//
//                        // 이미지를 상황에 맞게 회전시킨다
//                        ExifInterface exif = new ExifInterface(imagePath);
//                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                        image = rotateBitmap(image, exifOrientation);
////                        if(exifOrientation > 0) {
//                        // 변환된 이미지 사용
//                        imageView.setImageBitmap(image);

//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Bitmap img = BitmapFactory.decodeStream(in);
//                    try {
//                        in.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    // 이미지 표시
//                    imageView.setImageBitmap(img);

                } else {
//                    try {
                    // 비트맵 이미지로 가져온다
                    imagePath = mCapturedImageUrl;
//                        Bitmap image = BitmapFactory.decodeFile(imagePath);
//
//                        // 이미지를 상황에 맞게 회전시킨다
//                        ExifInterface exif = new ExifInterface(imagePath);
//                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                        image = rotateBitmap(image, exifOrientation);
////                        if(exifOrientation > 0) {
//                            // 변환된 이미지 사용
//                            imageView.setImageBitmap(image);
////                        }else{
////                            imageView.setImageURI(Uri.fromFile(new File(mCapturedImageUrl)));
////                        }

//                    } catch (Exception e) {
//                        Toast.makeText(this, "오류발생: " + e.getLocalizedMessage(),
//                                Toast.LENGTH_LONG).show();
//                    }

                }

                try {
                    Bitmap image = BitmapFactory.decodeFile(imagePath);
                    // 이미지를 상황에 맞게 회전시킨다
                    ExifInterface exif = new ExifInterface(imagePath);
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    image = rotateBitmap(image, exifOrientation);
//                        if(exifOrientation > 0) {
                    // 변환된 이미지 사용
                    imageView.setImageBitmap(image);
                } catch (Exception e) {
                    Toast.makeText(this, "오류발생: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }

        }

    }

    //https://inducesmile.com/android/how-to-set-camera-image-orientation-with-exif-metadata/
    public Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            //createBitmap.createBitmap - 임의의 행렬에 의해 변환 된, 소스 비트 맵의 ​​부분 집합으로부터 불변의 비트 맵을 돌려줍니다.
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public void callImageChooser() {
        // Create appImage at sdcard
        // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) : /storage/emulated/0/Pictures
        //android 4.4 kitkat(API 10) 이상이면 READ_EXTERNAL_STORAGE 권한이 필요없다. getExternalStoragePublicDirectory 대신 getExternalFilesDir(String), getExternalCacheDir()를 이용 가능
//        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).getAbsolutePath(), "callCameraImage");

        File imageStorageDir = new File(getExternalFilesDir(DIRECTORY_PICTURES).getAbsolutePath(), "callCameraImage");
        if (!imageStorageDir.exists()) {
            // Create appImage at sdcard
            imageStorageDir.mkdirs();
        }

        //Create camera captured image file path and name
        // /storage/emulated/0/Pictures
        File file = new File(imageStorageDir, "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mCapturedImageURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
        } else {
            // file:///storage/emulated/0/Pictures/appImage/IMG_1546592153733.jpg
            mCapturedImageURI = Uri.fromFile(file);
        }

        mCapturedImageUrl = file.getAbsolutePath();

        //Call Basic Camera App - 암시적 인텐트 객체 생성
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");

        // Create file chooser intent
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
        // Set camera intent to file chooser
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

        //암시적 인텐트 쓸 때 해당 인텐트를 받을 수 있는 컴포넌트 존재 유무를 확인하는 방어 코드
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, 200);
        }
    }

    boolean gotPermissions(final String... permissions){
        boolean shouldRequestpermissionFlag = false;
        for (String permission : permissions) {
            Log.d("dongy", permission);
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                shouldRequestpermissionFlag = true;
                break;
            }
        }
        return shouldRequestpermissionFlag;
    }


    boolean checkPermission(final String... permissions) {
        //Activity에서 실행
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        boolean shouldRequestpermissionFlag = false;
        for (String permission : permissions) {
            Log.d("dongy", permission);
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                shouldRequestpermissionFlag = true;
                break;
            }
        }

        if (shouldRequestpermissionFlag) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle("권한 요청")
                    .setMessage("이거 허가해야 이미지 가져오는 기능 쓸 수 있습니다요.")
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {

                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            permissions,
                                            400);
                                }
                            });

            // 다이얼로그 생성
            AlertDialog alertDialog = alertDialogBuilder.create();

            // 다이얼로그 보여주기
            alertDialog.show();

            return false;
        }else{
            return true;
        }

    }


}


