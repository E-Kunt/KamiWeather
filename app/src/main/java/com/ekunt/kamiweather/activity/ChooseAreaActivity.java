package com.ekunt.kamiweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ekunt.kamiweather.R;
import com.ekunt.kamiweather.db.KamiWeatherDB;
import com.ekunt.kamiweather.model.City;
import com.ekunt.kamiweather.model.County;
import com.ekunt.kamiweather.model.Province;
import com.ekunt.kamiweather.util.HttpCallbackListener;
import com.ekunt.kamiweather.util.HttpUtil;
import com.ekunt.kamiweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by E-Kunt on 2016/1/29.
 */
public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    /**
     * 加载进度对话框
     */
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private KamiWeatherDB kamiWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        findViews();
        kamiWeatherDB = KamiWeatherDB.getInstance(this);
        setListView();
        queryProvinces();

    }


    /**
     * 初始化控件
     */
    private void findViews() {
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
    }

    /**
     * 配置ListView
     */
    private void setListView() {
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
    }


    /**
     * 查询全国所有的省，优先从数据库查询，若无再从网上查
     */
    private void queryProvinces() {
        provinceList = kamiWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE ;
        } else {
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询所选省下所有的市，优先从数据库查询，若无再从网上查
     */
    private void queryCities() {
        cityList = kamiWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0 ) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    /**
     * 查询所选市下所有的县，优先从数据库查询，若无再从网上查
     */
    private void queryCounties() {
        countyList = kamiWeatherDB.loadCounties(selectedCity.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    /**
     * 根据代号和类型从服务器查询省市县数据
     * @param code
     * @param type
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(kamiWeatherDB,response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(kamiWeatherDB,response,selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(kamiWeatherDB,response,selectedCity.getId());
                }
                if (result) {
                    //回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败！",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载，请稍等...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获BACK按钮，根据当前级别显示不同数据或退出。
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
