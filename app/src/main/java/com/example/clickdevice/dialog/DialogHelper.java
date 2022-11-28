package com.example.clickdevice.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickdevice.MyApp;
import com.example.clickdevice.R;
import com.example.clickdevice.adapter.ScriptAdapter;
import com.example.clickdevice.adapter.ScriptListAdapter;
import com.example.clickdevice.db.ScriptDataBean;
import com.example.clickdevice.db.ScriptListBean;
import com.example.clickdevice.db.ScriptListDao;
import com.example.clickdevice.helper.KtxKt;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.ArrayList;
import java.util.List;

public class DialogHelper {
    private static int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    public static void showMenuDialog(final String[] items, Activity activity, final DialogInterface.OnClickListener callBack) {

        new QMUIDialog.MenuDialogBuilder(activity)
//                .addItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(activity, "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    }
//                })
                .addItems(items,callBack)
                .show();

    }

    public static void showMessagePositiveDialog(Activity activity,String title,String msg,QMUIDialogAction.ActionListener listener) {
        new QMUIDialog.MessageDialogBuilder(activity)
                .setTitle(title)
                .setMessage(msg)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE,listener)
                .show();
    }


    public static Dialog DeleteDialogShow(Context context, String title, String content, View.OnClickListener confirmListener){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null);
        final Dialog dialog = new AlertDialog.Builder(context, R.style.MyDialog).setView(view).create();
        TextView text_title = view.findViewById(R.id.txt_title_dialog_delete);
        TextView text_content = view.findViewById(R.id.txt_content_dialog_delete);
        Button quxiao = view.findViewById(R.id.dialog_btn_delete_no);
        Button queding = view.findViewById(R.id.dialog_btn_delete);
        text_title.setText(title);
        text_content.setText(content);
        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        queding.setOnClickListener(confirmListener);
        dialog.show();
        //需要先显示再设置大小
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();//获取屏幕分辨率
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        params.width = (int) (0.7 * screenWidth);
        window.setAttributes(params);
        return dialog;
    }

    public static Dialog RecordListDialogShow(Context context, View.OnClickListener confirmListener){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_record_list, null);
        final Dialog dialog = new AlertDialog.Builder(context, R.style.MyDialog).setView(view).create();
        TextView text_cancle = view.findViewById(R.id.tv_btn_cancle);
        TextView text_test = view.findViewById(R.id.tv_btn_test);
        RecyclerView recyclerView = view.findViewById(R.id.rv_record_list);
        text_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
       ScriptListDao scriptListDao =MyApp.getAppDatabase().getScriptListDao();

        List<ScriptListBean> mData = scriptListDao.loadAllScriptDataBean();
        mData.add(new ScriptListBean("测试1","", KtxKt.toDate(System.currentTimeMillis()),"",-1));
        mData.add(new ScriptListBean());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        ScriptListAdapter scriptAdapter = new ScriptListAdapter(mData, context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(scriptAdapter);
        scriptAdapter.setClickListener(new ScriptListAdapter.ClickListener() {
            @Override
            public void delete(ScriptListBean scriptDataBean) {

            }
            @Override
            public void edit(ScriptListBean scriptDataBean) {

            }
            @Override
            public void forTimeEdit(ScriptListBean scriptDataBean) {

            }
        });
        text_test.setOnClickListener(confirmListener);
        dialog.show();
        //需要先显示再设置大小
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();//获取屏幕分辨率
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        params.width = (int) (0.7 * screenWidth);
        window.setAttributes(params);
        return dialog;
    }
}
