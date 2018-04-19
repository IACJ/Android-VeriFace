package com.scut.veriface.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scut.veriface.R;

import java.util.List;

/**
 * Created by kidd on 2017/12/21.
 */

public class PersonAdapter extends ArrayAdapter<Person> {
    private int resourceId;

    public PersonAdapter(Context context, int textViewResourceId, List<Person> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        Person person = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView personImage = (ImageView)view.findViewById(R.id.person_image);
        TextView personUid = (TextView)view.findViewById(R.id.person_uid);
        personImage.setImageResource(person.getImageId());
        personUid.setText(person.getClass_id()+" - "+person.getUser_info());
        return view;
    }
}
