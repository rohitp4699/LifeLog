package edu.csulb.android.assignment4;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends Activity {
    Button fromDate, toDate, button;
    TextView selectedFromDate, selectedToDate, resultText;
    Calendar calendar = Calendar.getInstance();
    Spinner selectQuery;
    GoogleMap googleMap;
    boolean mono = false;
    int dateButtonId = -1, fromDay = 1, fromMonth = 0, fromYear = 2015, fromHour = 0, fromMin = 0, toDay = 1, toMonth = 0, toYear = 2015, toHour = 0, toMin = 0;
    DBOperations db = new DBOperations(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            button = (Button) this.findViewById(R.id.button);
            fromDate = (Button) this.findViewById(R.id.fromDate);
            toDate = (Button) this.findViewById(R.id.toDate);
            selectedFromDate = (TextView) this.findViewById(R.id.selectedFromDate);
            selectedToDate = (TextView) this.findViewById(R.id.selectedToDate);
            resultText = (TextView) this.findViewById(R.id.resultText);
            selectQuery = (Spinner) this.findViewById(R.id.selectQuery);
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
            ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.query, android.R.layout.simple_spinner_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectQuery.setAdapter(arrayAdapter);
            button.setOnClickListener(buttonOnClick);
            fromDate.setOnClickListener(fromDateOnClick);
            toDate.setOnClickListener(toDateOnClick);
            List<DataTransfer> myList = new ArrayList<DataTransfer>();
            String root_sd = Environment.getExternalStorageDirectory().toString() + "/Life Log";
            File file = new File(root_sd);
            File list[] = file.listFiles();
            for (int i = 0; i < list.length; i++) {
                File myFile = new File(root_sd + "/" + list[i].getName());
                FileInputStream fIn = new FileInputStream(myFile);
                BufferedReader myReader = new BufferedReader(
                        new InputStreamReader(fIn));
                String aDataRow = "";
                while ((aDataRow = myReader.readLine()) != null) {
                    String row[] = aDataRow.split(",");
                    if (row.length >= 17) {
                        DataTransfer transfer = new DataTransfer();
                        transfer.timestamp0 = row[0].trim();
                        transfer.timestamp1 = row[1];
                        transfer.unix_timestamp = Long.parseLong(row[2]);
                        transfer.activity_level = Double.parseDouble(row[3]);
                        transfer.activity_type = row[4];
                        transfer.step_count = Integer.parseInt(row[5]);
                        transfer.light = Double.parseDouble(row[6]);
                        transfer.media_usage = Integer.parseInt(row[7]);
                        transfer.latitude = Double.parseDouble(row[8]);
                        transfer.longitude = Double.parseDouble(row[9]);
                        transfer.venue_name = row[10];
                        transfer.venue_category = row[11];
                        transfer.venue_category_type = row[12];
                        transfer.setting = Integer.parseInt(row[13]);
                        transfer.application_count = "";
                        for (int j = 14; j <= row.length - 4; j++) {
                            transfer.application_count += row[j].trim() + ";";
                        }
                        if (transfer.application_count.length() >= 3) {
                            transfer.application_count = transfer.application_count.substring(1, transfer.application_count.length() - 2).trim();
                        } else {
                            transfer.application_count = "";
                        }
                        transfer.timeband = Integer.parseInt(row[row.length - 3]);
                        transfer.week = Integer.parseInt(row[row.length - 2]);
                        transfer.photo = Integer.parseInt(row[row.length - 1]);
                        db.insert(transfer);
                    }
                }

                myReader.close();
            }
        } catch (Exception e) {
        }
    }

    private View.OnClickListener buttonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ExecuteQuery();
        }
    };

    private View.OnClickListener fromDateOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new DatePickerDialog(MainActivity.this, fromDateSetListener, fromYear, fromMonth, fromDay).show();
        }
    };

    private View.OnClickListener toDateOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new DatePickerDialog(MainActivity.this, toDateSetListener, toYear, toMonth, toDay).show();
        }
    };

    private DatePickerDialog.OnDateSetListener fromDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (view.isShown()) {
                fromYear = year;
                fromMonth = monthOfYear;
                fromDay = dayOfMonth;
                new TimePickerDialog(MainActivity.this, fromTimeSetListener, fromHour, fromMin, true).show();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener toDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (view.isShown()) {
                toYear = year;
                toMonth = monthOfYear;
                toDay = dayOfMonth;
                new TimePickerDialog(MainActivity.this, toTimeSetListener, toHour, toMin, true).show();
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener fromTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (view.isShown()) {
                fromHour = hourOfDay;
                fromMin = minute;
                selectedFromDate.setText((fromMonth + 1) + "/" + fromDay + "/" + fromYear + "," + fromHour + ":" + fromMin);
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener toTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (view.isShown()) {
                toHour = hourOfDay;
                toMin = minute;
                selectedToDate.setText((toMonth + 1) + "/" + toDay + "/" + toYear + "," + toHour + ":" + toMin);
            }
        }
    };

    private void ExecuteQuery() {
        Integer option = selectQuery.getSelectedItemPosition();
        DataTransfer dataTransfer = new DataTransfer();
        dataTransfer.option = option;
        if (option >= 1) {
            try {
                DateFormat dfm = new SimpleDateFormat("yyyyMMddHHmm");
                dataTransfer.date_from = dfm.parse(String.valueOf(fromYear) + String.format("%2d", fromMonth + 1) + String.format("%2d", fromDay) + String.format("%2d", fromHour) + String.format("%2d", fromMin)).getTime();
                dataTransfer.date_to = dfm.parse(String.valueOf(toYear) + String.format("%2d", toMonth + 1) + String.format("%2d", toDay) + String.format("%2d", toHour) + String.format("%2d", toMin)).getTime();
            } catch (Exception e) {
            }
            List<DataTransfer> result = db.query(dataTransfer);
            if (result != null && result.size() > 0) {
                switch (option) {
                    case 1:
                        resultText.setText(String.valueOf(result.get(0).activity_level));
                        break;
                    case 2:
                        int minutes = result.get(0).id * 5;
                        resultText.setText(String.valueOf(minutes));
                        break;
                    case 3:
                        resultText.setText(String.valueOf(result.get(0).step_count));
                        break;
                    case 4:
                        Map<String, Integer> appCount = new HashMap();
                        for (DataTransfer data : result) {
                            String splitValue[] = data.application_count.split(";");
                            for (String eachValue : splitValue) {
                                if (appCount.containsKey(eachValue)) {
                                    Integer count = appCount.get(eachValue) + 1;
                                    appCount.put(eachValue, count);
                                } else {
                                    appCount.put(eachValue, 1);
                                }
                            }
                        }
                        String text = "";
                        Integer count = 0;
                        for (Map.Entry<String, Integer> entry : entriesSortedByValues(appCount)) {
                            count++;
                            text += entry.getKey() + " - " + entry.getValue() + ", ";
                            if (count == 3) {
                                break;
                            }
                        }
                        resultText.setText(text);
                        break;
                    case 5:
                        break;
                    case 6:
                        Random rand = new Random();
                        for (int i = 0; i < 5; i++) {
                            int n = rand.nextInt(result.size());
                            DataTransfer trans = result.get(n);
                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(trans.latitude, trans.longitude))
                                    .draggable(true));
                        }
                        break;
                }
            }
        }
    }

    static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
                new Comparator<Map.Entry<K, V>>() {
                    @Override
                    public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                        int res = e2.getValue().compareTo(e1.getValue());
                        return res != 0 ? res : 1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
