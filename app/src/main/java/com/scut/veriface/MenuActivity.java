package com.scut.veriface;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;

import static com.scut.veriface.BaiduFaceDetectActivity.dp2px;

public class MenuActivity extends AppCompatActivity {
    private ImageView picture;

    private Uri imageUri;
    private String image_Path;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        picture=(ImageView)findViewById(R.id.picture);
        /* 从 intent中获取 imageUri 或 imagePath ， 然后转换为 bitmap */
        Intent intent = getIntent();
        imageUri = (Uri) intent.getExtras().get("IMAGE_URI");
        image_Path = (String) intent.getExtras().get("IMAGE_PATH");

        if (imageUri == null && image_Path== null){
            System.out.println("请先放入图片~~");
            return;
        }

        if (imageUri !=null){
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (image_Path !=null){
            bitmap=BitmapFactory.decodeFile(image_Path);
        }
        /* 上面代码已拿到bitmap*/


        findViewById(R.id.ali_detect_face).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(MenuActivity.this, AliFaceDetectActivity.class);
                intent.putExtra("IMAGE_URI",imageUri);
                intent.putExtra("IMAGE_PATH",image_Path);
                startActivity(intent);
            }
        });

        findViewById(R.id.baidu_detect_face).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(MenuActivity.this, BaiduFaceDetectActivity.class);
                intent.putExtra("IMAGE_URI",imageUri);
                intent.putExtra("IMAGE_PATH",image_Path);
                startActivity(intent);
            }
        });


        findViewById(R.id.identify_face_Network_Engineer).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(MenuActivity.this, IdentifyFaceActivity.class);
                intent.putExtra("IMAGE_URI",imageUri);
                intent.putExtra("IMAGE_PATH",image_Path);
                intent.putExtra("GROUP_ID","Network_Engineer");
                startActivity(intent);
            }
        });
        findViewById(R.id.identify_face_Information_Safety).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(MenuActivity.this, IdentifyFaceActivity.class);
                intent.putExtra("IMAGE_URI",imageUri);
                intent.putExtra("IMAGE_PATH",image_Path);
                intent.putExtra("GROUP_ID","Information_Safety");
                startActivity(intent);
            }
        });
    }
    /***********************************************/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        /* 将原始图片先显示出来 */
        super.onWindowFocusChanged(hasFocus);
        if (bitmap !=null){
            int maxHeight = dp2px(this, 500);
            int width = picture.getWidth();
            int height = (int) ((float) width / bitmap.getWidth() * bitmap.getHeight());
            if (height > maxHeight) height = maxHeight;
            picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
            picture.setImageBitmap(bitmap);
        }

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.list_face:
                Intent intent=new Intent(this, ListUserActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                Intent intent1=new Intent(this, HelpActivity.class);
                startActivity(intent1);
                break;
            default:
        }
        return true;
    }




}
