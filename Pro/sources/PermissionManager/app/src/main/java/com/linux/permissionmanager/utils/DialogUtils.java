package com.linux.permissionmanager.utils;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class DialogUtils {
    public static void showCustomDialog(Context context, String title, String message,
                                        Drawable icon,
                                        String positiveButtonText, DialogInterface.OnClickListener positiveClickListener,
                                        String negativeButtonText, DialogInterface.OnClickListener negativeClickListener) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false);

        if (icon != null) {
            builder.setIcon(icon);
        }

        if (positiveButtonText != null && positiveClickListener != null) {
            builder.setPositiveButton(positiveButtonText, positiveClickListener);
        }
        if (negativeButtonText != null && negativeClickListener != null) {
            builder.setNegativeButton(negativeButtonText, negativeClickListener);
        }
        builder.show();
    }

    public static void showNeedPermissionDialog(Context context) {
        DialogUtils.showCustomDialog(
                context, "权限申请", "请授予权限后重新操作", null, "确定",
                (dialog, which) -> dialog.dismiss(),
                null, null
        );
    }

    /**
     * 显示带有消息的对话框。
     *
     * @param context 上下文
     * @param title   对话框标题
     * @param msg     对话框内容
     * @param icon    对话框图标（可为 null）
     */
    public static void showMsgDlg(Context context, String title, String msg, Drawable icon) {
        showCustomDialog(
                context,
                title,
                msg,
                icon,
                "确定", (dialog, which) -> dialog.dismiss(),
                null, null
        );
    }

    /**
     * 显示带有三个按钮的输入对话框。
     *
     * @param context           上下文
     * @param defaultText       默认文本
     * @param title             对话框标题
     * @param thirdButtonText   第三个按钮的文本
     * @param confirmCallback   点击确定按钮时的回调
     * @param thirdButtonCallback 第三个按钮的回调
     */
    public static void showInputDlg(Context context, String defaultText, String title, final String thirdButtonText,
                                    final Handler confirmCallback, final Handler thirdButtonCallback) {
        
        TextInputLayout textInputLayout = new TextInputLayout(context);
        textInputLayout.setPadding(40, 20, 40, 0);
        textInputLayout.setHint("输入内容");
        
        final TextInputEditText inputTxt = new TextInputEditText(context);
        inputTxt.setText(defaultText);
        inputTxt.setFocusable(true);
        if (defaultText != null) {
            inputTxt.setSelection(defaultText.length());
        }
        textInputLayout.addView(inputTxt);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(textInputLayout)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String text = inputTxt.getText().toString();
                        Message msg = new Message();
                        msg.obj = text;
                        confirmCallback.sendMessage(msg);
                    }
                });

        // 添加第三个按钮
        if (thirdButtonText != null && !thirdButtonText.isEmpty()) {
            builder.setNeutralButton(thirdButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 自定义回调
                    if (thirdButtonCallback != null) {
                        thirdButtonCallback.sendMessage(new Message());
                    }
                }
            });
        }

        builder.show();
    }

    public static void showLogDialog(Context context, String logs) {
        // 使用 MaterialAlertDialogBuilder 创建日志对话框
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("执行日志");

        // 创建一个外部的线性布局（垂直方向）
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        // 创建 TextView 作为日志显示区域
        TextView textView = new TextView(context);
        textView.setTextSize(13);
        textView.setText(logs);
        textView.setTextIsSelectable(true); // 允许选中复制
        textView.setVerticalScrollBarEnabled(true);
        textView.setSingleLine(false); // 允许多行显示
        textView.setMaxLines(Integer.MAX_VALUE); // 让其支持无限行
        textView.setLineSpacing(1.2f, 1.1f); // 增加行间距，增强可读性
        textView.setPadding(10, 10, 10, 10);

        // ScrollView 使日志可以滚动
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(textView);
        
        // 设置 ScrollView 的高度，避免对话框过大或过小
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                800 // 固定高度或者根据需要调整
        );
        scrollView.setLayoutParams(scrollLp);

        // 让 ScrollView 自动滚动到底部
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        });

        layout.addView(scrollView);
        builder.setView(layout);

        builder.setPositiveButton("复制", (dialog, which) -> {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("logs", logs));
                Toast.makeText(context, "日志已复制到剪贴板", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("关闭", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

}
