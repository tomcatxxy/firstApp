package com.swufe.buffermergetool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final String Tag="SwufeInfoActivity";
    private EditText in;
    private ListView out;
    private String data[];

    private Handler handler;
    private SimpleAdapter listItemAdapter;//适配器
    private DataManager manager;
    private List<DataItem> data_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        String tb[]=new String[]{DBHelper.TB_NAME1,DBHelper.TB_NAME2,DBHelper.TB_NAME3,DBHelper.TB_NAME4,DBHelper.TB_NAME5,DBHelper.TB_NAME6,DBHelper.TB_NAME7};
        manager=new DataManager(this);
        data_list=new ArrayList<DataItem>();
        for(int i=0;i<tb.length;i++){
            List<DataItem> temp=manager.listAll(tb[i]);
            for(int j=0;j<temp.size();j++){
                data_list.add(temp.get(j));
            }
        }
        data=new String[data_list.size()];
        for(int m=0;m<data_list.size();m++){
            data[m]=data_list.get(m).getCurName()+data_list.get(m).getCurData();
        }

        out=findViewById(R.id.resultList);
        in=findViewById(R.id.keyWord);
        in.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                List<HashMap<String,String>> dataList=new ArrayList<HashMap<String, String>>();
                int flag=0;
                for(int j=0;j<data.length;j++){
                    if(data[j].indexOf(s.toString())!=-1){
                        flag=1;
                        HashMap<String,String> map=new HashMap<String,String>();
                        map.put("ItemTitle",data[j].substring(0,data[j].indexOf("#")));
                        map.put("ItemDetail",data[j].substring(data[j].indexOf("#")+1));
                        dataList.add(map);
                    }
                }
                if(flag==1){
                    listItemAdapter=new SimpleAdapter(QueryActivity.this,dataList,//listItems数据簿
                            R.layout.list_item,//listItem的XML布局实现
                            new String[]{"ItemTitle","ItemDetail"},
                            new int[]{R.id.itemTitle,R.id.itemDetail}
                    );
                    out.setAdapter(listItemAdapter);
                    out.setOnItemClickListener(QueryActivity.this);
                }
                else {
                    Toast.makeText(QueryActivity.this,"Sorry!No information containing the keyword ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //实现监听方法
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String> map=(HashMap<String, String>) out.getItemAtPosition(position);
        String site=map.get("ItemDetail");

        //打开浏览器
        Intent intent = new Intent();
        intent.setData(Uri.parse(site));//Url 就是你要打开的网址
        intent.setAction(Intent.ACTION_VIEW);
        this.startActivity(intent); //启动浏览器
    }
}
