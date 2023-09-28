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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

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

        // Fetch data from the URL and populate the RecyclerView
        fetchData(universities);

        adapter = new UniversityAdapter(universities);
        recyclerView.setAdapter(adapter);
    }

    private void fetchData(List<University> universities) {
        String apiUrl = "https://universities.hipolabs.com/search";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                apiUrl,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String name = jsonObject.optString("name");
                            String country = jsonObject.optString("country");
                            String webPages = jsonObject.optString("web_pages");
                            universities.add(new University(name, country, webPages));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    // Handle error
                }
        );

        queue.add(jsonArrayRequest);
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
