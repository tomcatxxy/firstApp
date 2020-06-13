package com.swufe.buffermergetool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Runnable{

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private final String TAG="MainActivity";
    DataManager manager= new DataManager(this);//获取数据库对象
    private Handler handler;
    private String updateTime;//构建汇率更新字符串，表示上次更新时间
    private boolean flag=true;//需要更新则为true
    private boolean error;//用于检查网络是否故障,没有故障为false

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //加载toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //设置FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.floating_tips).setMessage(R.string.floating_detail).setPositiveButton(R.string.floating_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG,"FloatingActionButton：对话框事件处理");
                        Thread thread=new Thread(MainActivity.this);
                        thread.start();
                        Toast.makeText(MainActivity.this,R.string.tips,Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton(R.string.floating_no,null);
                builder.create().show();

            }
        });

        //加载抽屉布局，并设置监听方便调试
        drawer = findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(drawerListener);

        //加载导航视图并且加载NavController
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //获取上次更新的时间
        SharedPreferences sp=getSharedPreferences("myUpdate", Activity.MODE_PRIVATE);
        updateTime=sp.getString("updateTime","0000.00.00");

        //获取系统当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");//设置日期格式
        final String OSTime=df.format(new Date());//获取系统当前时间
        Log.i(TAG,"onCreate:OSTime="+OSTime);

        //获取设置的数据更新频率
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String frequency = sharedPrefs.getString(getString(R.string.update),"1");

        //检查是否需要更新，若需要更新，则更新flag为true
        try {
            Date update=df.parse(updateTime);
            Calendar rightNow =Calendar.getInstance();;
            rightNow.setTime(update);
            for(int n=0;n<Integer.valueOf(frequency);n++){
                if(df.format(rightNow.getTime()).equals(OSTime)){
                    flag=false;
                }
                rightNow.add(Calendar.DAY_OF_YEAR,1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //检查flag，若flag为true，则开启子线程完成更新，并将数据保存至数据库
        if(flag){
            updateTime=OSTime;
            Thread thread=new Thread(this);
            thread.start();
            Log.i(TAG,"run:更新数据");
        }

        //处理子线程的返回
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    if (!error) {
                        //记录更新日期
                        SharedPreferences.Editor edit=getSharedPreferences("myUpdate", Activity.MODE_PRIVATE).edit();
                        edit.putString("updateTime",updateTime);
                        edit.commit();
                        Log.i(TAG,"run:更新日期："+updateTime);
                        Log.i(TAG,"onActivityResult:handlerMessage:committing of rate finished");
                        Toast.makeText(MainActivity.this,R.string.update_success,Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this,R.string.update_fail,Toast.LENGTH_SHORT).show();
                    }
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

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId()==R.id.action_settings){
            //转向设置页面
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
        }
        else if(item.getItemId()==R.id.action_search){
            //转向搜索页面
            Intent query = new Intent(this, QueryActivity.class);
            startActivity(query);
        }
        else if(item.getItemId()==R.id.action_helper){
            //弹出帮助对话框
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.Helper).setMessage("这是一款针对西南财经大学经济信息工程学院学生的APP，在这个APP中您能看到下面的信息：\n" +
                    "1、西南财经大学官网最近的通知公告；\n" +
                    "2、经济信息工程学院最近的通知公告;\n" +
                    "3、西南财经大学校园招聘信息；\n" +
                    "4、最近的学术讲座的举办情况；\n" +
                    "5、经济信息工程学院官网上发布的科技前沿信息。\n\n" +
                    "APP每天都会更新一次信息，当然您也可以直接点击APP中的红色按钮来立刻更新信息，感谢您的使用！(长按消息还可分享哦)").setNegativeButton(R.string.floating_understand,null);
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
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
            switch (i){
                case DrawerLayout.STATE_DRAGGING:
                    Log.i(TAG, "onDrawerStateChanged: 滑动状态");
                    break;
                case DrawerLayout.STATE_IDLE:
                    Log.i(TAG, "onDrawerStateChanged: 静止状态");
                    break;
                case DrawerLayout.STATE_SETTLING:
                    Log.i(TAG, "onDrawerStateChanged: 设置状态");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void run() {
        try{
            getRecruitInfo();//获取招聘信息
            getNeedsInfo();//获取招聘需求信息
            getInternshipInfo();//获取实习需求信息
            getSwufeNotices();//获取学校公告
            getItNotices();//获取学院公告
            getLectureInfo();//获取讲座信息
            getFrontInfo();//获取科技前沿信息
        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG,"run:请检查网络，如果网络没问题则说明网页已改变，那么请修改解析网页源代码");
        }
        Message msg=handler.obtainMessage(7);
        msg.what=5;
        handler.sendMessage(msg);
    }

    private void getRecruitInfo(){
        Log.i(TAG,"getRecruitInfo():getRecruitInfo....");
        //获取网络数据
        Document doc = null;
        List<DataItem> dataList=new ArrayList<DataItem>();
        try {
            doc = Jsoup.connect("https://jobzpgl.swufe.edu.cn/").get();
            Log.i(TAG,"getInfo:"+doc.title());
            //获取url
            String url="https://jobzpgl.swufe.edu.cn"+(doc.select("body > div.wrap.mar0 > div:nth-child(2) > div.messList.fl > p > a.fr.zt14.cor8.hand.newsList_url.newsList_url1"))
                    .select("a").attr("href");
            Log.i(TAG,"getRecruitInfo():"+url);
            doc = Jsoup.connect(url).get();
            Elements lis=(doc.select("body > div.wrap.mar0 > ul.newListContent")).select("li");
            String title,detail;
            for(int i=0;i<lis.size();i++){
                title=lis.get(i).select("a").text()+"——"+lis.get(i).select("span").text()+"#";
                detail="https://jobzpgl.swufe.edu.cn"+(lis.get(i).attr("onclick")).replace("javascript:window.location.href='","");
                Log.i(TAG,"getRecruitInfo():"+title+detail);
                dataList.add(new DataItem(title,detail));
            }

            //把数据写入数据库
            manager.deleteAll(DBHelper.TB_NAME1);
            manager.addAll(dataList,DBHelper.TB_NAME1);
        } catch (IOException e) {
            e.printStackTrace();
            error=true;//网络出现问题，更新error
        }
    }
    private void getNeedsInfo(){
        Log.i(TAG,"getNeedsInfo():getNeedsInfo....");
        //获取网络数据
        Document doc = null;
        List<DataItem> dataList=new ArrayList<DataItem>();
        try {
            doc = Jsoup.connect("https://jobzpgl.swufe.edu.cn/").get();
            Log.i(TAG,"getInfo:"+doc.title());
            //获取url
            String url="https://jobzpgl.swufe.edu.cn"+(doc.select("body > div.wrap.mar0 > div:nth-child(3) > div.messList.fl > p > a.fr.zt14.cor8.hand.employ_url.employ_url2"))
                    .select("a").attr("href");
            Log.i(TAG,"getNeedsInfo():"+url);
            doc = Jsoup.connect(url).get();
            Elements lis=(doc.select("body > div.wrap.mar0 > ul.newListContent")).select("li");
            String title,detail;
            for(int i=0;i<lis.size();i++){
                title=lis.get(i).select("a").text()+"——"+lis.get(i).select("span").text()+"#";
                detail="https://jobzpgl.swufe.edu.cn"+(lis.get(i).attr("onclick")).replace("javascript:window.location.href='","");
                Log.i(TAG,"getNeedsInfo():"+title+detail);
                dataList.add(new DataItem(title,detail));
            }

            //把数据写入数据库
            manager.deleteAll(DBHelper.TB_NAME2);
            manager.addAll(dataList,DBHelper.TB_NAME2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getInternshipInfo(){
        Log.i(TAG,"getInternshipInfo():getInternshipInfo....");
        //获取网络数据
        Document doc = null;
        List<DataItem> dataList=new ArrayList<DataItem>();
        try {
            doc = Jsoup.connect("https://jobzpgl.swufe.edu.cn/").get();
            Log.i(TAG,"getInfo:"+doc.title());
            //获取url
            String url="https://jobzpgl.swufe.edu.cn"+(doc.select("body > div.wrap.mar0 > div:nth-child(3) > div.messList.fl > p > a.fr.zt14.cor8.hand.employ_url.employ_url3"))
                    .select("a").attr("href");
            Log.i(TAG,"getInternshipInfo():"+url);
            doc = Jsoup.connect(url).get();
            Elements lis=(doc.select("body > div.wrap.mar0 > ul.newListContent")).select("li");
            String title,detail;
            for(int i=0;i<lis.size();i++){
                title=lis.get(i).select("a").text()+"——"+lis.get(i).select("span").text()+"#";
                detail="https://jobzpgl.swufe.edu.cn"+(lis.get(i).attr("onclick")).replace("javascript:window.location.href='","");
                Log.i(TAG,"getInternshipInfo():"+title+detail);
                dataList.add(new DataItem(title,detail));
            }

            //把数据写入数据库
            manager.deleteAll(DBHelper.TB_NAME3);
            manager.addAll(dataList,DBHelper.TB_NAME3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getSwufeNotices() {
        Log.i(TAG,"SwufeNotices():getSwufeNotices....");
        //获取网络数据
        Document doc = null;
        List<DataItem> dataList=new ArrayList<DataItem>();
        try {
            doc = Jsoup.connect("https://www.swufe.edu.cn/index/tzgg.htm").get();
            Log.i(TAG,"getSwufeNotices():"+doc.title());
            //获取a中的数据
            Elements lis=(doc.select("body > div > div.c_main > div > div > div.c_box_left > ul")).select("li");
            String title,detail;
            for(int i=0;i<lis.size();i++){
                title=lis.get(i).select("a").attr("title")+"——"+lis.get(i).select("span").text()+"#";
                detail="http://www.swufe.edu.cn"+(lis.get(i).select("a").attr("href")).replace("..","");
                Log.i(TAG,"getSwufeNotices():"+title+detail);
                dataList.add(new DataItem(title,detail));
            }

            //把数据写入数据库
            manager.deleteAll(DBHelper.TB_NAME4);
            manager.addAll(dataList,DBHelper.TB_NAME4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getItNotices(){
        Log.i(TAG,"getItNotices():getItNotices....");
        //获取网络数据
        Document doc = null;
        List<DataItem> dataList=new ArrayList<DataItem>();
        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Log.i(TAG,"getItNotices():"+doc.title());
            //获取a中的数据
            Elements as=(doc.select("body > div.main > div > div > div.col-xs-12.col-md-9 > div > ul")).select("a");
            String title,detail;
            for(int i=0;i<as.size();i++){
                title=as.get(i).select("span.article-showTitle").text()+"——"+as.get(i).select("span.article-showTime").text()+"#";
                detail="https://it.swufe.edu.cn"+(as.get(i).attr("href")).replace("..","");
                Log.i(TAG,"getItNotices():"+title+detail);
                dataList.add(new DataItem(title,detail));
            }

            //把数据写入数据库
            manager.deleteAll(DBHelper.TB_NAME5);
            manager.addAll(dataList,DBHelper.TB_NAME5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getLectureInfo(){
        Log.i(TAG,"getLectureInfo():getLectureInfo....");
        //获取网络数据
        Document doc = null;
        List<DataItem> dataList=new ArrayList<DataItem>();
        try {
            doc = Jsoup.connect("http://www.swufe.edu.cn/index/xsjz.htm").get();
            Log.i(TAG,"getLectureInfo():"+doc.title());
            //获取a中的数据
            Elements lis=(doc.select("body > div > div.c_main > div > div > div.c_box_left > ul")).select("li");
            String title,detail;
            for(int i=0;i<lis.size();i++){
                title=lis.get(i).select("a").attr("title")+"——"+lis.get(i).select("span").text()+"#";
                detail="http://www.swufe.edu.cn"+(lis.get(i).select("a").attr("href")).replace("..","");
                Log.i(TAG,"getLectureInfo():"+title+detail);
                dataList.add(new DataItem(title,detail));
            }

            //把数据写入数据库
            manager.deleteAll(DBHelper.TB_NAME6);
            manager.addAll(dataList,DBHelper.TB_NAME6);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getFrontInfo(){
        Log.i(TAG,"getFrontInfo():getFrontInfo....");
        //获取网络数据
        Document doc = null;
        List<DataItem> dataList=new ArrayList<DataItem>();
        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/kydt.htm").get();
            Log.i(TAG,"getFrontInfo():"+doc.title());
            //获取a中的数据
            Elements as=(doc.select("body > div.main > div > div > div.col-xs-12.col-md-9 > div > ul")).select("a");
            String title,detail;
            for(int i=0;i<as.size();i++){
                title=as.get(i).select("span.article-showTitle").text()+"——"+as.get(i).select("span.article-showTime").text()+"#";
                detail="https://it.swufe.edu.cn"+(as.get(i).attr("href")).replace("..","");
                Log.i(TAG,"getFrontInfo():"+title+detail);
                dataList.add(new DataItem(title,detail));
            }

            //把数据写入数据库
            manager.deleteAll(DBHelper.TB_NAME7);
            manager.addAll(dataList,DBHelper.TB_NAME7);
            error=false;//网络没问题，更新error
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
