package com.alexlei.airexpress;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class SearchActivity extends ActionBarActivity {
    public final static String EXTRA_RESULT = "com.alexlei.airexpress.RESULT";

    private Intent intent;
    private DatePickerDialog flyDatePickerDialog;
    private AutoCompleteTextView flyDateEditText;
    private SimpleDateFormat dateFormatter;
    private AutoCompleteTextView autoCompleteTVOrigin;
    private AutoCompleteTextView autoCompleteTVDestination;
    private ArrayList<String> originCityNamesFromWS;
    private ArrayList<String> originCityCodesFromWS;
    private ArrayList<String> destinationCityNamesFromWS;
    private ArrayList<String> destinationCityCodesFromWS;
    private int selectedOriginCityIndex;
    private int selectedDestinationCityIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        initializeUI();
    }

    private void initializeUI() {
        selectedOriginCityIndex = -1;
        selectedDestinationCityIndex = -1;

        originCityNamesFromWS = new ArrayList<String>();
        originCityCodesFromWS = new ArrayList<String>();
        final ArrayAdapter adapterOriginCityNames = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1, originCityNamesFromWS);
        autoCompleteTVOrigin = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_origin);
        autoCompleteTVOrigin.setAdapter(adapterOriginCityNames);
        //autoCompleteTVOrigin.setThreshold(1);
        autoCompleteTVOrigin.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!IsNetworkAvailable())
                {
                    return;
                }

                if (s.length() < 1)
                    return;

                if (originCityNamesFromWS.size() > 0)
                {
                    int cityNameIndex = originCityNamesFromWS.indexOf(s.toString());
                    if (cityNameIndex >= 0) {
                        selectedOriginCityIndex = cityNameIndex;
                        return;
                    }

                    for (String originCityName: originCityNamesFromWS){
                        if (originCityName.contains(s)) {
                            selectedOriginCityIndex = -1;
                            return;
                        }
                    }
                }

                selectedOriginCityIndex = -1;
                new AsyncTask<String, Void, ArrayList<CityNameCode>>(){

                    @Override
                    protected ArrayList<CityNameCode> doInBackground(String... params) {
                        ArrayList<CityNameCode> cityNameCodes = null;
                        try {
                            cityNameCodes = GetCityNameCodes(params[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return cityNameCodes;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<CityNameCode> cityNameCodes) {
                        adapterOriginCityNames.clear();
                        if (cityNameCodes != null){
                            originCityNamesFromWS.clear();
                            originCityCodesFromWS.clear();
                            for (int i = 0; i < cityNameCodes.size(); i++) {
                                originCityNamesFromWS.add(cityNameCodes.get(i).CityName);
                                originCityCodesFromWS.add(cityNameCodes.get(i).CityCode);
                            }
                            adapterOriginCityNames.addAll(originCityNamesFromWS);
                        }
                        adapterOriginCityNames.notifyDataSetChanged();
                    }
                }.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destinationCityNamesFromWS = new ArrayList<String>();
        destinationCityCodesFromWS = new ArrayList<String>();
        final ArrayAdapter adapterDestinationCityNames = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1, destinationCityNamesFromWS);
        autoCompleteTVDestination =
                (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_destination);
        autoCompleteTVDestination.setAdapter(adapterDestinationCityNames);
        //autoCompleteTVDestination.setThreshold(1);
        autoCompleteTVDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!IsNetworkAvailable())
                {
                    return;
                }

                if (s.length() < 1)
                    return;

                if (destinationCityNamesFromWS.size() > 0)
                {
                    int cityNameIndex = destinationCityNamesFromWS.indexOf(s.toString());
                    if (cityNameIndex >= 0) {
                        selectedDestinationCityIndex = cityNameIndex;
                        return;
                    }

                    for (String originCityName: destinationCityNamesFromWS){
                        if (originCityName.contains(s)) {
                            selectedDestinationCityIndex = -1;
                            return;
                        }
                    }
                }

                selectedDestinationCityIndex = -1;
                new AsyncTask<String, Void, ArrayList<CityNameCode>>(){

                    @Override
                    protected ArrayList<CityNameCode> doInBackground(String... params) {
                        ArrayList<CityNameCode> cityNameCodes = null;
                        try {
                            cityNameCodes = GetCityNameCodes(params[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return cityNameCodes;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<CityNameCode> cityNameCodes) {
                        adapterDestinationCityNames.clear();
                        if (cityNameCodes != null){
                            destinationCityNamesFromWS = new ArrayList<String>();
                            destinationCityCodesFromWS = new ArrayList<String>();
                            for (int i = 0; i < cityNameCodes.size(); i++) {
                                destinationCityNamesFromWS.add(cityNameCodes.get(i).CityName);
                                destinationCityCodesFromWS.add(cityNameCodes.get(i).CityCode);
                            }
                            adapterDestinationCityNames.addAll(destinationCityNamesFromWS);
                        }
                        adapterDestinationCityNames.notifyDataSetChanged();
                    }
                }.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        flyDateEditText = (AutoCompleteTextView)findViewById(R.id.edit_fly_date);
        flyDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    try {
                        flyDatePickerDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        flyDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                flyDateEditText.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
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
        if (!IsNetworkAvailable()){
            return;
        }

        // Do something in response to button
        intent = new Intent(this, DisplayTicketActivity.class);

        if (selectedOriginCityIndex < 0)
        {
            Toast invalidOrigin = Toast.makeText(this, R.string.invalid_origin, Toast.LENGTH_SHORT);
            invalidOrigin.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            invalidOrigin.show();
            return;
        }
        String origin = originCityCodesFromWS.get(selectedOriginCityIndex);

        if (selectedDestinationCityIndex < 0){
            Toast invalidDestination = Toast.makeText(this, R.string.invalid_destination, Toast.LENGTH_SHORT);
            invalidDestination.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            invalidDestination.show();
            return;
        }
        String destination = destinationCityCodesFromWS.get(selectedDestinationCityIndex);

        EditText flyDateText = (EditText) findViewById(R.id.edit_fly_date);
        String flyDate = flyDateText.getText().toString();

        String[] searchParams = new String[]{origin, destination, flyDate};
        new DownloadTicketTask().execute(searchParams);
    }

    public void showFlyDateDatePicker(View view) {
        try {
            flyDatePickerDialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean IsNetworkAvailable(){
        // Check network availability
        Context context = getApplicationContext();
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast noNetworkPopup = Toast.makeText(context, R.string.network_not_available, Toast.LENGTH_SHORT);
            noNetworkPopup.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            noNetworkPopup.show();
            return false;
        }

        return true;
    }

    public class CityNameCode {
        public String CityName;
        public String CityCode;
    }

    private ArrayList<CityNameCode> GetCityNameCodes(String searchKey)
    {
        ArrayList<CityNameCode> cityNameCodes = null;
        try {
            String charset = "UTF-8";
            URL apiUrl = new URL("http://openflights.org/php/apsearch.php");
            String searchCityName = searchKey;
            String query = String.format("city=%s&country=ALL&dst=U&db=airports&iatafilter=true&action=SEARCH",
                    URLEncoder.encode(searchCityName, charset));

            HttpURLConnection connection = (HttpURLConnection)apiUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream output = connection.getOutputStream();
            output.write(query.getBytes(charset));

            InputStream responseStream = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(responseStream));
            String line;
            StringBuffer responseStrBuffer = new StringBuffer();
            while((line = rd.readLine()) != null) {
                responseStrBuffer.append(line);
                responseStrBuffer.append('\r');
            }
            rd.close();
            connection.disconnect();

            //read json string
            String resultString = responseStrBuffer.toString();

            JSONObject jResult = new JSONObject(resultString);
            JSONArray foundAirPorts = jResult.getJSONArray("airports");
            cityNameCodes = new ArrayList<CityNameCode>();

            for (int i = 0; i < foundAirPorts.length(); i++){
                JSONObject oneAirPort = foundAirPorts.getJSONObject(i);
                String cityFullName = oneAirPort.getString("city") + ", " +
                        oneAirPort.getString("name") + ", " +
                        oneAirPort.getString("country");
                String cityCode = oneAirPort.getString("iata");
                CityNameCode oneCityNameCode = new CityNameCode();
                oneCityNameCode.CityName = cityFullName;
                oneCityNameCode.CityCode = cityCode;
                cityNameCodes.add(oneCityNameCode);
            }

            return  cityNameCodes;
        }
        catch (Exception e){
            e.printStackTrace();
            return cityNameCodes;
        }
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
            String apiUrl = "https://www.googleapis.com/qpxExpress/v1/trips/search";
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }
}
