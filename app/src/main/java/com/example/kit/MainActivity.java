package com.example.kit;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.litepal.tablemanager.Connector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends Activity {

    private ImageButton mControlIb = null; // 控制按钮
    private boolean mFlag = false; // 控制标记符,默认no状态

    private Context mContext;
    private  boolean isSaved;

    private static final int TAKE_PHOTO=1;
    private static final int PIC=2;
    private static final int PERMISSION_REQ=1;

    public String type = "水果";

    private Button button;
    private Button buttonvis;
    private ImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //进去没权限就弹窗，动态申请
        if (ContextCompat.checkSelfPermission( MainActivity.this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String []{Manifest.permission.CAMERA},PERMISSION_REQ);
        }

        if (ContextCompat.checkSelfPermission( MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQ);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }//权限申请
        setContentView(R.layout.a);
        mContext=this;
        Connector.getDatabase();

        button = (Button) findViewById(R.id.button1);
        buttonvis = (Button)findViewById(R.id.button4);
        view = (ImageView) findViewById(R.id.imageView1);
        mControlIb = (ImageButton) findViewById(R.id.button3);

        mControlIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // 根据记录的控制状态进行图标切换
            if(mFlag) {
                Toast.makeText(MainActivity.this, "yes", Toast.LENGTH_SHORT).show();
                mControlIb.setImageResource(R.drawable.ic_camera);
                mFlag = false;
                buttonvis.setVisibility(View.VISIBLE);
            }else {
                Toast.makeText(MainActivity.this, "no", Toast.LENGTH_SHORT).show();
                mControlIb.setImageResource(R.mipmap.ic_launcher_round);
                mFlag = true;
                buttonvis.setVisibility(View.INVISIBLE);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission( MainActivity.this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, TAKE_PHOTO);
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String []{Manifest.permission.CAMERA},PERMISSION_REQ);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(intent, TAKE_PHOTO);

                }
            }
        });

        buttonvis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://baike.baidu.com/item/"+type);
                final Intent intent2 =new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent2);
            }
        });
    }

        public void gallery (View view){
            // 激活系统图库，选择一张图片
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PIC);
        }
        public void typeset(){
            type = "苹果";
        }

        public void net (View view){
            typeset();
            Uri uri = Uri.parse("https://baike.baidu.com/item/"+type);
            Intent intent =new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == TAKE_PHOTO) {
                new DateFormat();
                String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
                String str = "China"+DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA));
                Toast.makeText(this, name, Toast.LENGTH_LONG).show();
                Bundle bundle = data.getExtras();//容器
                final Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回数据，转换Bitmap

                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("是否保存这张照片到相册？");
                builder.setCancelable(false);
                builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (ContextCompat.checkSelfPermission( MainActivity.this,Manifest.permission.
                                WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                            isSaved = ImgUtils.saveImageToGallery(mContext,bitmap);
                            if (isSaved) {
                                Toast.makeText(mContext, "保存图片成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "保存图片失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            ActivityCompat.requestPermissions(MainActivity.this,new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQ);
                        }
                    }
                });
                builder.setPositiveButton("否", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"图片未保存",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();

                try {
                    view.setImageBitmap(bitmap);// 显示
                } catch (Exception e1) {
                    Log.e("error", e1.getMessage());
                }
                typeset();
                buttonvis.setVisibility(View.VISIBLE);

                History history=new History();
                history.setName(type);
                history.setTime(str);
                if(isSaved)
                    history.setIfSaved("已存储");
                else
                    history.setIfSaved("未存储");

            }//code = 1
            else if (requestCode == PIC) {
                //从相册返回的数据
                if (data != null) {
                    // 图片路径
                    Uri uri = data.getData();
                    ContentResolver cr = this.getContentResolver();
                    String str = "China"+DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA));
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        view.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    typeset();
                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                    buttonvis.setVisibility(View.VISIBLE);

                    History history=new History();
                    history.setName(type);
                    history.setTime(str);
                    history.setIfSaved("来自相册");

                }
            }//code = 2

        }

}


