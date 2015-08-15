package com.dragoneye.wjjt.activity.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.tool.ToolMaster;

import java.io.File;

public class ImageSelectedActivity extends ProgressActionBarActivity {
    public static final int REQUEST_CODE_SELECT_IN_GALLERY_KITKAT_LESS = 0;
    public static final int REQUEST_CODE_FROM_CAMERA = 1;
    public static final int REQUEST_USER_INTERNAL = 2;
    public static final int REQUEST_CROP_IMAGE = 3;
    public static final int REQUEST_CODE_SELECT_IN_GALLERY_KITKAT_ABOVE = 5;

    protected static final String IMAGE_FILE_NAME = "iSelf_faceImage.jpg";
    protected static final String SAVE_BUNDLE_KEY_SELECTED_IMAGE_URI = "SAVE_BUNDLE_KEY_SELECTED_IMAGE_URI";
    protected Uri selectedImageUri;

    protected Handler handler;

    private boolean isNeedCropImage = false;
    private int cropAspectX = 1;
    private int cropAspectY = 1;
    private int cropWidth = 96;
    private int cropHeight = 96;

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
                        goToGallerySelect();
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

    protected void goToGallerySelect(){
        if(Build.VERSION.SDK_INT < 19){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_SELECT_IN_GALLERY_KITKAT_LESS);
        }else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_SELECT_IN_GALLERY_KITKAT_ABOVE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_FROM_CAMERA:
                onFromCamera(resultCode, data);
                break;
            case REQUEST_CODE_SELECT_IN_GALLERY_KITKAT_LESS:
                onSelectInGalleryResult(resultCode, data);
                break;
            case REQUEST_CROP_IMAGE:
                onCorpImageResult(resultCode, data);
                break;
            case REQUEST_CODE_SELECT_IN_GALLERY_KITKAT_ABOVE:
                onSelectInGalleryResultLOLLI(resultCode, data);
                break;
        }

    }

    private void onSelectInGalleryResultLOLLI(int resultCode, Intent data){
        if( resultCode == RESULT_OK ){
            if( isNeedCropImage() ){
                String path = ToolMaster.getRealPathFromURI(this, data.getData());
                startPhotoZoom(Uri.fromFile(new File(path)));
            }else {
                onSelectedFromGalleryFinish(data.getData());
            }
        }
    }

    private void onSelectInGalleryResult(int resultCode, Intent data){
        if( resultCode == RESULT_OK ){
            if( isNeedCropImage() ){
                startPhotoZoom(data.getData());
            }else {
                onSelectedFromGalleryFinish(data.getData());
            }
        }
    }

    protected void onSelectedFromGalleryFinish(Uri uri){

    }

    protected void onSelectedFromCameraFinish(Uri uri){

    }

    protected void onSelectedCropFinish(Bitmap bitmap, File file){

    }

    protected void onCorpImageResult(int resultCode, Intent data){
        if( resultCode == RESULT_OK ){
            Bundle extras = data.getExtras();
            if(extras != null){
                Bitmap bitmap = extras.getParcelable("data");
                String path = getCacheDir() + "tempCropImage.jpg";
                File file = ToolMaster.SavePicInLocal(bitmap, path);
                onSelectedCropFinish(bitmap, file);
            }
        }
    }

    private void onFromCamera(int resultCode, Intent data){

        if( resultCode == RESULT_OK ){
            if( isNeedCropImage() ){
                startPhotoZoom(selectedImageUri);
            }else {
                onSelectedFromCameraFinish(selectedImageUri);
            }
        }

    }



    private void startPhotoZoom(final Uri uri){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("start zoom", uri.toString());
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");;
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", cropAspectX);
                intent.putExtra("aspectY", cropAspectY);
                intent.putExtra("outputX", cropWidth);
                intent.putExtra("outputY", cropHeight);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, REQUEST_CROP_IMAGE);
            }
        });

    }

    public boolean isNeedCropImage() {
        return isNeedCropImage;
    }

    public void setIsNeedCropImage(boolean isNeedCropImage) {
        this.isNeedCropImage = isNeedCropImage;
    }

    public int getCropAspectX() {
        return cropAspectX;
    }

    public void setCropAspectX(int cropAspectX) {
        this.cropAspectX = cropAspectX;
    }

    public int getCropAspectY() {
        return cropAspectY;
    }

    public void setCropAspectY(int cropAspectY) {
        this.cropAspectY = cropAspectY;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }
}
