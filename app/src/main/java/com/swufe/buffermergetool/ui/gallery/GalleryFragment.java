package com.swufe.buffermergetool.ui.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.swufe.buffermergetool.DBHelper;
import com.swufe.buffermergetool.DataItem;
import com.swufe.buffermergetool.DataManager;
import com.swufe.buffermergetool.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private static final String TAG="SlideshowFragment";
    private EditText in;
    private ListView out;
    private String data[];
    private SimpleAdapter listItemAdapter;//适配器
    private DataManager manager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        manager=new DataManager(getActivity());
        List<DataItem> data_list=(List<DataItem>)manager.listAll(DBHelper.TB_NAME2);
        data=new String[data_list.size()];
        for(int m=0;m<data_list.size();m++){
            data[m]=data_list.get(m).getCurName()+data_list.get(m).getCurData();
        }

        out=root.findViewById(R.id.resultList);
        in=root.findViewById(R.id.keyWord);
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
                }
                else {
                    Toast.makeText(getContext(),"Sorry!No information containing the keyword ",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.i(TAG,"onCreateView:done");
        return root;
    }
}