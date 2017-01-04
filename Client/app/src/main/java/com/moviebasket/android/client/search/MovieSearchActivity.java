package com.moviebasket.android.client.search;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moviebasket.android.client.R;
import com.moviebasket.android.client.global.ApplicationController;
import com.moviebasket.android.client.network.MBService;
import com.moviebasket.android.client.network.NaverService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieSearchActivity extends AppCompatActivity {



    EditText searchMovieName;
    ImageButton searchMovieBtn;
    ImageView image;

    NaverService naverService;
    MBService mbService;

    RecyclerView recyclerView;
    ArrayList<MoviesDatas> mDatas = new ArrayList<MoviesDatas>();
    LinearLayoutManager mLayoutManager;


    MovieDataResult result;
    ArrayList<MovieDetail> movieDetails;
    private ProgressDialog mProgressDialog;
    String query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);

        naverService = ApplicationController.getInstance().getNaverService();
        mbService = ApplicationController.getInstance().getMbService();

        searchMovieName = (EditText)findViewById(R.id.searchMovieName);
        searchMovieBtn = (ImageButton)findViewById(R.id.searchMovieBtn);

        /**
         * 1. recyclerview 초기화
         */
        recyclerView = (RecyclerView)findViewById(R.id.myRecyclerview);
        //각 item의 크기가 일정할 경우 고정
        recyclerView.setHasFixedSize(true);

        // LayoutManager 초기화
        // layoutManager 설정

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        /**
         * 3. Adapter 생성 후 recyclerview에 지정
         */
        movieDetails = new ArrayList<>();
        final MoviesAdapter adapter = new MoviesAdapter(movieDetails, recylerClickListener);
        recyclerView.setAdapter(adapter);

        mProgressDialog = new ProgressDialog(MovieSearchActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("검색중..");
        mProgressDialog.setIndeterminate(true);

        searchMovieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //네이버 네트워킹 테스트
                mProgressDialog.show();
                if ( searchMovieName.getText().toString() != null && searchMovieName.getText().toString().length() != 0 ) {
                    query = searchMovieName.getText().toString();
                } else {
                    query = "movie";
                }
                /**
                 * 2. recyclerview에 보여줄 data
                 */
                TextView search_korean = (TextView)findViewById(R.id.search_korean);
                ImageView search_nosearch = (ImageView)findViewById(R.id.search_nosearch);
                search_korean.setText("");
                search_nosearch.setImageResource(0);

                Call<MovieDataResult> getMovieData = naverService.getMovieDataResult(query);
                getMovieData.enqueue(new Callback<MovieDataResult>() {
                    @Override
                    public void onResponse(Call<MovieDataResult> call, Response<MovieDataResult> response) {
                        if (response.isSuccessful()) {
                            result = response.body();
                            movieDetails.clear();
                            //태그 제거
                            for(int i=0; i<result.items.size(); i++){
                                MovieDetail detail =
                                        new MovieDetail
                                                (RemoveHTMLTag(result.items.get(i).title),
                                                        result.items.get(i).link,
                                                        result.items.get(i).image,
                                                        result.items.get(i).pubDate,
                                                        RemoveHTMLTag(result.items.get(i).director.replaceAll("[|]", ",")),
                                                        result.items.get(i).actor,
                                                        result.items.get(i).userRating);
                                movieDetails.add(detail);
                            }

                            adapter.notifyDataSetChanged();
                            mProgressDialog.dismiss();

                        }
                    }

                    @Override
                    public void onFailure(Call<MovieDataResult> call, Throwable t) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "서비스 연결을 확인하세요.", Toast.LENGTH_SHORT);
                    }
                });

            }
        });
    }

    private View.OnClickListener recylerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildLayoutPosition(v);

            //서버에 요청해야할 매개변수들
            final String token = ApplicationController.getInstance().getPreferences();
            final int basket_id = getIntent().getExtras().getInt("basket_id");
            final String movie_title = RemoveHTMLTag(movieDetails.get(position).title);
            final String movie_image = movieDetails.get(position).image;
            final int movie_pub_date = Integer.parseInt(movieDetails.get(position).pubDate);
            final String movie_director = RemoveSpecitialCharacter(movieDetails.get(position).director);
            final String movie_user_rating = movieDetails.get(position).userRating;
            final String movie_link = movieDetails.get(position).link;

            AlertDialog.Builder addBuilder = new AlertDialog.Builder(MovieSearchActivity.this);
            addBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            addBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mProgressDialog.show();

                    Call<VerifyMovieAddResult> verifyMovieAddResultCall =
                            mbService.verifyMovieAddResult(
                                    token, basket_id, movie_title, movie_image, movie_pub_date, movie_director, movie_user_rating, movie_link
                            );
                    verifyMovieAddResultCall.enqueue(new Callback<VerifyMovieAddResult>() {
                        @Override
                        public void onResponse(Call<VerifyMovieAddResult> call, Response<VerifyMovieAddResult> response) {
                            //추가 성공했을 때
                            Log.i("NetConfirm", "response : "+response.message());
                            VerifyMovieAddResult result = response.body();
                            if(result==null){
                                Toast.makeText(MovieSearchActivity.this, "null값", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(result.result.message.equals("movie add success")){
                                //Intent data = new Intent();
                                //추가된 영화를 보내준다.
                                // data.putExtra("addedMovie", );
                                // setResult(RESULT_OK, data);
                                mProgressDialog.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            }else if(result.result.message.equals("movie add failed")){
                                Toast.makeText(MovieSearchActivity.this, "이미 추가된 영화입니다.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MovieSearchActivity.this, "바스켓에 영화를 추가하는데 실패했습니다", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<VerifyMovieAddResult> call, Throwable t) {
                            //통신 실패했을 때
                            Toast.makeText(MovieSearchActivity.this, "서버와 연결을 실패하였습니다", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_CANCELED);
                        }
                    });
                }
            });
            addBuilder.setMessage("해당 영화를 바스켓에 추가하시겠습니까?");
            addBuilder.show();


            /*
            Call<VerifyMovieAddResult> verifyMovieAddResultCall =
                    mbService.verifyMovieAddResult(
                            token, basket_id, movie_title, movie_image, movie_pub_date, movie_director, movie_user_rating, movie_link
                    );
            verifyMovieAddResultCall.enqueue(new Callback<VerifyMovieAddResult>() {
                @Override
                public void onResponse(Call<VerifyMovieAddResult> call, Response<VerifyMovieAddResult> response) {
                    //추가 성공했을 때
                    VerifyMovieAddResult result = response.body();
                    if(result.result.message.equals("movie add success")){
                        //Intent data = new Intent();
                        //추가된 영화를 보내준다.
                        // data.putExtra("addedMovie", );
                        // setResult(RESULT_OK, data);
                    }else{
                        Toast.makeText(MovieSearchActivity.this, "바스켓에 영화를 추가하는데 실패했습니다", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<VerifyMovieAddResult> call, Throwable t) {
                    //통신 실패했을 때
                    Toast.makeText(MovieSearchActivity.this, "서버와 연결을 실패하였습니다", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                }
            });
            */
        }
    };

    //태그제거 메서드
    public String RemoveHTMLTag(String changeStr){
        if( changeStr != null && !changeStr.equals("") ) {
            changeStr = changeStr.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
        } else {
            changeStr = "";
        }
        return changeStr;
    }

    //특수문자 제거
    public String RemoveSpecitialCharacter(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str =str.replaceAll(match, " ");
        return str;
    }

}