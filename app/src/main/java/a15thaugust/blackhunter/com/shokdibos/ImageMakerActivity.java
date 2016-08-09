package a15thaugust.blackhunter.com.shokdibos;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImageMakerActivity extends AppCompatActivity implements OnClickListener {

    Button browserBtn;
    Button makeThisPhotoBtn;
    ImageView imageView;

    private static final int SELECT_PICTURE = 1;
    private final int CAMERA_REQUEST_CODE = 2222;
    private boolean flag = false;
    private Bitmap image;
    Bitmap mutableBitmap;

    private CallbackManager callbackManager;
    private LoginManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_icon);

        facebookSDKInitialize();
        setContentView(R.layout.activity_image_maker);
        imageView = (ImageView) findViewById(R.id.imageView);
        browserBtn = (Button) findViewById(R.id.browseBtn);
        makeThisPhotoBtn = (Button) findViewById(R.id.makeThisPhotoBtn);
        browserBtn.setOnClickListener(this);
        makeThisPhotoBtn.setOnClickListener(this);
        imageView.setOnClickListener(this);

        MobileAds.initialize(this, "ca-app-pub-3985395576754717~6293075382");

        AdView adView = (AdView) this.findViewById(R.id.adViewPhotMaker);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.browseBtn:
                Toast.makeText(getApplicationContext(), "Select photo less then 4MB", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                break;
            case R.id.makeThisPhotoBtn:

                if(flag){
                    // Bitmap one = BitmapFactory.decodeResource(getResources(),R.drawable.bbb);
                    Bitmap two = BitmapFactory.decodeResource(getResources(), R.drawable.ping);
                    //two.setWidth(two.getWidth());

                    Bitmap mutableChap = two.copy(Bitmap.Config.ARGB_8888, true);

                    Bitmap bitmap = Bitmap.createScaledBitmap(mutableChap, image.getWidth(), image.getHeight() / 3, true);

                    //mutableChap.setWidth(one.getWidth());
                    //mutableChap.setHeight(image.getHeight()/4);
                    int oneHeight = image.getHeight();

// As described by Steve Pomeroy in a previous comment,
// use the canvas to combine them.
// Start with the first in the constructor..
                    mutableBitmap = image.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas comboImage = new Canvas(mutableBitmap);
// Then draw the second on top of that
                    comboImage.drawBitmap(bitmap, 0f, image.getHeight() - bitmap.getHeight(), null);
                    imageView.setImageBitmap(mutableBitmap);

                }

// one is now a composite of the two.
                break;
            case R.id.imageView:
                Toast.makeText(getApplicationContext(), "Select photo less then 4MB", Toast.LENGTH_SHORT).show();
                Intent intentImageView = new Intent();
                intentImageView.setType("image/*");
                intentImageView.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentImageView, "Select Picture"), SELECT_PICTURE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                //selectedImagePath = getPath(selectedImageUri);
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    imageView.setImageBitmap(image);
                    //int imageHeight = image.getHeight();
                    //imageView.setMaxHeight(imageHeight);
                    flag = true;
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (data != null && requestCode == CAMERA_REQUEST_CODE) {
                image = (Bitmap) data.getExtras().get("data");
            }
            else {
                callbackManager.onActivityResult(requestCode,resultCode,data);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {

            case R.id.facebookMenu:
                if(flag){

                    List<String> permissionNeeds = Arrays.asList("publish_actions");

                    //this loginManager helps you eliminate adding a LoginButton to your UI
                    manager = LoginManager.getInstance();
                    manager.logInWithPublishPermissions(this, permissionNeeds);
                    manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            sharePhotoToFacebook();
                        }

                        @Override
                        public void onCancel() {

                            Toast.makeText(getApplicationContext(), "Cancle", Toast.LENGTH_SHORT).show();
                            System.out.println("onCancel");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            System.out.println("onError");
                        }


                    });
                }
                break;
            case R.id.saveImage:
                if(flag){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Save Image");
                    builder.setIcon(R.mipmap.app_icon);
                    final EditText fileNameEt = new EditText(this);
                    fileNameEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(fileNameEt);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(fileNameEt.getText().toString().equals("")){
                                Toast.makeText(getApplicationContext(),"Please enter name",Toast.LENGTH_LONG).show();
                                builder.show();
                                fileNameEt.requestFocus();
                            }
                            else {
                                FileOutputStream os = null;
                                try {
                                    String fileNameTxt = fileNameEt.getText().toString().trim();
                                    os = new FileOutputStream("/sdcard/DCIM/Camera/" + fileNameTxt+".png");
                                    mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                                    Toast.makeText(getApplicationContext(),"saved on sdcard/DCIM/Camera/"+fileNameTxt,Toast.LENGTH_LONG).show();
                                } catch(FileNotFoundException e) {
                                    try {
                                        String fileNameTxt = fileNameEt.getText().toString().trim();
                                        os = new FileOutputStream(Environment.getDataDirectory()+"/DCIM/Camera/"+ fileNameTxt+".png");
                                        mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                                        Toast.makeText(getApplicationContext(),"saved on Phone memory/DCIM/Camera/"+fileNameTxt,Toast.LENGTH_LONG).show();
                                    } catch (Exception e1) {
                                        Toast.makeText(getApplicationContext(),e1.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch (Exception e3){
                                    Toast.makeText(getApplicationContext(),e3.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                    builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sharePhotoToFacebook() {

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(mutableBitmap)
                .setCaption("This is a production of Amazing Leader (Android App)")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);

        Toast.makeText(getApplicationContext(), "Successfully posted", Toast.LENGTH_SHORT).show();
    }

    private void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }
}
