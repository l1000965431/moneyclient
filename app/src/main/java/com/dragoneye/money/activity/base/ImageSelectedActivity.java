package com.dragoneye.money.activity.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.dragoneye.money.R;

import java.io.File;

public class ImageSelectedActivity extends BaseActivity {
    public static final int REQUEST_CODE_SELECT_IN_GALLERY = 0;
    public static final int REQUEST_CODE_FROM_CAMERA = 1;
    public static final int REQUEST_USER_INTERNAL = 2;
    public static final int REQUEST_CROP_IMAGE = 3;

    protected static final String IMAGE_FILE_NAME = "iSelf_faceImage.jpg";
    protected static final String SAVE_BUNDLE_KEY_SELECTED_IMAGE_URI = "SAVE_BUNDLE_KEY_SELECTED_IMAGE_URI";
    protected Uri selectedImageUri;

    protected Handler handler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_BUNDLE_KEY_SELECTED_IMAGE_URI, selectedImageUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        selectedImageUri = savedState.getParcelable(SAVE_BUNDLE_KEY_SELECTED_IMAGE_URI);
    }


    protected void goToPortraitSelect(){
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("选择头像");
        ad.setItems(R.array.image_select_source, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: //  从图库中选择
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, REQUEST_CODE_SELECT_IN_GALLERY);
                        break;
                    case 1: //  拍照
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");

                            File photo = new File(android.os.Environment
                                    .getExternalStorageDirectory(), IMAGE_FILE_NAME);
                            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                            selectedImageUri = Uri.fromFile(photo);
                            startActivityForResult(getImageByCamera, REQUEST_CODE_FROM_CAMERA);
                        } else {
                            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });
        ad.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_FROM_CAMERA:
                onFromCamera(resultCode, data);
                break;
            case REQUEST_CODE_SELECT_IN_GALLERY:
                onSelectInGalleryResult(resultCode, data);
                break;
            case REQUEST_CROP_IMAGE:
                onCorpImageResult(resultCode, data);
                break;
        }

    }

    private void onSelectInGalleryResult(int resultCode, Intent data){
        if( resultCode == RESULT_OK ){
            //tartPhotoZoom(data.getData());
            onSelectedFromGalleryFinish(data.getData());
        }
    }

    protected void onSelectedFromGalleryFinish(Uri uri){

    }

    private void onFromCamera(int resultCode, Intent data){

        if( resultCode == RESULT_OK ){
//            Uri uri = data.getData();
//            if(uri == null) {
//                //use bundle to get data
//                Bundle bundle = data.getExtras();
//                if (bundle != null) {
//                    Bitmap photo = (Bitmap) bundle.get("data"); //get bitmap
//                    uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), photo, null, null));
//                    //mIBPortrait.setImageBitmap(photo);
//
//                }
//
//
//            }
            startPhotoZoom(selectedImageUri);

        }

    }

    protected void onCorpImageResult(int resultCode, Intent data){
    }

    private void startPhotoZoom(final Uri uri){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("start zoom", uri.toString());
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 96);
                intent.putExtra("outputY", 96);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, REQUEST_CROP_IMAGE);
            }
        });

    }
}
