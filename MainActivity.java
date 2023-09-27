package com.example.myapplication_1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UniversityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<University> universities = new ArrayList<>();

        String apiUrl = "https://universities.hipolabs.com/search";
        try {
            //JSONArray jsonArray = new JSONArray(apiUrl);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            StringBuilder result = new StringBuilder();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
            }

            connection.disconnect();

            // Now, you can parse the JSON response and create a JSONArray
            JSONArray jsonArray = new JSONArray(result.toString());

            //List<University> universities = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.optString("name");
                String country = jsonObject.optString("country");
                String webPages = jsonObject.optString("web_pages");
                universities.add(new University(name, country, webPages));
            }

            // Now you have a list of universities
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        //universities.add(new University("Kharkiv National University", "Ukraine", "https://karazin.ua"))

        adapter = new UniversityAdapter(universities);
        recyclerView.setAdapter(adapter);
    }

    public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.ViewHolder> {

        private List<University> universities;

        public UniversityAdapter(List<University> universities) {
            this.universities = universities;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            University university = universities.get(position);
            String universityInfo = university.getUniversityName() + "      " +
                    university.getCountryName() + "     " +
                    university.getWebsite();
            holder.textView.setText(universityInfo);

            // Make the website link clickable
            Linkify.addLinks(holder.textView, Linkify.WEB_URLS);

            // Set a custom click listener for website links
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = (TextView) v;
                    String url = textView.getText().toString();
                    if (url != null && !url.isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return universities.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(android.R.id.text1);
            }
        }
    }

    public class University {
        private String universityName;
        private String countryName;
        private String website;

        public University(String universityName, String countryName, String website) {
            this.universityName = universityName;
            this.countryName = countryName;
            this.website = website;
        }

        public String getUniversityName() {
            return universityName;
        }

        public String getCountryName() {
            return countryName;
        }

        public String getWebsite() {
            return website;
        }
    }
}
