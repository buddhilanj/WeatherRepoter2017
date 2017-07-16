package com.osmium.weatherrepoter2017;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.osmium.weatherrepoter2017.model.Weather;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by OSMiUM on 16/07/2017.
 */

public class WeatherDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weatherreporter2017.db";

    public static final String TABLE_WEATHER = "weather";
    public static final String WEATHER_CITY = "city";
    public static final String WEATHER_DESC = "weather";
    public static final String WEATHER_DATE = "requested_date";
    public static final String WEATHER_TEMP = "temprature";

    public static final String CREATE_DB_SQL =
            "CREATE TABLE " + TABLE_WEATHER + " (" +
                    "id INTEGER PRIMARY KEY, " +
                    WEATHER_DATE + " TEXT," +
                    WEATHER_CITY + " TEXT, " +
                    WEATHER_DESC + " TEXT, " +
                    WEATHER_TEMP + " TEXT" +
                    ")";


    public static final String DESTROY_WEATHER = "DROP TABLE " + TABLE_WEATHER + ";";

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DB_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_v, int new_v) {
        sqLiteDatabase.execSQL(DESTROY_WEATHER);
        onCreate(sqLiteDatabase);
    }

    public void addRecord(Weather weather) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(WEATHER_CITY, weather.getCity());
        content.put(WEATHER_DATE, weather.getDate());
        content.put(WEATHER_DESC, weather.getWeather());
        content.put(WEATHER_TEMP, weather.getTemp());
        db.insert(TABLE_WEATHER, null, content);
    }

    public ArrayList<Weather> getAllWeatherInfo() {
        ArrayList<Weather> array = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WEATHER, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Weather val = new Weather();
            val.setId(cursor.getInt(cursor.getColumnIndex("id")));
            val.setCity(cursor.getString(cursor.getColumnIndex(WEATHER_CITY)));
            val.setDate(cursor.getString(cursor.getColumnIndex(WEATHER_DESC)));
            val.setTemp(cursor.getString(cursor.getColumnIndex(WEATHER_TEMP)));
            array.add(val);
            cursor.moveToNext();
        }
        return array;
    }
}
