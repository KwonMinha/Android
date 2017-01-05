package com.moviebasket.android.client.global;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.moviebasket.android.client.network.MBService;
import com.moviebasket.android.client.network.NaverService;
import com.moviebasket.android.client.security.SecurityDataSet;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by LEECM on 2016-12-26.
 */

public class ApplicationController extends Application {

    private static final String naverURL = "https://openapi.naver.com/v1/search/";
    private static final String MovieBasketURL = SecurityDataSet.MBServerUrl;
    private static ApplicationController instance;
    private static final String AppVersion = "0.1"; //버전정보
    private ArrayList<Activity> activities;

    private MBService mbService;
    private NaverService naverService;

    public ApplicationController() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = new ApplicationController();
        }
        instance = this;

        //NaverService Build
        buildNaverService();
        //MBService Build
        buildMBService();
        //Activity List init
        activities = new ArrayList<>();
    }

    /**
     * Getter & Setter
     */
    public static ApplicationController getInstance() {
        return instance;
    }

    public String getVersion() { return AppVersion;}

    public MBService getMbService() {
        return mbService;
    }

    public NaverService getNaverService() {
        return naverService;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    /**
     * methods
     */
    private void buildNaverService() {
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder
                .baseUrl(naverURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        naverService = retrofit.create(NaverService.class);
    }

    private void buildMBService() {
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder
                .baseUrl(MovieBasketURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mbService = retrofit.create(MBService.class);
    }

    // 토큰값 가져오기
    public String getPreferences() {
        SharedPreferences pref = getSharedPreferences(SecurityDataSet.STR_NAME, MODE_PRIVATE);
        String Token = pref.getString(SecurityDataSet.TK_KEY, "");
        return Token;
    }

    // 토큰값 저장하기
    public void savePreferences(String Token) {
        SharedPreferences pref = getSharedPreferences(SecurityDataSet.STR_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SecurityDataSet.TK_KEY, Token);
        editor.commit();
    }

    //모든 액티비티 삭제 후 액티비티리스트 클리어
    public void clearAndFinishAllActivities(){
        Iterator it = this.activities.iterator();
        while(it.hasNext()){
            Activity targetActivity = (Activity)it.next();
            targetActivity.finish();
        }
        activities.clear();
    }

}