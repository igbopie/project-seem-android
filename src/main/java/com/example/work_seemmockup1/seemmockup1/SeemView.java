package com.example.work_seemmockup1.seemmockup1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SeemView extends ActionBarActivity implements View.OnClickListener{

    //SHARED
    public static int count=0;
    public static List<Reply> repliesDB = new ArrayList<Reply>();
    public static final String EXTRA_REPLY_ID = "replyId";
    public static final String EXTRA_DEPTH = "depth";
    public static ViewGroup.LayoutParams layoutParams;

    Map<ImageView,Reply> images = new HashMap<ImageView,Reply>();
    ImageView image;
    ImageView currentImage;
    Uri lastOutputUri = null;
    String dir;
    GridLayout gridLayout;
    Reply reply;
    int depth = 0;

    final Context context = this;

    int TAKE_PHOTO_CODE = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_seem_view);

        //here,we are making a folder named picFolder to store pics taken by the camera using this application
        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        boolean success = newdir.mkdirs();

        if( !newdir.exists() && !success ){
            Log.d("CameraDemo","Could not create folder...");
        }

        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        image = (ImageView) findViewById(R.id.imageView);
        currentImage = image;

        depth = getIntent().getIntExtra(EXTRA_DEPTH,0);
        this.setTitle("Depth "+depth);

        int idOfReply = getIntent().getIntExtra(EXTRA_REPLY_ID,-1);
        if(idOfReply >=  0){
            reply = repliesDB.get(idOfReply);
            Log.d("CameraDemo","Loading Reply View");
        }


        if(reply == null) {
            currentImage.setOnClickListener(this);
        }else{
            currentImage.setImageBitmap(reply.imageBitmap);
            for(Reply replyReply:reply.replyList){
                currentImage = new ImageView(this);
                currentImage.setImageBitmap(replyReply.imageBitmap);
                currentImage.setOnClickListener(new ClickReplyResolver());
                currentImage.setLayoutParams(layoutParams);
                gridLayout.addView(currentImage,0);

                images.put(currentImage,replyReply);
            }

            currentImage = new ImageView(this);
            currentImage.setImageResource(R.drawable.boton);
            currentImage.setOnClickListener(this);
            currentImage.setLayoutParams(layoutParams);
            gridLayout.addView(currentImage,0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            if(layoutParams == null) {
                int parentWidth = gridLayout.getWidth();
                layoutParams = new LinearLayout.LayoutParams(parentWidth / 4, parentWidth / 4);
            }
            Log.d("CameraDemo", "Pic saved");

            //Save Image
            Reply newReply = new Reply();
            newReply.imageUri = lastOutputUri;
            newReply.imageBitmap = shrinkBitmap(lastOutputUri.getPath());
            repliesDB.add(newReply);

            currentImage.setImageBitmap(newReply.imageBitmap);
            if(currentImage == image) {
                currentImage.setOnClickListener(null);//
            }else {
                currentImage.setOnClickListener(new ClickReplyResolver());//
            }
            images.put(currentImage,newReply);

            if(reply != null){
                reply.replyList.add(newReply);
            }

            //Create new button

            currentImage = new ImageView(this);
            currentImage.setImageResource(R.drawable.boton);
            currentImage.setOnClickListener(this);
            currentImage.setLayoutParams(layoutParams);
            gridLayout.addView(currentImage,0);

            //}

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seem_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    Bitmap shrinkBitmap(String file){

        //500x500

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();

        bmpFactoryOptions.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        boolean width = bmpFactoryOptions.outWidth > bmpFactoryOptions.outHeight;


        int ratio = 0;
        if(width) {
            ratio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) 500);
        } else {
            ratio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) 500);
        }
        bmpFactoryOptions.inSampleSize = ratio;


        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int squareSize = Math.min(bitmap.getHeight(), bitmap.getHeight());

        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,squareSize,squareSize,matrix,true);

        return croppedBmp;
    }

    @Override
    public void onClick(View view) {
        // here,counter will be incremented each time,and the picture taken by camera will be stored as 1.jpg,2.jpg and likewise.
        count++;
        String file = dir+count+".jpg";

        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            Log.d("CameraDemo","Error creating file "+file+" :",e);
        }

        Uri outputFileUri = Uri.fromFile(newfile);
        lastOutputUri = outputFileUri;

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
    }

    class ClickReplyResolver implements View.OnClickListener{


        @Override
        public void onClick(View view) {
            if(view instanceof ImageView) {
                ImageView imageView = (ImageView)view;
                Intent intent = new Intent(context, SeemView.class);
                intent.putExtra(EXTRA_REPLY_ID, repliesDB.indexOf(images.get(imageView)));
                intent.putExtra(EXTRA_DEPTH,depth+1);
                startActivity(intent);
            }

        }
    }
}
