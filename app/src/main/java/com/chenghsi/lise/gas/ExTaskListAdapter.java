package com.chenghsi.lise.gas;

import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chenghsi.lise.gas.task.NewTaskActivity;
import com.google.zxing.client.android.CaptureActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by MengHan on 2015/10/28.
 */
public class ExTaskListAdapter extends BaseExpandableListAdapter {

    private NewTaskActivity taskActivity;
    private List<StaffList> staffList;
    private ExpandableListView expListView;
    private List<Map<String, String>> childList;
    /*使用者id & name*/
    private static String userName ;
    private static String staff_id ;

    private String action = Intent.ACTION_CALL;

    private LayoutInflater inflater;

    public static int counter;
    public static List<ArrayList<TaskLists>> groupList;
    public static boolean isCollapse;

    //回傳api
    String up_order_id;
    String up_order_accept;
    String up_order_status;
    String up_doddle_id;
    String up_doddle_accept;
    String up_doddle_status;
    String up_degree;

    int flag;

    public ExTaskListAdapter(NewTaskActivity taskActivity, ExpandableListView expListView,
                             List<ArrayList<TaskLists>> groupList,
                             List<Map<String, String>> childList,
                             List<StaffList> staffList) {
        super();
        this.taskActivity = taskActivity;
        this.expListView = expListView;
        this.groupList = groupList;
        this.childList = childList;
        this.staffList = staffList;

        Log.e("extaskList", "userName:"+userName);
        Log.e("extaskList", "staff_id:"+staff_id);

        inflater = LayoutInflater.from(taskActivity);
        Globals g = new Globals();
        userName = g.getUser_name();
        staff_id = g.getUser_id();

    }
    //TODO 事後最好用api判斷是否為月結戶

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
        Button btn_finish;
        EditText edt_degree;

