package com.chenghsi.lise.gas;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chenghsi.lise.gas.task.DetailedTaskActivity;
import com.chenghsi.lise.gas.task.NewTaskActivity;
import com.google.zxing.client.android.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by MengHan on 2015/10/28.
 */
public class ExTaskListAdapter extends BaseExpandableListAdapter {

    private NewTaskActivity taskActivity;
    private ExpandableListView expListView;
    private List<Map<String, String>> childList;
    private String userName = LoginActivity.usn;
    private String action = Intent.ACTION_CALL;

    private LayoutInflater inflater;

    public static int counter;
    public static List<ArrayList<TaskLists>> groupList;
    public static boolean isCollapse;

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
        TextView money;
        Button btn_accept;
        ImageButton img_btn_call;

        public GroupViewHolder(View convertView) {
            appointment = (TextView) convertView.findViewById(R.id.tv_appointment);
            kindOfTask = (TextView) convertView.findViewById(R.id.tv_kindOfTask);
            clientName = (TextView) convertView.findViewById(R.id.tv_clientName);
            address = (TextView) convertView.findViewById(R.id.tv_address);
            contents = (TextView) convertView.findViewById(R.id.tv_contents);
            phones = (TextView) convertView.findViewById(R.id.tv_phones);
            btn_accept = (Button) convertView.findViewById(R.id.btn_accept);
            money = (TextView) convertView.findViewById(R.id.tv_money);
            img_btn_call = (ImageButton) convertView.findViewById(R.id.img_btn_call);
        }
    }

    private class ChildViewHolder {
        Button btn_scanIn;
        Button btn_scanOut;
//        Button btn_setting;
        Button btn_finish;

        public ChildViewHolder(View convertView) {
            btn_scanIn = (Button) convertView.findViewById(R.id.btn_scanIn);
            btn_scanOut = (Button) convertView.findViewById(R.id.btn_scanOut);
            btn_finish = (Button) convertView.findViewById(R.id.btn_finish);
//            btn_setting = (Button) convertView.findViewById(R.id.btn_setting);
        }
    }

    int count = 1;

    @Override
    public int getGroupCount() {
//        return 50;
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
        counter = groupPosition;
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
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_item_task, parent, false);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        Log.e("task", "group size:" + groupList.size());

        ArrayList<TaskLists> list = groupList.get(0);
        Log.e("task", "getView call 次數：" + count);
        count++;
        Log.e("task", "list size:" + list.size());
        final TaskLists taskLists = list.get(groupPosition);

        String add = _toAddress(taskLists.getCustomer_addreess());
        String cylinders = convertCylinders(taskLists.getOrder_cylinders_list());
        final String callPhone = taskLists.getOrder_phone();
        groupViewHolder.appointment.setText(taskLists.getOrder_prefer_time());
        groupViewHolder.kindOfTask.setText(taskLists.getOrder_task());
        groupViewHolder.clientName.setText(taskLists.getCustomer_name());
        groupViewHolder.address.setText(add);
        groupViewHolder.contents.setText(cylinders);
        groupViewHolder.phones.setText(callPhone);
        groupViewHolder.money.setText("應付金額：" + taskLists.getOrder_should_money());
        groupViewHolder.btn_accept.setFocusable(false);
        //TODO 做按鈕點擊跳出子項目
        if (taskLists.getOrder_status().equals("") || taskLists.getOrder_status().equals("false")) {
            taskLists.setOrder_status("false");
            groupViewHolder.btn_accept.setText("承接");
        } else {
            taskLists.setOrder_status("true");
            groupViewHolder.btn_accept.setText(taskLists.getOrder_accept());
        }

        //承接按鈕動作
        final GroupViewHolder finalGroupView = groupViewHolder;
        groupViewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskLists.getOrder_status().equals("false")) {
                    finalGroupView.btn_accept.setText(userName);
                    taskLists.setOrder_accept(userName);
                    taskLists.setOrder_status("true");
                    //展開expandable listView
                    isCollapse = expListView.expandGroup(groupPosition);
                    Log.e("task", "button");
                } else if (taskLists.getOrder_status().equals("true") && taskLists.getOrder_accept().equals(userName)) {
                    finalGroupView.btn_accept.setText("承接");
                    taskLists.setOrder_accept("");
                    taskLists.setOrder_status("false");
                    isCollapse = expListView.collapseGroup(groupPosition);

                } else {

                }
            }
        });

        //call
        groupViewHolder.img_btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callPhone == null || callPhone.equals("")){

                }else {
                    Uri uri = Uri.parse("tel:" + callPhone);
                    Intent intent = new Intent(action, uri);
                    taskActivity.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_item_child_task, null);
            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        final ChildViewHolder finalChildViewHolder = childViewHolder;

        // 三個按鈕動作
        childViewHolder.btn_scanIn.setText(childList.get(childPosition).get("scanIn"));
        childViewHolder.btn_scanOut.setText(childList.get(childPosition).get("scanOut"));
//        childViewHolder.btn_setting.setText(childList.get(childPosition).get("setting"));
        childViewHolder.btn_finish.setText(childList.get(childPosition).get("finish"));

        //掃入
        childViewHolder.btn_scanIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(taskActivity, CaptureActivity.class);
                taskActivity.startActivity(intent);
            }
        });

        //掃出
        childViewHolder.btn_scanOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(taskActivity, CaptureActivity.class);
                taskActivity.startActivity(intent);
            }
        });

        /*原本的設定按鈕
        childViewHolder.btn_setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                ArrayList<TaskLists> list = groupList.get(0);
                TaskLists taskLists = list.get(groupPosition);

                //轉換字串
                String add = _toAddress(taskLists.getCustomer_addreess());
//                String cylinders = convertCylinders(taskLists.getOrder_cylinders_list());

                //傳值到細項Intent
                intent.setClass(taskActivity, DetailedTaskActivity.class);
                bundle.putString("appointment", taskLists.getOrder_prefer_time());
                bundle.putString("kindOfTask", taskLists.getOrder_task());
                bundle.putString("clientName", taskLists.getCustomer_name());
                bundle.putString("address", add);
                bundle.putString("contents", taskLists.getOrder_cylinders_list());
                bundle.putString("phones", taskLists.getOrder_phone());
                bundle.putString("customerId", taskLists.getCustomer_id());
                intent.putExtras(bundle);
                taskActivity.startActivity(intent);
            }
        });*/
        return convertView;
    }

    public String _toAddress(String address) {
        try {
            String[] addr_name = new String[]{"", "", "巷", "弄", "號", "樓", "室"};
            String temp = "";
            String[] address_arr = address.split("_");
            Log.e("add", "addLength : " + address_arr.length);
            for (int i = 0; i < address_arr.length; i++) {
                if (!address_arr[i].equals("") && address_arr[i] != null) {
                    temp += address_arr[i] + addr_name[i];
                }
            }
            return temp;
        } catch (Exception e) {
            return null;
        }

    }

    public String convertCylinders(String cylinders) {
        try {
            String[] cylinders_list = new String[]{"50KG", "20KG", "16KG", "4KG"};
            String temp = "";
            String[] cylinder = cylinders.split(",");
            for (int i = 0; i < cylinder.length; i++) {
                if (!cylinder[i].equals("0")) {
                    temp += cylinders_list[i] + "x" + cylinder[i] + " ";
                }
            }

            return temp;
        } catch (Exception e) {
            return null;
        }
    }

}
