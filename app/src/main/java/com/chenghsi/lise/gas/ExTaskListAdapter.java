package com.chenghsi.lise.gas;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.chenghsi.lise.gas.task.NewTaskActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by MengHan on 2015/10/28.
 */
public class ExTaskListAdapter extends BaseExpandableListAdapter {

    private NewTaskActivity taskActivity;
    private ExpandableListView expListView;
    private List<ArrayList<TaskLists>> groupList;
    private List<Map<String, String>> childList;

    private LayoutInflater inflater;

    public ExTaskListAdapter(NewTaskActivity taskActivity, ExpandableListView expListView,
                             List<ArrayList<TaskLists>> groupList,
                             List<Map<String, String>> childList) {
        super();
        this.taskActivity = taskActivity;
        this.expListView = expListView;
        this.groupList = groupList;
        this.childList = childList;

        inflater = LayoutInflater.from(taskActivity);

    }

    private class GroupViewHolder {
        TextView appointment;
        TextView kindOfTask;
        TextView clientName;
        TextView address;
        TextView contents;
        TextView phones;
        Button btn_accept;

        public GroupViewHolder(View convertView) {
            appointment = (TextView) convertView.findViewById(R.id.tv_appointment);
            kindOfTask = (TextView) convertView.findViewById(R.id.tv_kindOfTask);
            clientName = (TextView) convertView.findViewById(R.id.tv_clientName);
            address = (TextView) convertView.findViewById(R.id.tv_address);
            contents = (TextView) convertView.findViewById(R.id.tv_contents);
            phones = (TextView) convertView.findViewById(R.id.tv_phones);
            btn_accept = (Button) convertView.findViewById(R.id.btn_accept);

        }
    }

    private class ChildViewHolder {
        Button btn_scanIn;
        Button btn_scanOut;
        Button btn_setting;
        Button btn_finish;

        public ChildViewHolder(View convertView) {
            btn_scanIn = (Button) convertView.findViewById(R.id.btn_scanIn);
            btn_scanOut = (Button) convertView.findViewById(R.id.btn_scanOut);
            btn_finish = (Button) convertView.findViewById(R.id.btn_finish);
            btn_setting = (Button) convertView.findViewById(R.id.btn_setting);
        }
    }

    int count = 1;

    @Override
    public int getGroupCount() {
        return groupList.get(0).size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(0).get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_item_task, null);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        //// TODO: 2015/10/28 取得資料後更改文字
        Log.e("task", "group size:" + groupList.size());

        ArrayList<TaskLists> list = groupList.get(0);
        Log.e("task", "getView call 次數：" + count);
        count++;
        Log.e("task", "list size:" + list.size());
            TaskLists taskLists = list.get(groupPosition);
            Log.e("task", taskLists.getCustomer_addreess());
            groupViewHolder.appointment.setText(taskLists.getOrder_prefer_time());
            groupViewHolder.kindOfTask.setText(taskLists.getOrder_task());
            groupViewHolder.clientName.setText(taskLists.getCustomer_name());
            groupViewHolder.address.setText(taskLists.getCustomer_addreess());
            groupViewHolder.contents.setText(taskLists.getOrder_cylinders_list());
            groupViewHolder.phones.setText(taskLists.getOrder_phone());


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_item_child_task, null);
            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();

        }
        //TODO 三個按鈕動作
        childViewHolder.btn_scanIn.setText(childList.get(childPosition).get("scanIn"));
        childViewHolder.btn_scanOut.setText(childList.get(childPosition).get("scanOut"));
        childViewHolder.btn_setting.setText(childList.get(childPosition).get("setting"));
        childViewHolder.btn_finish.setText(childList.get(childPosition).get("finish"));
        return convertView;
    }
    /*function data_in_show(address){
        var address_arr = address.split("_");
        var temp = "";
        var addr_name = ["","","巷","弄","號","樓","室"];
        for(var i=0;i<8;i++){
            if(address_arr[i] != "" && address_arr[i] != null)temp+=address_arr[i]+addr_name[i];
        }
        return temp;
    }*/


}
