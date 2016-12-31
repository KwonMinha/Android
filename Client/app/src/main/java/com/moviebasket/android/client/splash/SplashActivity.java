package com.moviebasket.android.client.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.moviebasket.android.client.R;
import com.moviebasket.android.client.global.ApplicationController;
import com.moviebasket.android.client.login.LoginActivity;
import com.moviebasket.android.client.main.MainActivity;
import com.moviebasket.android.client.network.MBService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private boolean isConnected;
    private boolean isLogined;

    MBService mbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mbService = ApplicationController.getInstance().getMbService();

        //커넥션 확인
        verifyConnection();
        //로그인 상태 확인
        verifyLoginState();


        Handler verifyState = new Handler(){
            public void handleMessage(Message msg){

                Log.i("NetConfirm", "handleMessage: login:"+isLogined+" , connect:"+isConnected);

                if (isConnected) {
                    if (isLogined) {
                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                        //MainActivity로 이동
                    } else {
                        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                        //LoginActivity로 이동
                    }
                }
            }
        };
        verifyState.sendEmptyMessageDelayed(0, 2300);

    }

    //통신 확인을 하는 부분
    private void verifyConnection(){
        Call<VerifyVersionResult> getVersionResult = mbService.getVerifyVersionResult();
        getVersionResult.enqueue(new Callback<VerifyVersionResult>() {
            @Override
            public void onResponse(Call<VerifyVersionResult> call, Response<VerifyVersionResult> response) {
                VerifyVersionResult result = response.body();
                String version = result.result.version;
                Log.i("NetConfirm", "version: "+version);
                //버전을 가져왔을 때
                if(!version.equals("")){
                    isConnected = true;
                }
            }

            @Override
            public void onFailure(Call<VerifyVersionResult> call, Throwable t) {
                //통신연결에 문제가 생겼을 때
                Log.i("NetConfirm", "verifyConnection isConnectec: "+isConnected);
                Toast.makeText(SplashActivity.this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                isConnected = false;
            }
        });
    }

    //로그인 상태 확인하기.
    private void verifyLoginState(){
        String token = ApplicationController.getInstance().getMember_token();
        //SharedPreferences pref = ApplicationController.getInstance().getSharedPreferences(SecurityDataSet.STR_NAME, MODE_PRIVATE);
        //final String token = pref.getString(SecurityDataSet.TK_KEY, "");
        Log.i("NetConfirm", "verifyConnection token: "+token);

        //토큰 검사
        Call<VerifyLoginResult> getVerifyLoginState = mbService.getVerifyLoginResult(token);
        getVerifyLoginState.enqueue(new Callback<VerifyLoginResult>() {
            @Override
            public void onResponse(Call<VerifyLoginResult> call, Response<VerifyLoginResult> response) {
                VerifyLoginResult result = response.body();
                String message = result.result.message;
                Log.i("NetConfirm", "VerifyLogin Message: "+message);

                if(message.equals("is logined")){
                    isLogined = true;
                }else{
                    isLogined = false;
                }
            }

            @Override
            public void onFailure(Call<VerifyLoginResult> call, Throwable t) {
                Log.i("NetConfirm", "verifyLoginState isLogined: "+isLogined);

                isLogined = false;
            }
        });
    }
}