        public ChildViewHolder(View convertView) {
            btn_scanIn = (Button) convertView.findViewById(R.id.btn_scanIn);
            btn_scanOut = (Button) convertView.findViewById(R.id.btn_scanOut);
            btn_finish = (Button) convertView.findViewById(R.id.btn_finish);
            edt_degree = (EditText) convertView.findViewById(R.id.edt_degree);

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
//        Log.e("task", "group size:" + groupList.size());

        ArrayList<TaskLists> list = groupList.get(0);
//        Log.e("task", "getView call 次數：" + count);
        count++;
//        Log.e("task", "list size:" + list.size());
        final TaskLists taskLists = list.get(groupPosition);

        String add = _toAddress(taskLists.getCustomer_address());
        String cylinders = convertCylinders(taskLists.getOrder_cylinders_list());
        final String callPhone = taskLists.getOrder_phone();
//        Log.e("task", "phone" + callPhone);
        groupViewHolder.appointment.setText(taskLists.getOrder_prefer_time());
        groupViewHolder.kindOfTask.setText(taskLists.getOrder_task());
        groupViewHolder.clientName.setText(taskLists.getCustomer_name());
        groupViewHolder.address.setText(add);

        groupViewHolder.phones.setText(callPhone);
        groupViewHolder.money.setText("應付金額：" + taskLists.getOrder_should_money());
        if (taskLists.getOrder_task().equals("抄錶")) {
            groupViewHolder.contents.setVisibility(View.GONE);
            groupViewHolder.money.setVisibility(View.GONE);
        } else {
            groupViewHolder.contents.setText(cylinders);
        }
        groupViewHolder.btn_accept.setFocusable(false);

        /*按鈕文字初始化*/
        if (taskLists.getOrder_doddle_status().equals("")) {
//            taskLists.setOrder_doddle_status("");
            groupViewHolder.btn_accept.setText("承接");
            expListView.collapseGroup(groupPosition);
        } else if (taskLists.getOrder_doddle_status().equals("1") && taskLists.getOrder_doddle_accept().equals(staff_id)) {
            groupViewHolder.btn_accept.setText(userName);
            isCollapse = expListView.expandGroup(groupPosition);
        } else {
            taskLists.setOrder_doddle_status("1");
            for (int i=0;i<staffList.size(); i++){
                String staff_id = staffList.get(i).getStaff_id();
                if (staff_id.equals(taskLists.getOrder_doddle_accept()))
                    groupViewHolder.btn_accept.setText(staffList.get(i).getStaff_name());
            }
//            groupViewHolder.btn_accept.setText(taskLists.getOrder_doddle_accept());
            expListView.collapseGroup(groupPosition);
        }


        /*承接按鈕動作*/
        final GroupViewHolder finalGroupView = groupViewHolder;

        groupViewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!taskLists.getOrder_task().equals("抄錶")) {  //訂單承接動作
                    flag = 0;
                    if (taskLists.getOrder_doddle_status().equals("")) { //使用者承接
                        finalGroupView.btn_accept.setText(userName);
                        taskLists.setOrder_doddle_accept(staff_id);
                        taskLists.setOrder_doddle_status("1");
                        //展開expandable listView
                        isCollapse = expListView.expandGroup(groupPosition);
                        up_order_id = taskLists.getOrder_doddle_id();
                        up_order_accept = taskLists.getOrder_doddle_accept();
                        up_order_status = taskLists.getOrder_doddle_status();
                        Log.e("task", "承接：" + taskLists.getOrder_doddle_status());
                        new Update().start();
                    } else if (taskLists.getOrder_doddle_status().equals("1") &&
                            taskLists.getOrder_doddle_accept().equals(staff_id)) {
                        taskLists.setOrder_doddle_status("");
                        finalGroupView.btn_accept.setText("承接");
                        taskLists.setOrder_doddle_accept("");

                        up_order_id = taskLists.getOrder_doddle_id();
                        up_order_accept = taskLists.getOrder_doddle_accept();
                        up_order_status = taskLists.getOrder_doddle_status();

                        isCollapse = expListView.collapseGroup(groupPosition);
                        Log.e("task", "取消承接：" + taskLists.getOrder_doddle_status());
                        new Update().start();
                    } else {
                        Log.e("task", "已有人承接");
                        Log.e("task", "ELSE status:" + taskLists.getOrder_doddle_status());
                        Log.e("task", "ELSE accept:" + taskLists.getOrder_doddle_accept());
                    }
                }else { //抄表承接動作
                    flag = 1;
                    if (taskLists.getOrder_doddle_status().equals("")) { //使用者承接
                        finalGroupView.btn_accept.setText(userName);
                        taskLists.setOrder_doddle_accept(staff_id);
                        taskLists.setOrder_doddle_status("1");
                        //展開expandable listView
                        isCollapse = expListView.expandGroup(groupPosition);
                        up_doddle_id = taskLists.getOrder_doddle_id();
                        up_doddle_accept = taskLists.getOrder_doddle_accept();
                        up_doddle_status = taskLists.getOrder_doddle_status();
                        Log.e("task", "承接：" + taskLists.getOrder_doddle_status());
                        new Update().start();
                    } else if (taskLists.getOrder_doddle_status().equals("1") &&
                            taskLists.getOrder_doddle_accept().equals(staff_id)) {
                        taskLists.setOrder_doddle_status("");
                        finalGroupView.btn_accept.setText("承接");
                        taskLists.setOrder_doddle_accept("");

                        up_doddle_id = taskLists.getOrder_doddle_id();
                        up_doddle_accept = taskLists.getOrder_doddle_accept();
                        up_doddle_status = taskLists.getOrder_doddle_status();

                        isCollapse = expListView.collapseGroup(groupPosition);
                        Log.e("task", "取消承接：" + taskLists.getOrder_doddle_status());
                        new Update().start();
                    } else {
                        Log.e("task", "已有人承接");
                        Log.e("task", "ELSE status:" + taskLists.getOrder_doddle_status());
                        Log.e("task", "ELSE accept:" + taskLists.getOrder_doddle_accept());
                    }
                }
            }
        });


        //call phone
        groupViewHolder.img_btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callPhone == null || callPhone.equals("")) {
                    Toast.makeText(taskActivity.getApplicationContext(), "請新增客戶電話", Toast.LENGTH_SHORT).show();
                } else {
                    Uri uri = Uri.parse("tel:" + callPhone);
                    Intent intent = new Intent(action, uri);
                    taskActivity.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, final ViewGroup parent) {
        final ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_item_child_task, null);
            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        ArrayList<TaskLists> list = groupList.get(0);
        final TaskLists taskLists = list.get(groupPosition);
        if (taskLists.getOrder_task().equals("抄錶")) {
            childViewHolder.btn_scanIn.setVisibility(View.GONE);
            childViewHolder.btn_scanOut.setVisibility(View.GONE);
            childViewHolder.edt_degree.setVisibility(View.VISIBLE);
            childViewHolder.edt_degree.setInputType(InputType.TYPE_CLASS_NUMBER);
            childViewHolder.edt_degree.requestFocus();//让EditText获得焦点，但是获得焦点并不会自动弹出键盘

        } else {

            // 三個按鈕動作
            childViewHolder.btn_scanIn.setText(childList.get(childPosition).get("scanIn"));
            childViewHolder.btn_scanOut.setText(childList.get(childPosition).get("scanOut"));
            childViewHolder.btn_scanIn.setVisibility(View.VISIBLE);
            childViewHolder.btn_scanOut.setVisibility(View.VISIBLE);
            childViewHolder.edt_degree.setVisibility(View.GONE);

        }

        childViewHolder.btn_finish.setText(childList.get(childPosition).get("finish"));

        //TODO 掃入
        childViewHolder.btn_scanIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 2;
                Intent intent = new Intent();
                intent.setClass(taskActivity, CaptureActivity.class);
                intent.putExtra("flag", flag-1);
                taskActivity.startActivity(intent);
            }
        });

        //TODO 掃出
        childViewHolder.btn_scanOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 3;
                Intent intent = new Intent();
                intent.setClass(taskActivity, CaptureActivity.class);
                intent.putExtra("flag", flag-1);
                taskActivity.startActivity(intent);
            }
        });

        //結案
        childViewHolder.btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskLists.getOrder_task().equals("抄錶")){    //抄錶結案
                    flag = 4;
//                    taskLists.setOrder_doddle_status("2");
                    up_doddle_id = taskLists.getOrder_doddle_id();
                    up_degree = childViewHolder.edt_degree.getText().toString();
                    //TODO doddle
                    new Update().start();
                }else {
                    flag = 5;
//                    taskLists.setOrder_doddle_status("2");
                    up_order_id = taskLists.getOrder_doddle_id();
//                    up_order_accept = taskLists.getOrder_doddle_accept();
//                    up_order_status = taskLists.getOrder_doddle_status();
                    Log.e("task", "flag:"+flag+" status:"+up_order_status);
                }
                new Update().start();
                Log.e("tag", "結案：" + groupPosition);
                groupList.get(0).remove(groupPosition);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private class Update extends Thread {
        @Override
        public void run() {
            String url = "";
            switch (flag) {
                //訂單承接
                case 0:
                    url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_other.php?tb_name=order" +
                            "&tb_where_name=order_id&tb_where_val=" + up_order_id + "&tb_td=order_accept%7Corder_status&tb_val=" + up_order_accept + "%7C" + up_order_status;
                    Log.e("task", "order "+url);
                    break;
                //抄錶承接
                case 1:
                    url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_other.php?tb_name=doddle" +
                    "&tb_where_name=doddle_id&tb_where_val=" + up_doddle_id + "&tb_td=doddle_accept%7Cdoddle_status&tb_val=" + up_doddle_accept + "%7C" + up_doddle_status;
                    Log.e("task", "doddle 承接"+url);
                    break;
                //掃入
                case 2:
                    break;
                //掃出
                case 3:
                    break;
                //抄錶結案
                case 4:
                    url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_dod.php?" +
                            "doddle_id="+up_doddle_id+"&doddle_this_phase_degree="+up_degree;
                    break;
                //訂單結案
                case 5:
                    url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_del.php?order_id="+up_order_id;
                    break;
            }
            String retSrc;
            HttpGet httpget = new HttpGet(url);
            HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    retSrc = EntityUtils.toString(resEntity);
                    Log.e("retSrc", "完整資料：" + retSrc);
                } else {
                    retSrc = "Did not work!";
                    Log.e("retSrc", "完整資料：" + retSrc);
                }

            } catch (Exception e) {
                Log.e("retSrc", "讀取JSON Error...");
            } finally {

                httpclient.getConnectionManager().shutdown();
            }
        }
    }

    public String _toAddress(String address) {
        try {
            String[] addr_name = new String[]{"", "", "巷", "弄", "號", "樓", "室"};
            String temp = "";
            String[] address_arr = address.split("_");
//            Log.e("add", "addLength : " + address_arr.length);
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
