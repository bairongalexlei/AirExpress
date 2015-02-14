package com.alexlei.airexpress;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DisplayTicketActivity extends ActionBarActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ticket);

        //Get search parameters here
        Intent searchIntent = getIntent();
        String result = searchIntent.getStringExtra(SearchActivity.EXTRA_RESULT);

        //String[] searchParams = new String[]{origin, destination, flyDate};
        //new DownloadTicketTask().execute(searchParams);

        //Testing here
        // Create the text view
        /*textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(origin + "\n" + destination + "\n" + flyDate);*/

        // Set the text view as the activity layout
        //setContentView(textView);

        result = parseTicket(result);

        textView = new TextView(this);
        textView.setText(result);
        setContentView(textView);
    }

    private String parseTicket(String result) {
        StringBuilder ticketResults = new StringBuilder();
        try {
            JSONObject jResult = new JSONObject(result);

            jResult = jResult.getJSONObject("trips");
            JSONArray tripOptions = jResult.getJSONArray("tripOption");

            for (int i = 0; i < tripOptions.length(); i++){
                JSONObject tripOption = tripOptions.getJSONObject(i);
                String salePrice = tripOption.getString("saleTotal");
                ticketResults.append("Solution#" + (i + 1) + " Sale Price: " + salePrice + "\n");

                JSONArray slices = tripOption.getJSONArray("slice");
                for (int k = 0; k < slices.length(); k++) {
                    JSONObject slice = (JSONObject) tripOption.getJSONArray("slice").get(k);
                    ticketResults.append("         Slice 0" + "\n");

                    JSONArray segments = slice.getJSONArray("segment");
                    for (int j = 0; j < segments.length(); j++)
                    {
                        JSONObject oneSegment = segments.getJSONObject(j);
                        JSONObject oneFlight = oneSegment.getJSONObject("flight");
                        String flightNumber = oneFlight.getString("carrier") + oneFlight.getString("number");

                        JSONObject leg = oneSegment.getJSONArray("leg").getJSONObject(0);
                        String origin = leg.getString("origin");
                        String destination = leg.getString("destination");
                        String departureTime = leg.getString("departureTime");
                        String arrivalTime = leg.getString("arrivalTime");

                        ticketResults.append("             " + flightNumber + " " + origin + " " + departureTime + " " +
                                destination + " " + arrivalTime + "\n");
                    }
                }

                ticketResults.append("\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return ticketResults.toString();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_ticket, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
