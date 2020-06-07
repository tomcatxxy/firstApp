package com.swufe.buffermergetool;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Runnable{

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private final String TAG="MainActivity";
    DataManager manager= new DataManager(this);
    private Handler handler;
    private String updateTime;//构建汇率更新字符串，表示上次更新时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(drawerListener);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
       /* NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                navController.getGraph())
                .setDrawerLayout(drawer)
                .build();*/
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        String curDateStr=(new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        SharedPreferences sp=getSharedPreferences("myUpdate", Activity.MODE_PRIVATE);
        updateTime=sp.getString("updateTime","0000-00-00");//获取上次更新的时间

        if(!curDateStr.equals(updateTime)){
            updateTime=curDateStr;
            Thread thread=new Thread(this);
            thread.start();
        }
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    //记录更新日期
                    SharedPreferences.Editor edit=getSharedPreferences("myUpdate", Activity.MODE_PRIVATE).edit();
                    edit.putString("updateTime",updateTime);
                    edit.commit();
                    Log.i(TAG,"run:更新日期："+updateTime);
                    Log.i(TAG,"onActivityResult:handlerMessage:committing of rate finished");
                    Toast.makeText(MainActivity.this,"Data has updated",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawer == null) return;

        // 返回键: 侧滑开着就将其关闭, 关着则退出应用
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View view, float v) {
            Log.i(TAG, "onDrawerSlide: 滑动时:" + v);
        }

        @Override
        public void onDrawerOpened(@NonNull View view) {
            Log.i(TAG, "onDrawerOpened: 打开后");
        }

        @Override
        public void onDrawerClosed(@NonNull View view) {
            Log.i(TAG, "onDrawerClosed: 关闭后");
        }

        @Override
        public void onDrawerStateChanged(int i) {
            // 滑动状态
            switch (i){
                case DrawerLayout.STATE_DRAGGING:
                    Log.i(TAG, "onDrawerStateChanged: 滑动状态");
                    break;
                case DrawerLayout.STATE_IDLE:
                    Log.i(TAG, "onDrawerStateChanged: 静止状态");
                    break;
                case DrawerLayout.STATE_SETTLING:
                    // 设置状态在静止状态之前调用, 表示正在调整到最终位置
                    Log.i(TAG, "onDrawerStateChanged: 设置状态");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void run() {
        Log.i(TAG,"run:run()....");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"run:日期不相等，更新数据");
        //获取网络数据，放入List带回到主线程中
        Document doc = null;
        List<DataItem> dataList=new ArrayList<DataItem>();
        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Log.i(TAG,"run:"+doc.title());
            //获取a中的数据
            Elements as=(doc.select("body > div.main > div > div > div.col-xs-12.col-md-9 > div > ul")).select("a");
            Elements td=(doc.select("body > div.main > div > div > div.col-xs-12.col-md-9 > div > div > table > tbody > tr > td > table > tbody > tr")).select("td");
            int siteNum=Integer.parseInt((td.get(0).text()).substring(td.get(0).text().indexOf("/")+1));
            String title,detail;
            Log.i(TAG,siteNum+"");
            for(int i=0;i<as.size();i++){
                title=as.get(i).attr("title")+"——"+as.get(i).select("span.article-showTime").text();
                detail="#https://it.swufe.edu.cn"+(as.get(i).attr("href")).replace("..","");
                dataList.add(new DataItem(title,detail));
                Log.i(TAG,"run():"+title+detail);
            }
            for(int j=siteNum-1;j>0;j--){
                doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg/"+j+".htm").get();
                Elements temp=(doc.select("body > div.main > div > div > div.col-xs-12.col-md-9 > div > ul")).select("a");
                for(int k=0;k<temp.size();k++){
                    title=temp.get(k).attr("title")+"——"+temp.get(k).select("span.article-showTime").text();
                    detail="#https://it.swufe.edu.cn"+(temp.get(k).attr("href")).replace("..","");
                    dataList.add(new DataItem(title,detail));
                    Log.i(TAG,"run():"+title+detail);
                }
            }

            //把数据写入数据库
            manager.deleteAll(DBHelper.TB_NAME2);
            manager.addAll(dataList,DBHelper.TB_NAME2);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,"run:请检查网络，如果网络没问题则说明网页已改变，那么请修改解析网页源代码");
        }
        Message msg=handler.obtainMessage(7);
        msg.what=5;
        handler.sendMessage(msg);
    }
}
