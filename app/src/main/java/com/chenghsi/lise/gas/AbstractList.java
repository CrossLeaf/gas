package com.chenghsi.lise.gas;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

import com.chenghsi.lise.gas.db.GasDB;
//import com.chenghsi.lise.gas.task.TaskActivity;


public abstract class AbstractList extends Activity
{
    protected ListView listView;
    protected Toolbar toolbar;
    protected SwipeRefreshLayout swipeRefreshLayout;
    static protected GasDB gasDB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abstract_list);

        listView = (ListView) findViewById(R.id.listView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        // Initializing GasDB
        gasDB = new GasDB();

        // Initializing toolbar
        toolbar.inflateMenu(R.menu.menu_main);

        // Initializing swipeRefreshLayout (the refreshing animation)
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        // Initializing listView
        listView.setOnScrollListener(onScrollListener);
    }

    // Refreshing when pulling down
    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            // Nothing
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {

            // Enable refreshing event if at the top of listView
            if(firstVisibleItem==0)
            {
                Log.e("refresh", "firstVisibleItem:"+firstVisibleItem);
                swipeRefreshLayout.setEnabled(true);
            }
            else
            {
                Log.e("refresh", "not firstVisibleItem:"+firstVisibleItem);
                swipeRefreshLayout.setEnabled(false);
            }
        }
    };

    // What to do if refreshing
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener()
    {
        @Override
        public void onRefresh()
        {
            swipeRefreshLayout.setRefreshing(true);

            // TODO something
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 3000);
        }
    };


}
