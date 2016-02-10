package com.ekunt.kamiweather.util;

import android.text.TextUtils;

import com.ekunt.kamiweather.db.KamiWeatherDB;
import com.ekunt.kamiweather.model.City;
import com.ekunt.kamiweather.model.County;
import com.ekunt.kamiweather.model.Province;

/**
 * 用户解析和处理服务器返回的数据
 * Created by E-Kunt on 2016/1/21.
 */
public class Utility {

    /**
     * 解析和处理服务器返回的省级数据，保存解析后的数据到Province表
     * @param kamiWeatherDB
     * @param response
     * @return
     */
    public synchronized static boolean handleProvincesResponse(KamiWeatherDB kamiWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //保存解析后的数据到Province表
                    kamiWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据，保存解析后的数据到City表
     * @param kamiWeatherDB
     * @param response
     * @param provinceId
     * @return
     */
    public synchronized static boolean handleCitiesResponse(KamiWeatherDB kamiWeatherDB, String response ,int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String p : allCities) {
                    String[] array = p.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //保存解析后的数据到City表
                    kamiWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }


    /**
     * 解析和处理服务器返回的县级数据，保存解析后的数据到County表
     * @param kamiWeatherDB
     * @param response
     * @param cityId
     * @return
     */
    public synchronized static boolean handleCountiesResponse(KamiWeatherDB kamiWeatherDB, String response ,int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String p : allCounties) {
                    String[] array = p.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //保存解析后的数据到County表
                    kamiWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}
