package com.somrandomteam.hacklondon2016.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.somrandomteam.hacklondon2016.R;

import java.util.ArrayList;

/**
 * Created by abhinavmishra on 27/02/2016.
 */
public class PersonAdapter extends BaseAdapter {

    Context personContext;
    ArrayList<Person> personList;

    public PersonAdapter(Context context, ArrayList<Person> people) {
        personList = people;
        personContext = context;
    }

    @Override
    public int getCount() {
        return personList.size();
    }

    @Override
    public Object getItem(int position) {
        return personList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PersonViewHolder holder;

        if (convertView == null) {
            LayoutInflater personInflater = (LayoutInflater) personContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = personInflater.inflate(R.layout.fragment_proximity, null);

            holder = new PersonViewHolder();

            holder.personView = (TextView) convertView.findViewById(R.id.proximity_id);

            convertView.setTag(holder);
        } else {

            holder = (PersonViewHolder) convertView.getTag();
        }

        Person person = (Person) getItem(position);

        holder.personView.setText(person.proximity_id);

        return convertView;

    }

    public void add(Person person) {
        personList.add(person);
        notifyDataSetChanged();
    }


    private static class PersonViewHolder {
        public TextView personView;
    }
}
