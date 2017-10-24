package com.asociadosmonterrubio.admin.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by joseluissanchezcruz on 10/24/17.
 */

public class ActivityUploadImage extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upload_image);
        downloadImageFromStorage();
    }

    private void downloadImageFromStorage(){
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference oYQvqZhLv3farol = FirebaseStorage.getInstance().getReference().child("imagenes/".concat("-Kwp-oYQvqZhLv3farol"));
        oYQvqZhLv3farol.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            @Override
            public void onSuccess(final byte[] bytes_farol) {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes_farol, 0, bytes_farol.length);


                StorageReference KwoxcBsn7TDZrmxkHTl = FirebaseStorage.getInstance().getReference().child("imagenes/".concat("-KwoxcBsn7TDZrmxkHTl"));
                KwoxcBsn7TDZrmxkHTl.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes_KHTL) {
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes_KHTL, 0, bytes_KHTL.length);
                        Log.d("bitmap", bitmap.toString());
                        Log.d("bitmap1", bitmap1.toString());
                        upLoadImage("-KwoxcBsn7TDZrmxkHTl", bytes_farol);
                        upLoadImage("-Kwp-oYQvqZhLv3farol", bytes_KHTL);
                    }
                });
            }
        });
    }

    private void upLoadImage(String pushId, byte[] bytes){
        StorageReference storageReference = FireBaseQuery.getReferenceForSaveUserImage(pushId);
        storageReference.putBytes(bytes);
    }
}
