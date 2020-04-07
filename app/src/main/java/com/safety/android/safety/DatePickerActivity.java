package com.safety.android.safety;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.safety.android.util.DatePickerFragment;

import java.util.Date;

/**
 * Created by WangJing on 2017/6/1.
 */

public class DatePickerActivity extends SingleFragmentActivity {
    private static final String ARG_DATE="date";

    public static DatePickerFragment newInstance(Date date){
        Bundle args=new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerFragment fragment=new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected Fragment createFragment() {
        return null;
    }
}
