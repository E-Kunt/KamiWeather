package com.ekunt.kamiweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ekunt.kamiweather.model.City;
import com.ekunt.kamiweather.model.County;
import com.ekunt.kamiweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装了对数据库的常用操作
 * Created by E-Kunt on 2016/1/20.
 */
public class KamiWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "kami_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    /**
     * 保存KamiWeatherDB的实例
     */
    private static KamiWeatherDB kamiWeatherDB;

    /**
     * 数据库操作对象
     */
    private SQLiteDatabase db;

    /**
     * 构造方法私有化
     * @param context
     */
    private KamiWeatherDB(Context context) {
        KamiWeatherOpenHelper dbHelper = new KamiWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取KamiWeatherDB实例
     * @param context
     * @return KamiWeatherDB
     */
    public synchronized static KamiWeatherDB getInstance(Context context) {
        if (kamiWeatherDB == null) {
            kamiWeatherDB = new KamiWeatherDB(context);
        }
        return kamiWeatherDB;
    }

    /**
     * 将Province实例存储到数据库中
     * @param province
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /**
     * 将City实例存储到数据库中
     * @param city
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }

    /**
     * 将County实例存储到数据库中
     * @param county
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("County", null, values);
        }
    }

    /**
     * 从数据库读取全国所有的省份信息
     * @return List<Province>
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 从数据库读取某个省份下所有的城市信息
     * @param provinceId
     * @return List<City>
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City",null,"province_id = ?",new String[]{String.valueOf(provinceId)},
                null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setProvinceId(provinceId);
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                list.add(city);
            }while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     *从数据库读取某个城市下所有的县信息
     * @param cityId
     * @return List<County>
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("City",null,"city_id = ?",new String[]{String.valueOf(cityId)},
                null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
            }while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

}
