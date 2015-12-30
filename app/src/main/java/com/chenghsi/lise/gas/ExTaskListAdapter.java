package com.chenghsi.lise.gas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenghsi.lise.gas.task.DoddleDialog;
import com.chenghsi.lise.gas.task.NewTaskActivity;
import com.google.zxing.client.android.CaptureActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
//import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MengHan on 2015/10/28.
 */
public class ExTaskListAdapter extends BaseExpandableListAdapter {

    private SharedPreferences sp;
    private NewTaskActivity taskActivity;
    private List<StaffList> staffList;
    private ExpandableListView expListView;
    public static List<ArrayList<TaskLists>> groupList;
    private List<Map<String, String>> childList;
    private List<String> edt_list;

    /*使用者id & name*/
    private static String userName;
    private static String userId;
    //撥打電話action
    private String action = Intent.ACTION_CALL;

    private LayoutInflater inflater;

    public static int counter;
    public static boolean isCollapse;

    //回傳api
    String up_order_id;
    String up_order_accept;
    String up_order_status;
    String up_doddle_id;
    String up_doddle_accept;
    String up_doddle_status;
    String up_degree;
    String carcyln_content;

    int flag;
    int count = 1;
    boolean hasFocus = false;


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

        Globals globals = new Globals();
        String staff_id = globals.getUser_id();
        String staff_name = globals.getUser_name();

        inflater = LayoutInflater.from(taskActivity);
        sp = taskActivity.getSharedPreferences("LoginInfo", 0);
        userName = sp.getString("staff_name", staff_name);
        userId = sp.getString("staff_id", staff_id);

        Log.e("extask", "userName:" + userName);
        Log.e("extask", "userId:" + userId);

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
        ImageView imageView;

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
            imageView = (ImageView) convertView.findViewById(R.id.imageView);
        }
    }

    private class ChildViewHolder {

        Button btn_scanIn;
        Button btn_scanOut;
        Button btn_finish;
        Button btn_doddle;

        public ChildViewHolder(View convertView, final int groupPosition) {
            btn_scanIn = (Button) convertView.findViewById(R.id.btn_scanIn);
            btn_scanOut = (Button) convertView.findViewById(R.id.btn_scanOut);
            btn_finish = (Button) convertView.findViewById(R.id.btn_finish);
            btn_doddle = (Button) convertView.findViewById(R.id.btn_doddle);


        }
    }


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
            Log.e("extask", "getView call 次數：" + count);
            count++;
        }
//        Log.e("extask", "group size:" + groupList.size());

        ArrayList<TaskLists> list = groupList.get(0);
//        Log.e("extask", "list size:" + list.size());
        final TaskLists taskLists = list.get(groupPosition);

        String add = _toAddress(taskLists.getCustomer_address());
        String cylinders = convertCylinders(taskLists.getOrder_cylinders_list());
        final String callPhone = taskLists.getOrder_phone();
