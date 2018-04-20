package com.scut.veriface;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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

import com.scut.veriface.baiduface.IdentifyMvN;
import com.scut.veriface.baiduface.auth.AuthService;
import com.scut.veriface.baiduface.entity.DetectResult;
import com.scut.veriface.baiduface.entity.IdentifyMvNResult;
import com.scut.veriface.baiduface.entity.IdentifyResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.scut.veriface.BaiduFaceDetectActivity.dp2px;

public class IdentifyFaceActivity extends AppCompatActivity {

    private  TextView textView;
    private  TextView title;
    private ImageView picture;

    private Bitmap bitmap;
    private String group_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_face);
        picture = (ImageView)  findViewById(R.id.picture);
        textView = (TextView) findViewById(R.id.text_view);
        title = (TextView) findViewById(R.id.title);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         /* 从 intent中获取 imageUri 或 imagePath ， 然后转换为 bitmap */
        Intent intent = getIntent();
        Uri imageUri = (Uri) intent.getExtras().get("IMAGE_URI");
        String imagePath = (String) intent.getExtras().get("IMAGE_PATH");
        group_id = intent.getStringExtra("GROUP_ID");


        String titleText = "unknown" ;

        if ("Network_Engineer".equals(group_id)){
            titleText="从网工库识别，Ta们可能是：";
        }else if ("Information_Safety".equals(group_id)){
            titleText="从信安库识别，Ta们可能是：";
        }

        title.setText(titleText);

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
                Toast.makeText(IdentifyFaceActivity.this, "识别状态已更新",Toast.LENGTH_LONG).show();
            }
        });
    }


    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();
            try{
                Log.i("wechat", "原始图片的大小" + (bitmap.getByteCount() / 1024)
                        + "K,宽度为" + bitmap.getWidth() + ",高度为" + bitmap.getHeight());
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

                /* 将图片转为 base64 格式的字符串，并加密后发给`百度云.人脸识别服务` */
                String strImg = FaceService.getImageStr(isBm); // 将图片转为 base64 格式的字符串
                System.out.println("发送图片base64长度:"+strImg.length()/1024+"K");

                System.out.println("网络请求中......");
                String accessToken =  AuthService.getAuth();
                System.out.println("accessToken : "+accessToken);

//                String identifyResultJSON = Identify.identify(accessToken,group_id,strImg);
                String identifyResultJSON = IdentifyMvN.identify(accessToken,group_id,strImg);

                data.putString("identifyResultJSON", identifyResultJSON);
                msg.setData(data);
                handler.sendMessage(msg);


            }catch (Exception e){
                e.printStackTrace();
                showResponse("网络异常");
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            /* 处理 网络请求 之后的结果*/
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String identifyResultJSON = data.getString("identifyResultJSON");
            Log.i("mylog", "请求结果为-->" + identifyResultJSON);

            Gson gson = new GsonBuilder().create();
            IdentifyMvNResult identifyMvNResult = gson.fromJson(identifyResultJSON, IdentifyMvNResult.class);
            System.out.println("identifyMvNResult : "+ identifyMvNResult);

            String s = "";
            if (identifyMvNResult.result_num == 0){
                showResponse("没有找到匹配");
            }else{
                s = "";
                int t=1;
                for (IdentifyMvNResult.Result i : identifyMvNResult.result){
                    System.out.println("IdentifyResult : "+ i);
                    if (t!=10){
                        s += "0"+t+". 相似度  "+String.format("%.2f",  i.scores[0])+"% : "+i.user_info+"\n";
                    }else{
                        s += t+". 相似度  "+String.format("%.2f",  i.scores[0])+"% : "+i.user_info+"\n";
                    }

                    t++;
                }
                showResponse(s);
                drawRectangles(bitmap,identifyMvNResult.result);
            }
        }
    };

    private void drawRectangles(Bitmap imageBitmap, IdentifyMvNResult.Result[] keywordRects) {
        /* 在 bitmap 上面画矩阵 */
        float left, top, right, bottom;
        Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();
        if (keywordRects != null){
            for (int i = 0; i < keywordRects.length; i++) {
                left = (float) keywordRects[i].position.left;
                top =(float) keywordRects[i].position.top;
                right = (float)(left+keywordRects[i].position.width);
                bottom = (float)(top+keywordRects[i].position.height);
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                canvas.drawRect(left, top, right, bottom, paint);
                paint.setTextSize(30);
                paint.setColor(Color.RED);
                canvas.drawText((i+1)+"", left, top, paint);//使用画笔paint
            }
        }

        /* 将画好的图，按照一定缩放比例，展示出来 */
        int maxHeight = dp2px(this, 550);
        int height = (int) ((float) picture.getWidth()/mutableBitmap.getWidth() * mutableBitmap.getHeight());
        if (height > maxHeight) height = maxHeight;
        picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        picture.setImageBitmap(mutableBitmap);
    }

    public static int dp2px(Context context, int dp)
    {
        /* 单位转换 ： dp 转换为 px */
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
