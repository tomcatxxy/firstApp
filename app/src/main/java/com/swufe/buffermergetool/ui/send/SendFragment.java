package com.swufe.buffermergetool.ui.send;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.swufe.buffermergetool.R;

public class SendFragment extends Fragment implements View.OnClickListener{

    private SendViewModel sendViewModel;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        root = inflater.inflate(R.layout.fragment_send, container, false);
        Button submit=root.findViewById(R.id.suggestion_btn);
        submit.setOnClickListener(this);
        return root;
    }

    public void onClick(View v){
        String problem=((TextInputEditText)root.findViewById(R.id.suggestion_problem)).getText().toString();
        if(problem.length()>0){
            Intent email=new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:1561231137@qq.com"));
            email.putExtra(Intent.EXTRA_SUBJECT, "Swufer's info problem feedback");
            String contact=((TextInputEditText)root.findViewById(R.id.suggestion_contact)).getText().toString();
            String content;
            if(contact.length()>0){
                content=problem+"\n联系方式："+contact;
            }
            else {
                content=problem;
            }
            email.putExtra(Intent.EXTRA_TEXT, content);
            startActivity(email);
        }else {
            Toast.makeText(getContext(),"Please input the problem you want to feedback",Toast.LENGTH_SHORT).show();
        }
    }
}