//        Log.e("extask", "phone" + callPhone);
        groupViewHolder.appointment.setText(taskLists.getOrder_prefer_time());
        groupViewHolder.kindOfTask.setText(taskLists.getOrder_task());
        groupViewHolder.clientName.setText(taskLists.getCustomer_name());
        groupViewHolder.address.setText(add);
        if (callPhone.equals("") ){
            groupViewHolder.img_btn_call.setVisibility(View.GONE);
        }
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
        if (taskLists.getOrder_doddle_status().equals("")) {    //為承接
//            taskLists.setOrder_doddle_status("");
            groupViewHolder.imageView.setImageResource(R.drawable.circle);
            groupViewHolder.btn_accept.setText("承接");
            expListView.collapseGroup(groupPosition);
        } else if (taskLists.getOrder_doddle_status().equals("1") && taskLists.getOrder_doddle_accept().equals(userId)) {
            //是承接者自己換至最頂


            groupViewHolder.imageView.setImageResource(R.drawable.red);
            groupViewHolder.btn_accept.setText(userName);
            isCollapse = expListView.expandGroup(groupPosition);
        } else {
            taskLists.setOrder_doddle_status("1");
            groupViewHolder.imageView.setImageResource(R.drawable.red);
            for (int i = 0; i < staffList.size(); i++) {
                String staff_id = staffList.get(i).getStaff_id();
                if (staff_id.equals(taskLists.getOrder_doddle_accept()))
                    groupViewHolder.btn_accept.setText(staffList.get(i).getStaff_name());
            }
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
                        taskLists.setOrder_doddle_accept(userId);
                        taskLists.setOrder_doddle_status("1");
                        //展開expandable listView
                        isCollapse = expListView.expandGroup(groupPosition);
                        up_order_id = taskLists.getOrder_doddle_id();
                        up_order_accept = taskLists.getOrder_doddle_accept();
                        up_order_status = taskLists.getOrder_doddle_status();
                        Log.e("exTask", "承接：" + taskLists.getOrder_doddle_status());
                        new Update().start();
                    } else if (taskLists.getOrder_doddle_status().equals("1") &&
                            taskLists.getOrder_doddle_accept().equals(userId)) {
                        taskLists.setOrder_doddle_status("");
                        finalGroupView.btn_accept.setText("承接");
                        taskLists.setOrder_doddle_accept("");

                        up_order_id = taskLists.getOrder_doddle_id();
                        up_order_accept = taskLists.getOrder_doddle_accept();
                        up_order_status = taskLists.getOrder_doddle_status();

                        isCollapse = expListView.collapseGroup(groupPosition);
                        Log.e("extask", "取消承接：" + taskLists.getOrder_doddle_status());
                        new Update().start();
                    } else {
                        Log.e("extask", "已有人承接");
                        Log.e("extask", "ELSE status:" + taskLists.getOrder_doddle_status());
                        Log.e("extask", "ELSE accept:" + taskLists.getOrder_doddle_accept());
                    }
                } else { //抄表承接動作
                    flag = 1;
                    if (taskLists.getOrder_doddle_status().equals("")) { //使用者承接
                        finalGroupView.btn_accept.setText(userName);
                        taskLists.setOrder_doddle_accept(userId);
                        taskLists.setOrder_doddle_status("1");
                        //展開expandable listView
                        isCollapse = expListView.expandGroup(groupPosition);
                        up_doddle_id = taskLists.getOrder_doddle_id();
                        up_doddle_accept = taskLists.getOrder_doddle_accept();
                        up_doddle_status = taskLists.getOrder_doddle_status();
                        Log.e("extask", "承接：" + taskLists.getOrder_doddle_status());
                        new Update().start();
                    } else if (taskLists.getOrder_doddle_status().equals("1") &&
                            taskLists.getOrder_doddle_accept().equals(userId)) {
                        taskLists.setOrder_doddle_status("");
                        finalGroupView.btn_accept.setText("承接");
                        taskLists.setOrder_doddle_accept("");

                        up_doddle_id = taskLists.getOrder_doddle_id();
                        up_doddle_accept = taskLists.getOrder_doddle_accept();
                        up_doddle_status = taskLists.getOrder_doddle_status();

                        isCollapse = expListView.collapseGroup(groupPosition);
                        Log.e("extask", "取消承接：" + taskLists.getOrder_doddle_status());
                        new Update().start();
                    } else {
                        Log.e("extask", "已有人承接");
                        Log.e("extask", "ELSE status:" + taskLists.getOrder_doddle_status());
                        Log.e("extask", "ELSE accept:" + taskLists.getOrder_doddle_accept());
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
        ArrayList<TaskLists> list = groupList.get(0);
        final TaskLists taskLists = list.get(groupPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_item_child_task, null);
            childViewHolder = new ChildViewHolder(convertView, groupPosition);
//            childViewHolder.btn_doddle.addTextChangedListener(new addTextChangedListener(cus_id));
            convertView.setTag(childViewHolder);

        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }


        Log.e("extask", "Group Count:" + getGroupCount());

        if (taskLists.getOrder_task().equals("抄錶")) {
            //抄錶的子項目
            childViewHolder.btn_scanIn.setVisibility(View.GONE);
            childViewHolder.btn_scanOut.setVisibility(View.GONE);
            childViewHolder.btn_finish.setVisibility(View.GONE);
            childViewHolder.btn_doddle.setVisibility(View.VISIBLE);
            childViewHolder.btn_doddle.setText(childList.get(childPosition).get("doddle"));
        } else {
            //設定子項目的文字和可見度
            childViewHolder.btn_scanIn.setText(childList.get(childPosition).get("scanIn"));
            childViewHolder.btn_scanOut.setText(childList.get(childPosition).get("scanOut"));
            childViewHolder.btn_finish.setText(childList.get(childPosition).get("finish"));
            childViewHolder.btn_scanIn.setVisibility(View.VISIBLE);
            childViewHolder.btn_scanOut.setVisibility(View.VISIBLE);
            childViewHolder.btn_doddle.setVisibility(View.GONE);
            childViewHolder.btn_finish.setVisibility(View.VISIBLE);
        }

        /*掃入監聽事件*/
        childViewHolder.btn_scanIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 2;
                Intent intent = new Intent();
                intent.setClass(taskActivity, CaptureActivity.class);
                intent.putExtra("flag", flag + 2);
                intent.putExtra("car_id", userId);
                Log.e("flag", String.valueOf(flag + 2));
                taskActivity.startActivity(intent);
            }
        });

        /*掃出監聽事件*/
        childViewHolder.btn_scanOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String customer_id = taskLists.getCustomer_id();
                flag = 3;
                Intent intent = new Intent();
                intent.setClass(taskActivity, CaptureActivity.class);
                intent.putExtra("flag", flag + 2);
                intent.putExtra("customer_id", customer_id);
                Log.e("flag", String.valueOf(flag + 2));
                taskActivity.startActivity(intent);
            }
        });

        /*抄錶監聽事件*/
        childViewHolder.btn_doddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up_doddle_id = taskLists.getOrder_doddle_id();
                String customer_id = taskLists.getCustomer_id();
                Intent intent = new Intent(taskActivity, DoddleDialog.class);
                //傳prev_degree
                intent.putExtra("doddle_id", up_doddle_id);
                intent.putExtra("customer_id", customer_id);
                taskActivity.startActivity(intent);
            }
        });

        /*結案監聽事件*/
        childViewHolder.btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = 5;
                up_order_id = taskLists.getOrder_doddle_id();
                String car_content = taskLists.getCarcyln_content();
                String[] car_content_list = car_content.split(",");
                String[] up_cylinders_list = taskLists.getOrder_cylinders_list().split(",");
                carcyln_content = convertAndCountCylinders(car_content_list, up_cylinders_list);
                Log.e("extask", "flag:" + flag + " status:" + carcyln_content);

                new Update().start();
                Log.e("extask", "結案：" + groupPosition);
                groupList.get(0).remove(groupPosition);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private class Update extends Thread {
        String url[];

        @Override
        public void run() {
            HttpClient httpclient = null;
            try {
                switch (flag) {
                    //訂單承接
                    case 0:
                        url = new String[1];
                        url[0] = "http://198.245.55.221:8089/ProjectGAPP/php/upd_other.php?tb_name=order" +
                                "&tb_where_name=order_id&tb_where_val=" + up_order_id + "&tb_td=order_accept%7Corder_status&tb_val=" + up_order_accept + "%7C" + up_order_status;
                        break;
                    //抄錶承接
                    case 1:
                        url = new String[1];
                        url[0] = "http://198.245.55.221:8089/ProjectGAPP/php/upd_other.php?tb_name=doddle" +
                                "&tb_where_name=doddle_id&tb_where_val=" + up_doddle_id + "&tb_td=doddle_accept%7Cdoddle_status&tb_val=" + up_doddle_accept + "%7C" + up_doddle_status;
                        break;
                /*//掃入
                case 2:
                    break;
                //掃出
                case 3:
                    break;*/

                    /*//抄錶結案
                    case 4:
                        url = new String[1];
                        url[0] = "http://198.245.55.221:8089/ProjectGAPP/php/upd_dod.php?" +
                                "doddle_id=" + up_doddle_id + "&doddle_this_phase_degree=" + up_degree;
                        break;*/
                    //訂單結案
                    case 5:
                        url = new String[2];
                        url[0] = "http://198.245.55.221:8089/ProjectGAPP/php/upd_del.php?order_id=" + up_order_id;
                        url[1] = "http://198.245.55.221:8089/ProjectGAPP/php/upd_car.php?car_id=" + NewTaskActivity.user_id + "&carcyln_content=" + carcyln_content;
                        break;
                    default:
                        break;
                }

                String retSrc;
                HttpGet httpget;
                httpclient = new DefaultHttpClient();
                HttpResponse response;
                for (String urls : url) {
                    httpget = new HttpGet(urls);
                    response = httpclient.execute(httpget);
                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {
                        retSrc = EntityUtils.toString(resEntity);
                        Log.e("retSrc", "完整資料：" + retSrc);
                    } else {
                        retSrc = "Did not work!";
                        Log.e("retSrc", "完整資料：" + retSrc);
                    }
                }
            } catch (Exception e) {
                Log.e("retSrc", "讀取JSON Error...");
            } finally {
                assert httpclient != null;
                httpclient.getConnectionManager().shutdown();
            }
        }
    }

    public String _toAddress(String address) {
        try {
            String[] addr_name = new String[]{"", "", "巷", "弄", "號", "樓", "室"};
            String temp = "";
            String[] address_arr = address.split("_");
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

    public String convertAndCountCylinders(String[] car_content_list, String[] up_cylinders_list) {
        int[] int_car_list = new int[4];
        int[] int_cylinders_list = new int[4];
        int[] count = new int[4];
        for (int i = 0; i < up_cylinders_list.length; i++) {
            int_car_list[i] = Integer.parseInt(car_content_list[i]);
            int_cylinders_list[i] = Integer.parseInt(up_cylinders_list[i]);
            count[i] = int_car_list[i] - int_cylinders_list[i];
            if (i == 0) {
                carcyln_content = String.valueOf(count[0]);
            } else {
                carcyln_content += "," + count[i];
            }

        }

        return carcyln_content;
    }

//    private class addTextChangedListener implements TextWatcher {
//        ChildViewHolder holder = null;
//        String cus_id;
//        public addTextChangedListener(ChildViewHolder holder , String cus_id) {
//            this.holder = holder;
//            this.cus_id = cus_id;
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            Log.e("extask", "onTextChanged:"+charSequence);
//            doddleHash.put(cus_id, charSequence.toString());
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//            Log.e("extask", "text cus_id:" + cus_id);
//            Log.e("extask", "text :" + editable.toString());
////            doddleHash.put(cus_id, editable.toString());
////            int position = (int) holder.btn_doddle.getTag();
////            doddleHash.put(position, editable.toString().trim());
//
//        }
//    }
}
