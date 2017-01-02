package com.moviebasket.android.client.network;


import com.moviebasket.android.client.basket_detail.DetailResultParent;
import com.moviebasket.android.client.join.JoinResult;
import com.moviebasket.android.client.login.LoginResult;
import com.moviebasket.android.client.mypage.basket_list.BasketListDataResult;
import com.moviebasket.android.client.mypage.movie_pack_list.BasketListDataDeleteResult;
import com.moviebasket.android.client.mypage.movie_pack_list.PackResultResult;
import com.moviebasket.android.client.mypage.movie_rec_list.HeartResult;
import com.moviebasket.android.client.mypage.movie_rec_list.RecResultParent;
import com.moviebasket.android.client.mypage.setting.SettingResult;
import com.moviebasket.android.client.search.VerifyMovieAddResult;
import com.moviebasket.android.client.splash.VerifyLoginResult;
import com.moviebasket.android.client.splash.VerifyVersionResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by LEECM on 2016-12-28.
 */

public interface MBService {

    //로그인
    @FormUrlEncoded
    @POST("/member")
    Call<LoginResult> getLoginResult(@Field("member_email") String member_email, @Field("member_pwd") String member_pwd);

    //회원가입
    @FormUrlEncoded
    @POST("/member/signUp")
    Call<JoinResult> getJoinResult(@Field("member_name") String member_name, @Field("member_email") String member_email, @Field("member_pwd") String member_pwd);

    //추천한 영화 목록 리스트 가져오기
    @GET("/mypage/movie/recommend")
    Call<RecResultParent> getRecommendResult(@Header("member_token") String member_token );

    ////담은영화 리스트 가져오기
    @GET("/mypage/movie/cart")
    Call<PackResultResult> getMoviePackResult(@Header("member_token") String member_token);

    //바스켓리스트 가져오기 (sort값  1: 관리자 추천순 2: 날짜순 3: 인기순)
    @GET("/basket/")
    Call<BasketListDataResult> getBasketListDataResultOrderBy(@Header("member_token") String member_token, @Query("sort") int sort);

    //로그인 상태 확인하기
    @GET("/member/verify")
    Call<VerifyLoginResult> getVerifyLoginResult(@Header("member_token") String member_token);

    //연결 확인 (Connection)
    @GET("/member/version")
    Call<VerifyVersionResult> getVerifyVersionResult();

    //마이페이지 환경설정
    @GET("/mypage/setting")
    Call<SettingResult> getSettingResult(@Header("member_token") String member_token);

    ////바스켓 상세보기
    @GET("/basket/detail/{basket_id}")
    Call<DetailResultParent> getBasketDetail(@Header("member_token") String member_token, @Path("basket_id") int basket_id);

    //추천하기 및 추천해제하기
    @FormUrlEncoded
    @POST("/basket/movie/recommend")
    Call<HeartResult> getHeartResult(@Field("movie_id") int movie_id, @Field("is_liked") int is_liked, @Header("member_token") String member_token);

    //담은 바스켓리스트 보기
    @GET("/mypage/basket")
    Call<BasketListDataResult> getMyBasketListResult(@Header("member_token") String member_token);

    @FormUrlEncoded
    @POST("/basket/movie/add")
    Call<VerifyMovieAddResult> verifyMovieAddResult(@Header("member_token") String member_token,
                                                    @Field("basket_id") int basket_id,
                                                    @Field("movie_title") String movie_title,
                                                    @Field("movie_image") String movie_image,
                                                    @Field("movie_pub_date") int movie_pub_date,
                                                    @Field("movie_director") String movie_director,
                                                    @Field("movie_user_rating") String movie_user_rating,
                                                    @Field("movie_link") String movie_link);

    @FormUrlEncoded
    @POST("/mypage/movie/cart/delete")
    Call<BasketListDataDeleteResult> getBasketListDataDeleteResult(@Header("member_token") String member_token, @Field("movie_id") int movie_id );



}
