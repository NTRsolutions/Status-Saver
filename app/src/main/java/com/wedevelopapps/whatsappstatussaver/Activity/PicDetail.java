package com.wedevelopapps.whatsappstatussaver.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wedevelopapps.whatsappstatussaver.R;
import com.wedevelopapps.whatsappstatussaver.adapter.CustomSliderAdapter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class PicDetail extends AppCompatActivity {

    FloatingActionButton downloadFab, shareFab;
    Bitmap bmap;
    Uri iri2;
    List<File> imagesList;
    CustomSliderAdapter myCustomPagerAdapter;
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_detail);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        imagesList = new ArrayList<>();
        imagesList = fetchImages();
        int pos = 0;
        String posString = getIntent().getStringExtra("pos").toString();
        try {
            pos = Integer.parseInt(posString);
        } catch (Exception e) {
            Log.d("Error", "not parceable Int");
        }
        downloadFab = findViewById(R.id.DownloadFab);
        shareFab = findViewById(R.id.shareFab);
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        myCustomPagerAdapter = new CustomSliderAdapter(this, imagesList);
        viewPager.setAdapter(myCustomPagerAdapter);
        viewPager.setCurrentItem(pos);

        downloadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImage();
            }
        });


        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage();
            }
        });
    }


    /*


    sharing Images

     */

    private void shareImage() {

        iri2 = Uri.parse(imagesList.get(viewPager.getCurrentItem()).getAbsolutePath());

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/jpeg");
        i.putExtra(Intent.EXTRA_STREAM, iri2);
        startActivity(Intent.createChooser(i, "Share Image Using"));


    }


    /*

    downloading images

     */
    private void downloadImage() {


        File f1, f2;
        f1 = imagesList.get(viewPager.getCurrentItem());
        String fname = f1.getName();
        f2 = new File(Environment.getExternalStorageDirectory() + "/WhatsAppStatus/Images/");
        f2.mkdirs();

        try {
            FileUtils.copyFileToDirectory(f1, f2);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, f2.toString() + "/" + fname);
            getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        }


    }



    List fetchImages(){
        List<File> muList = new ArrayList<File>();
        try {
            String path = Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/.Statuses";
            Log.d("test", "onStart: " + path);
            File dir = new File(path);
            File[] files = dir.listFiles();

            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            for (int i = 0; i < files.length; i++) {

                if(files[i].getName().endsWith(".jpg")||files[i].getName().endsWith(".png")){

                    muList.add(files[i]);

                }

            }



        }catch (Exception ex){
            Toast.makeText(this,ex.getMessage().toString(),Toast.LENGTH_LONG).show();
        }

        return muList;
    }


    @Override
    protected void onStart() {
        super.onStart();
        new MaterialIntroView.Builder(this)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.RIGHT)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText("Hi There! \nClick here to share this picture")
                .setShape(ShapeType.CIRCLE)
                .setTarget(shareFab)
                .setUsageId("intro_card3") //THIS SHOULD BE UNIQUE ID
                .show();
    }
}