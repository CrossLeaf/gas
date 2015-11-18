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
    private ExpandableListView expListView;
    private List<Map<String, String>> childList;
    private String userName = LoginActivity.usn;
    private String action = Intent.ACTION_CALL;

    private LayoutInflater inflater;

    public static int counter;
    public static List<ArrayList<TaskLists>> groupList;
    public static boolean isCollapse;

    String up_order_id;
    String up_order_accept;
    int flag;

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
        Log.e("task", "group size:" + groupList.size());

        ArrayList<TaskLists> list = groupList.get(0);
        Log.e("task", "getView call 次數：" + count);
        count++;
        Log.e("task", "list size:" + list.size());
        final TaskLists taskLists = list.get(groupPosition);

        String add = _toAddress(taskLists.getCustomer_address());
        String cylinders = convertCylinders(taskLists.getOrder_cylinders_list());
        final String callPhone = taskLists.getOrder_phone();
        Log.e("task", "phone" + callPhone);
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

        if (taskLists.getOrder_status().equals("") || taskLists.getOrder_status().equals("false")) {
            taskLists.setOrder_status("false");
            groupViewHolder.btn_accept.setText("承接");
        } else {
            taskLists.setOrder_status("true");
            groupViewHolder.btn_accept.setText(taskLists.getOrder_accept());
        }

        //TODO 承接按鈕動作
        final GroupViewHolder finalGroupView = groupViewHolder;
        groupViewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (taskLists.getOrder_accept().equals("承接")) { //使用者承接
                    finalGroupView.btn_accept.setText(userName);
                    taskLists.setOrder_accept(userName);

                    //展開expandable listView
                    isCollapse = expListView.expandGroup(groupPosition);
                    up_order_id = taskLists.getOrder_id();
                    up_order_accept = taskLists.getOrder_accept();
                    new Update().start();
                    Log.e("task", "button");
                } else if (taskLists.getOrder_accept().equals(userName)) {
                    finalGroupView.btn_accept.setText("承接");
                    taskLists.setOrder_accept("");
                    up_order_id = taskLists.getOrder_id();
                    up_order_accept = taskLists.getOrder_accept();

                    isCollapse = expListView.collapseGroup(groupPosition);

                } else {

                }
                flag = 0;
            }

            /*class AcceptUpdate extends Thread {
                @Override
                public void run() {
                    String url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_other.php?tb_name=order" +
                            "&tb_where_name=order_id&tb_where_val=" + up_order_id + "&tb_td=order_accept&tb_val=" + up_order_accept;
                    HttpGet httpget = new HttpGet(url);
                    HttpClient httpclient = new DefaultHttpClient();
                    Log.e("retSrc", "讀取 JSON-1...");
                    try {
                        HttpResponse response = httpclient.execute(httpget);
                        Log.e("retSrc", "讀取 JSON-2...");
                        HttpEntity resEntity = response.getEntity();
                        if (resEntity != null) {
                            String retSrc = EntityUtils.toString(resEntity);
                            Log.e("retSrc", retSrc);
                        } else {
//                            retSrc = "Did not work!";
                        }
                    } catch (Exception e) {
                        Log.e("retSrc", "讀取JSON Error...");
                    } finally {
                        httpclient.getConnectionManager().shutdown();
                    }
                }
            }*/
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
        TaskLists taskLists = list.get(groupPosition);
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
                Intent intent = new Intent();
                intent.setClass(taskActivity, CaptureActivity.class);
                taskActivity.startActivity(intent);
            }
        });

        //TODO 掃出
        childViewHolder.btn_scanOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(taskActivity, CaptureActivity.class);
                taskActivity.startActivity(intent);
            }
        });

        //TODO 結案要考慮 order&doddle
        childViewHolder.btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("tag", "結案：" + groupPosition);
                groupList.get(0).remove(groupPosition);
                notifyDataSetChanged();
            }
        });

        //TODO 設定輸入瓦斯度數


        return convertView;
    }

    private class Update extends Thread {
        @Override
        public void run() {
            String url = "";
            switch (flag) {
                case 0:
                    url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_other.php?tb_name=order" +
                            "&tb_where_name=order_id&tb_where_val=" + up_order_id + "&tb_td=order_accept&tb_val=" + up_order_accept;
                    break;
                case 1:
//                    url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_other.php?tb_name=order" +
//                            "&tb_where_name=order_id&tb_where_val=" + order_id + "&tb_td=order_status&tb_val=" + order_status;
                    break;
            }
            String retSrc;
            //doddle url
            //order url

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
