package com.scut.veriface;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scut.veriface.baiduface.FaceGetList;
import com.scut.veriface.baiduface.FaceGetUsers;
import com.scut.veriface.baiduface.auth.AuthService;
import com.scut.veriface.baiduface.entity.UserList;
import com.scut.veriface.util.CustomProgress;
import com.scut.veriface.util.Person;
import com.scut.veriface.util.PersonAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.scut.veriface.util.MyToast.showToast;


public class ListUserActivity extends AppCompatActivity {
    //TextView textView;
    private List<Person> personList = new ArrayList<>();
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //textView = (TextView) findViewById(R.id.text_view);
        dialog = CustomProgress.show(this,"获取中，请稍等...", true, null);

        new Thread(networkTask).start();
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
                //textView.setText(response);
                dialog.dismiss();
                PersonAdapter adapter = new PersonAdapter(ListUserActivity.this,R.layout.person_item,personList);
                ListView listView = (ListView)findViewById(R.id.list_view);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Person person = personList.get(position);

                        showToast(ListUserActivity.this,
                                "用户班级 ： "+person.getClass_id()+
                                        "\n用户信息 ： "+person.getUser_info()+
                                        "\n用户标识 ： "+person.getUid());

                    }
                });
                showToast(ListUserActivity.this,response);
            }
        });
    }


    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            try{
                System.out.println("网络请求中......");
                String accessToken =  AuthService.getAuth();
                System.out.println("accessToken : "+accessToken);
                String groupListJSON = FaceGetList.getList(accessToken);
                System.out.println("groupListJSON : "+groupListJSON);

                String s = "";
                s +="网络工程班：\n";
                String userListJSON = FaceGetUsers.getUsers(accessToken,"Network_Engineer");
                Gson gson = new GsonBuilder().create();
                UserList userList = gson.fromJson(userListJSON, UserList.class);
                for (UserList.Result i : userList.result){
                    s += i.uid+" - "+ i.user_info+"\n";
                    personList.add(new Person(i.uid,i.user_info,"网络工程",R.drawable.network));
                }

                s +="\n信息安全班：\n";
                userListJSON = FaceGetUsers.getUsers(accessToken,"Information_Safety");
                gson = new GsonBuilder().create();
                userList = gson.fromJson(userListJSON, UserList.class);
                for (UserList.Result i : userList.result){
                    s += i.uid+" - "+ i.user_info+"\n";
                    personList.add(new Person(i.uid,i.user_info,"信息安全",R.drawable.safety));
                }

                showResponse("获取成功");
            }catch (Exception e){
                e.printStackTrace();
                showResponse("网络异常");
            }
        }
    };
}
