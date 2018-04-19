package com.scut.veriface;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scut.veriface.aliface.FaceService;
import com.scut.veriface.baiduface.Identify;

import com.scut.veriface.baiduface.auth.AuthService;
import com.scut.veriface.baiduface.entity.IdentifyResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.scut.veriface.BaiduFaceDetectActivity.dp2px;

public class IdentifyFaceActivity extends AppCompatActivity {

    private  TextView textView;
    private ImageView picture;

    private Bitmap bitmap;
    private String group_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_face);
        picture = (ImageView)  findViewById(R.id.picture);
        textView = (TextView) findViewById(R.id.text_view);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         /* 从 intent中获取 imageUri 或 imagePath ， 然后转换为 bitmap */
        Intent intent = getIntent();
        Uri imageUri = (Uri) intent.getExtras().get("IMAGE_URI");
        String imagePath = (String) intent.getExtras().get("IMAGE_PATH");
        group_id = intent.getStringExtra("GROUP_ID");

        if (imageUri == null && imagePath== null){
            textView.setText("请先放入图片~~");
            Toast.makeText(IdentifyFaceActivity.this,"请先放入图片~~",Toast.LENGTH_LONG).show();
            return;
        }

        if (imageUri !=null){
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (imagePath !=null){
            bitmap=BitmapFactory.decodeFile(imagePath);
        }
        /* 上面代码已拿到bitmap，接下来进行网络请求 */
        new Thread(networkTask).start();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        /* 将原始图片先显示出来 */
        super.onWindowFocusChanged(hasFocus);
        if (bitmap !=null){
            int maxHeight = dp2px(this, 500);
            int height = (int) ((float) picture.getWidth() / bitmap.getWidth() * bitmap.getHeight());
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

    void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                textView.setText(response);
                Toast.makeText(IdentifyFaceActivity.this,response,Toast.LENGTH_LONG).show();
            }
        });
    }


    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            try{
                Log.i("wechat", "原始图片的大小" + (bitmap.getByteCount() / 1024)
                        + "K,宽度为" + bitmap.getWidth() + ",高度为" + bitmap.getHeight());
            /* 加速程序时，注释掉下方内容  ***********************/
//            {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//                String strImg = FaceService.getImageStr(isBm);
//                System.out.println("原始图片图片base64长度:" + strImg.length() / 1024 + "K");
//            }
            /* *************************************************/

            /* 如果照片太大，则将其压缩为 MAX_SIZE K */
                int size = bitmap.getByteCount()/ 1024;
                final int MAX_SIZE = 1800;
                if (size >MAX_SIZE){
                    float scale =  (float) Math.sqrt((float) MAX_SIZE / size);
                    Matrix matrix = new Matrix();
                    matrix.setScale(scale, scale);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);
                    Log.i("wechat", "压缩后图片的大小" + (bitmap.getByteCount() / 1024 )
                            + "K宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight());
                }

            /* 将 bitmap 转为 inputStream */
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                InputStream isBm = new ByteArrayInputStream(baos.toByteArray());

            /* 将图片转为 base64 格式的字符串，并加密后发给`阿里云.人脸识别服务` */
                String strImg = FaceService.getImageStr(isBm); // 将图片转为 base64 格式的字符串
                System.out.println("发送图片base64长度:"+strImg.length()/1024+"K");

                System.out.println("网络请求中......");
                String accessToken =  AuthService.getAuth();
                System.out.println("accessToken : "+accessToken);

                String identifyResultJSON = Identify.identify(accessToken,group_id,strImg);

                Gson gson = new GsonBuilder().create();
                IdentifyResult identifyResult = gson.fromJson(identifyResultJSON, IdentifyResult.class);
                System.out.println("identifyResult : "+ identifyResult);

                String s = "";
                if (identifyResult.result_num == 0){
                    showResponse("没有找到匹配");
                }else{
                    s = "Ta 可能是 :";
                    for (IdentifyResult.Result i : identifyResult.result){
                        System.out.println("IdentifyResult : "+ i);
                            s += "\n"+i.user_info+"\t 相似度 : "+String.format("%.2f",  i.scores[0])+"%";
                    }
                    showResponse(s);
                }
            }catch (Exception e){
                e.printStackTrace();
                showResponse("网络异常");
            }
        }
    };
}
