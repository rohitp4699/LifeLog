package edu.csulb.android.assignment4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBOperations extends SQLiteOpenHelper {

    public DBOperations(Context context) {
        super(context, "a4_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE a4_table (" +
                "id integer primary key autoincrement," +
                "timestamp0 text," +
                "date integer," +
                "timestamp1 text," +
                "unix_timestamp integer," +
                "activity_level real," +
                "activity_type text," +
                "step_count integer," +
                "light real," +
                "media_usage integer," +
                "latitude real," +
                "longitude real," +
                "venue_name text," +
                "venue_category text," +
                "venue_category_type text," +
                "setting integer," +
                "application_count text," +
                "timeband integer," +
                "week integer," +
                "photo integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS a4_table");
        onCreate(db);
    }

    public List<DataTransfer> query(DataTransfer dataTransfer) {
        String query = "";

        switch (dataTransfer.option) {
            case 1:
                query = "SELECT AVG(activity_level) AS activity_level FROM a4_table WHERE unix_timestamp >= " + String.valueOf(dataTransfer.date_from) + " AND unix_timestamp <= " + String.valueOf(dataTransfer.date_to) + ";";
                break;

            case 2:
                query = "SELECT COUNT(id) AS row_count FROM a4_table WHERE unix_timestamp >= " + String.valueOf(dataTransfer.date_from) + " AND unix_timestamp <= " + String.valueOf(dataTransfer.date_to) + ";";
                break;

            case 3:
                query = "SELECT SUM(step_count) AS step_count FROM a4_table WHERE unix_timestamp >= " + String.valueOf(dataTransfer.date_from) + " AND unix_timestamp <= " + String.valueOf(dataTransfer.date_to) + ";";
                break;

            case 4:
                query = "SELECT application_count FROM a4_table WHERE unix_timestamp >= " + String.valueOf(dataTransfer.date_from) + " AND unix_timestamp <= " + String.valueOf(dataTransfer.date_to) + ";";
                break;

            case 5:
                break;

            case 6:
                query = "SELECT timestamp0, latitude, longitude FROM a4_table WHERE unix_timestamp >= " + String.valueOf(dataTransfer.date_from) + " AND unix_timestamp <= " + String.valueOf(dataTransfer.date_to) + ";";
                break;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<DataTransfer> myList = new ArrayList<DataTransfer>();

        if (cursor.moveToFirst()) {
            do {
                DataTransfer transfer = new DataTransfer();

                switch (dataTransfer.option) {
                    case 1:
                        transfer.activity_level = cursor.getDouble(cursor.getColumnIndex("activity_level"));
                        break;

                    case 2:
                        transfer.id = cursor.getInt(cursor.getColumnIndex("row_count"));
                        break;

                    case 3:
                        transfer.step_count = cursor.getInt(cursor.getColumnIndex("step_count"));
                        break;

                    case 4:
                        transfer.application_count = cursor.getString(cursor.getColumnIndex("application_count"));
                        break;

                    case 5:
                        break;

                    case 6:
                        transfer.timestamp0 = cursor.getString(cursor.getColumnIndex("timestamp0"));
                        transfer.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                        transfer.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                        break;
                }

                myList.add(transfer);
            } while (cursor.moveToNext());
        }
        db.close();
        return myList;
    }

    public void insert(DataTransfer dataTransfer) {
        ContentValues values = new ContentValues();
        values.put("timestamp0", dataTransfer.timestamp0);
        values.put("timestamp1", dataTransfer.timestamp1);
        values.put("unix_timestamp", dataTransfer.unix_timestamp);
        values.put("activity_level", dataTransfer.activity_level);
        values.put("activity_type", dataTransfer.activity_type);
        values.put("step_count", dataTransfer.step_count);
        values.put("light", dataTransfer.light);
        values.put("media_usage", dataTransfer.media_usage);
        values.put("latitude", dataTransfer.latitude);
        values.put("longitude", dataTransfer.longitude);
        values.put("venue_name", dataTransfer.venue_name);
        values.put("venue_category", dataTransfer.venue_category);
        values.put("venue_category_type", dataTransfer.venue_category_type);
        values.put("setting", dataTransfer.setting);
        values.put("application_count", dataTransfer.application_count);
        values.put("timeband", dataTransfer.timeband);
        values.put("week", dataTransfer.week);
        values.put("photo", dataTransfer.photo);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("a4_table", null, values);
        db.close();
    }
}
