package com.example.redditapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.redditapp.Model.Entry.Entry;
import com.example.redditapp.Model.ExtractXML;
import com.example.redditapp.Model.Feed;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String Base_URL = "https://old.reddit.com/r/";

    private Button btnrefresh;
    private EditText subName;
    private String currentSub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnrefresh = (Button) findViewById(R.id.refBut);
        subName = (EditText) findViewById(R.id.feedName);

init();

btnrefresh.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String feedname = subName.getText().toString();
        if(!feedname.equals(""))
        {
            currentSub = feedname;
init();
        }
        else
            {
            init();
        }
    }
});
    }

    private void init()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Base_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed(currentSub);

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                //Log.d(TAG, "onResponse: feed" + response.body().toString());
                Log.d(TAG, "onResponse: serverResponse" + response.toString());

                List<Entry> entrys = response.body().getEntrys();
                Log.d(TAG, "onResponse: entrys" + response.body().getEntrys());
                //    Log.d(TAG, "onResponse: updated:" + entrys.get(0).getUpdated());
                //   Log.d(TAG, "onResponse: author:" + entrys.get(0).getAuthor().getName());
                //  Log.d(TAG, "onResponse: title:" + entrys.get(0).getTitle());


                ArrayList<Post> posts = new ArrayList<Post>();
                for (int i = 0; i < entrys.size(); i++) {
                    ExtractXML extractXML1 = new ExtractXML(entrys.get(i).getContent(), "<a href=");
                    List<String> postContent = extractXML1.start();

                    ExtractXML extractXML2 = new ExtractXML(entrys.get(i).getContent(), "<img src=");

                    try {
                        postContent.add(extractXML2.start().get(0));
                    }
                    catch(NullPointerException e)
                    {
                        postContent.add(null);
                        Log.e(TAG, "onResponse: NullPointerException(thumbnail):" + e.getMessage());
                    }
                    catch(IndexOutOfBoundsException e)
                    {
                        postContent.add(null);
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException(thumbnail):" + e.getMessage());

                    }
                    int lastPos = postContent.size() -1;
                    try {
                        posts.add(new Post(
                                entrys.get(i).getTitle(),
                                entrys.get(i).getAuthor().getName(),
                                entrys.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPos)


                        ));
                    }
                    catch (NullPointerException e) {
                        posts.add(new Post(
                                entrys.get(i).getTitle(),
                                "None",
                                entrys.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPos)


                        ));
                    }

                }

             /*   for(int j = 0; j<posts.size(); j++)
                {
                    Log.d(TAG, "onResponse: \n"  +
                            "PostURL: " + posts.get(j).getPostURL() + "\n" +
                                    "ThumbnailURL: " + posts.get(j).getThumbnailURL() + "\n" +
                                    "Title: " + posts.get(j).getTitle() + "\n" +
                                    "Author: " + posts.get(j).getAuthor() + "\n" +
                                    "Updated: " + posts.get(j).getDate_updated() + "\n"
                    );
                }*/

                ListView listView = (ListView) findViewById(R.id.listView);
                CustomListAdapter customListAdapter = new CustomListAdapter(MainActivity.this, R.layout.card_layout_main, posts);
                listView.setAdapter(customListAdapter);

            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS:" + t.getMessage());
            }
        });
    }

}
