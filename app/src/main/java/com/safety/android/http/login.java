package com.safety.android.http;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.safety.android.safety.MainActivity;
import com.safety.android.safety.R;
import com.safety.android.safety.SQLite3.UserInfo;
import com.safety.android.safety.SQLite3.UserLab;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class login extends AppCompatActivity {

    final String URL=OKHttpFetch.URL;

    public static String token="";

    private EditText mUsername;
    private EditText mPassWord;
    private CheckBox mCheckbox;

    String username=null;
    String password=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
     //   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
     //   setSupportActionBar(toolbar);

        Button mButton=(Button)findViewById(R.id.mButton);
        mUsername=(EditText)findViewById(R.id.mUserName);
        mPassWord=(EditText)findViewById(R.id.mPassWord);
        mCheckbox =(CheckBox)findViewById(R.id.mCheckBox);

        mCheckbox.setChecked(true);

        readfrominfo();     //调用读内部存储函数

        mPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    username=mUsername.getText().toString();
                    password=mPassWord.getText().toString();

                   new FetchItemsTask().execute();

            }
        });

    }

    //读内部存储函数
    private void readfrominfo(){

            List<UserInfo> list= UserLab.get(getApplication()).getUserInfo();

            try {
                UserInfo userInfo = list.get(0);

                mUsername.setText(userInfo.getName());       //填写username与password
                mPassWord.setText(userInfo.getPassword());

            }catch (Exception e) {
                e.printStackTrace();
            }

    }

    private class FetchItemsTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String  doInBackground(Void... params) {

            OkHttpClient client=OKHttpFetch.getOkHttpClient();

            final MediaType FORM_CONTENT_TYPE
                    = MediaType.parse("application/json;charset=utf-8");

            JSONObject object =new JSONObject();
            try {
                object.put("username",username);
                object.put("password",password);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            System.out.println(object.toString());
            RequestBody body=RequestBody.create(FORM_CONTENT_TYPE,object.toString());

            RequestBody formBody = new FormBody.Builder()
                    .add("username:", username)
                    .add("password", password)
                    .build();

            final Request loginRequest = new Request.Builder()
                    .url(URL + "/jeecg-boot/sys/login")
                    .post(body)
                    .build();


            Call loginCall = client.newCall(loginRequest);

            String s = null;

            try {
                //非异步执行
                Response loginResponse = loginCall.execute();
                //获取返回数据的头部
                Headers headers = loginResponse.headers();
                HttpUrl loginUrl = loginRequest.url();
                //获取头部的Cookie,注意：可以通过Cooke.parseAll()来获取
                List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
                System.out.print("cookies=" + cookies);
                //防止header没有Cookie的情况
                if (cookies != null) {
                    //存储到Cookie管理器中
                    client.cookieJar().saveFromResponse(loginUrl, cookies);//这样就将Cookie存储到缓存中了
                    s=loginResponse.body().string();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return s;

        }

        @Override
        protected void onPostExecute(String items) {
            System.out.println("items="+items);
            try {
                JSONObject jsonObject=new JSONObject(items);

                if(jsonObject!=null){

                    System.out.println("jsonObject="+jsonObject);

                    String message=jsonObject.getString("message");

                    System.out.println("message="+message);

                    String result=jsonObject.getString("result");

                    JSONObject jsonObject2=new JSONObject(result);

                    String token=jsonObject2.getString("token");

                    System.out.println("token="+token);

                    if(message.equals("登录成功")) {

                        if (mCheckbox.isChecked()) {

                            UserInfo userInfo = new UserInfo();

                            userInfo.setName(username);
                            userInfo.setPassword(password);
                            userInfo.setDate(new Date());
                            userInfo.setToken(token);

                            try {
                                int i = UserLab.get(getApplication()).updateUserInfo(userInfo);
                                if (i < 1) {
                                    UserLab.get(getApplicationContext()).addUserInfo(userInfo);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(login.this, "登录成功", Toast.LENGTH_SHORT).show();        //吐司界面，参数依次为提示发出Activity,提示内容,提示时长

                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        startActivity(intent);
                    }else{

                        Toast.makeText(login.this,message,Toast.LENGTH_SHORT).show();

                    }
                }else{

                    Toast.makeText(login.this,"登陆失败",Toast.LENGTH_SHORT).show();        //吐司界面，参数依次为提示发出Activity,提示内容,提示时长

                }

            } catch (JSONException e) {

                Toast.makeText(login.this,"登陆失败",Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }catch (NullPointerException e){

                Toast.makeText(login.this,"登陆失败",Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }


        }

    }



}
