package com.example.wgu.c196termscheduler;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ExpandListViews {

    public static void listViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            //empty, no need to adjust height
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += (listItem.getMeasuredHeight()/listAdapter.getCount());
            //System.out.println(listItem.getMeasuredHeight() + " " + totalHeight + " " + desiredWidth + " " + listItem.getHeight());
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()));
        //System.out.println(params.height + " " + totalHeight + " " + listView.getDividerHeight() + " " + listAdapter.getCount());
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
