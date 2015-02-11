package com.alexlei.airexpress;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SearchActivity extends ActionBarActivity {
    public final static String EXTRA_ORIGIN = "com.alexlei.airexpress.ORIGIN";
    public final static String EXTRA_DESTINATION = "com.alexlei.airexpress.DESTINATION";
    public final static String EXTRA_FLY_DATE = "com.alexlei.airexpress.FLY_DATE";
    public final static String EXTRA_RESULT = "com.alexlei.airexpress.RESULT";

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    public void getTickets(View view) {
        // Check network availability
        Context context = getApplicationContext();
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast noNetworkPopup = Toast.makeText(context, R.string.network_not_available, Toast.LENGTH_SHORT);
            //noNetworkPopup.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
            noNetworkPopup.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            noNetworkPopup.show();
            return;
        }

        // Do something in response to button
        intent = new Intent(this, DisplayTicketActivity.class);

        EditText originText = (EditText) findViewById(R.id.edit_origin);
        String origin = originText.getText().toString();

        EditText destinationText = (EditText) findViewById(R.id.edit_destination);
        String destination = destinationText.getText().toString();

        EditText flyDateText = (EditText) findViewById(R.id.edit_fly_date);
        String flyDate = flyDateText.getText().toString();

        String[] searchParams = new String[]{origin, destination, flyDate};
        new DownloadTicketTask().execute(searchParams);
    }

    private class DownloadTicketTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... searchParams) {
            return downloadTicket(searchParams);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                if (result.toLowerCase().contains("error"))
                {
                    Context context = getApplicationContext();
                    Toast noResultPopup = Toast.makeText(context, R.string.no_ticket_result, Toast.LENGTH_SHORT);
                    noResultPopup.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    noResultPopup.show();
                    return;
                }

                intent.putExtra(EXTRA_RESULT, result);
                startActivity(intent);
            }
            catch (Exception e)
            {
                Context context = getApplicationContext();
                Toast errorPopup = Toast.makeText(context, R.string.error_occur, Toast.LENGTH_SHORT);
                errorPopup.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                errorPopup.show();
            }
        }

        private String downloadTicket(String... searchParams) {//throws IOException {
            String apiUrl = "https://www.googleapis.com/qpxExpress/v1/trips/search?key=AIzaSyDBC6HfBNFRr6CqQo32PV7ZaUUbAZFRqeg";
            String searchOrigin = searchParams[0];
            String searchDestination = searchParams[1];
            String searchFlyDate = searchParams[2];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                String jsonRequest = "{\n" +
                        "  \"request\": {\n" +
                        "    \"passengers\": {\n" +
                        "      \"adultCount\": 1\n" +
                        "    },\n" +
                        "    \"slice\": [\n" +
                        "      {\n" +
                        "        \"origin\": \"" + searchOrigin + "\",\n" +
                        "        \"destination\": \"" + searchDestination + "\",\n" +
                        "        \"date\": \"" + searchFlyDate + "\"\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"solutions\": 6\n" +
                        "  }\n" +
                        "}";

                //conn.connect();
                OutputStream os = conn.getOutputStream();
                os.write(jsonRequest.getBytes());
                os.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                StringBuilder sb = new StringBuilder();
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                    sb.append("\n");
                }

                conn.disconnect();
                String result = sb.toString();
                return result;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                return "Api URL Error!";

            }
            catch (IOException e) {
                e.printStackTrace();
                return "IO Error!";
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println(e.getMessage());
                return "Unknown Exception!";
            }
        }
    }
}
