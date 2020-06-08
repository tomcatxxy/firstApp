package com.swufe.buffermergetool.ui.home;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.swufe.buffermergetool.DBHelper;
import com.swufe.buffermergetool.DataItem;
import com.swufe.buffermergetool.DataManager;
import com.swufe.buffermergetool.R;
import com.swufe.buffermergetool.ui.tools.ToolsViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItFragment extends Fragment {

    private static final String TAG="ItFragment";
    private ListView out;
    private String data[];
    private SimpleAdapter listItemAdapter;//适配器
    private DataManager manager;

    public ItFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_it, container, false);

        manager=new DataManager(getActivity());
        List<DataItem> data_list=(List<DataItem>)manager.listAll(DBHelper.TB_NAME5);
        data=new String[data_list.size()];
        for(int m=0;m<data_list.size();m++){
            data[m]=data_list.get(m).getCurName()+data_list.get(m).getCurData();
        }

        out=root.findViewById(R.id.resultList_it);
        List<HashMap<String,String>> dataList=new ArrayList<HashMap<String, String>>();
        for(int j=0;j<data.length;j++){
            HashMap<String,String> map=new HashMap<String,String>();
            map.put("ItemTitle",data[j].substring(0,data[j].indexOf("#")));
            map.put("ItemDetail",data[j].substring(data[j].indexOf("#")+1));
            dataList.add(map);
        }
        listItemAdapter=new SimpleAdapter(getContext(),dataList,//listItems数据簿
                R.layout.list_item,//listItem的XML布局实现
                new String[]{"ItemTitle","ItemDetail"},
                new int[]{R.id.itemTitle,R.id.itemDetail}
        );

        out.setAdapter(listItemAdapter);
        out.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> map=(HashMap<String, String>) out.getItemAtPosition(position);
                String site=map.get("ItemDetail");

                //打开浏览器
                Intent intent = new Intent();
                intent.setData(Uri.parse(site));//Url 就是你要打开的网址
                intent.setAction(Intent.ACTION_VIEW);
                getActivity().startActivity(intent); //启动浏览器
            }
        });

        Log.i(TAG,"onCreateView:done");

        return root;
    }

}
