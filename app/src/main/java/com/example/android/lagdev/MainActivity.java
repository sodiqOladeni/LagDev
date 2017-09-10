package com.example.android.lagdev;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.example.android.lagdev.R;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String mUrl = "https://api.github.com/search/users?q=location:lagos";
    com.example.android.lagdev.ProfileAdapter profileAdapter;
    View loadingIndicator;
    OkHttpClient client = new OkHttpClient();
    ListView listview;
    /**
     * TextView that is displayed when the list is empty
     */
    private AlertDialog dialog;
    private TextView mEmptyStateTextView;
    private Button mRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GitAsyncTask task = new GitAsyncTask();
        task.execute();


        loadingIndicator = findViewById(R.id.loading_indicator);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mRetry = (Button) findViewById(R.id.retry);

        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        listview = (ListView) findViewById(R.id.list);
        listview.setEmptyView(mEmptyStateTextView);

        profileAdapter = new com.example.android.lagdev.ProfileAdapter(this, new ArrayList<Profile>());
        listview.setAdapter(profileAdapter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    private class GitAsyncTask extends AsyncTask<URL, Void, List<Profile>> {

        @Override
        protected List<Profile> doInBackground(URL... urls) {

            List<Profile> profiles = loadData();

            return profiles;
        }

        @Override
        protected void onPostExecute(List<Profile> profiles) {
            if (profiles == null) {
                return;
            }
            loadingIndicator.setVisibility(View.GONE);
            profileAdapter.addAll(profiles);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialog);
                    dialog.setTitle("User Profile");

                    final String username = profileAdapter.getItem(position).getMUserName();
                    final String githubUrl = profileAdapter.getItem(position).getMGitUrl();

                    //Get reference to imageview from the clicked listview line
                    //This allows me get the drawable without having to try the network call to retrieve the image again.
                    ImageView original_imageview = (ImageView) view.findViewById(R.id.profile_icon);
                    //dialog.show();

                    TextView textUsername = (TextView) dialog.findViewById(R.id.user_name);
                    TextView textUserUrl = (TextView) dialog.findViewById(R.id.user_url);
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.user_image);
                    Button share = (Button) dialog.findViewById(R.id.share);

                    imageView.setImageDrawable(original_imageview.getDrawable());

                    textUsername.setText(username);
                    textUserUrl.setText(githubUrl);

                    Linkify.addLinks(textUserUrl, Linkify.WEB_URLS);

                    share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome developer " + username + ", " + githubUrl);
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Github User");
                            startActivity(Intent.createChooser(shareIntent, "Share...."));
                        }
                    });

                    dialog.show();
                }
            });
        }

        public List<Profile> loadData() {
            if (mUrl == null) {
                return null;
            }
            loadingIndicator.setVisibility(View.VISIBLE);
            List<Profile> profiles = new ArrayList<>();
            Request request = new Request.Builder().url(mUrl).build();
            Response response = null;
            Response nextResponse = null;
            try {
                response = client.newCall(request).execute();
                String responseData = response.body().string();
                JSONObject jsonObject = new JSONObject(responseData);
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject user = jsonArray.getJSONObject(i);
                    String htmlUrl = user.getString("html_url");
                    String avatarUrl = user.getString("avatar_url");
                    String userName = "@" + user.getString("login");
                    Log.d("MYLOG", avatarUrl + userName + htmlUrl + "/n");
                    Profile profile = new Profile(avatarUrl, userName, htmlUrl);
                    profiles.add(profile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return profiles;
        }
    }
}
