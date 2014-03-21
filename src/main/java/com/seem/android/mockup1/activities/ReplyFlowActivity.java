package com.seem.android.mockup1.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.seem.android.mockup1.Api;
import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.customviews.SpinnerImageView;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.util.Utils;

import java.util.Calendar;

/**
 * Created by igbopie on 21/03/14.
 */
public class ReplyFlowActivity extends Activity {

    Item itemInProgress;
    ImageView imageView;
    EditText editText;
    Button submit;
    String replyId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_flow);
        replyId = getIntent().getStringExtra(GlobalVars.EXTRA_ITEM_ID);

        imageView = (ImageView) findViewById(R.id.previewImageView);
        editText = (EditText) findViewById(R.id.captionEditText);
        submit = (Button) findViewById(R.id.seemItButton);

        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit.performClick();
                    return true;
                }
                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemInProgress.setCaption(editText.getText().toString());
                itemInProgress.setCreated(Calendar.getInstance().getTime());
                new UploadMedia().execute(itemInProgress);
            }
        });
        //---
        itemInProgress = new Item();
        itemInProgress.setTempLocalFile(Utils.getNewFileUri());

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, itemInProgress.getTempLocalFile());

        startActivityForResult(cameraIntent, GlobalVars.TAKE_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GlobalVars.TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
            Utils.debug("Pic taken");

            //Controller Logic
            itemInProgress.setTempLocalBitmap(Utils.shrinkBitmap(itemInProgress.getTempLocalFile().getPath()));

            imageView.setImageBitmap(itemInProgress.getTempLocalBitmap());


            //SpinnerImageView iv = addToGrid(itemInProgress);
            //iv.getImageView().setImageBitmap(itemInProgress.getTempLocalBitmap());
            //itemInProgress = null;
        }

    }

    private class UploadMedia extends AsyncTask<Item,Void,Item> {
        private final ProgressDialog dialog = new ProgressDialog(ReplyFlowActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Uploading...");
            dialog.show();
        }

        @Override
        protected Item doInBackground(Item... items) {

            try {
                String mediaId = Api.createMedia(items[0].getTempLocalBitmap());
                if(mediaId != null){
                    items[0].setMediaId(mediaId);
                    Item reply = Api.reply(items[0].getCaption(),items[0].getMediaId(),replyId);
                    AppSingleton.getInstance().saveItem(reply);

                    Item parentReply = AppSingleton.getInstance().findItemById(replyId);
                    parentReply.setReplyCount(parentReply.getReplyCount() + 1);

                    return items[0];
                }else {
                    Utils.debug("Error uploading");
                }
            }catch (Exception e) {
                Utils.debug("Pete al crear la imagen",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Item item) {
            super.onPostExecute(item);
            dialog.dismiss();

            //android:noHistory = "true"
            Intent data = new Intent();

            if (getParent() == null) {
                setResult(Activity.RESULT_OK, data);
            } else {
                getParent().setResult(Activity.RESULT_OK, data);
            }
            finish();
        }
    }

}
