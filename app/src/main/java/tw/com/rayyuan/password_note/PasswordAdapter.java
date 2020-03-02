package tw.com.rayyuan.password_note;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.*;

public class PasswordAdapter extends  RecyclerView.Adapter<PasswordAdapter.ViewHolder> {
    private ArrayList<Password_Data> mUsageItems;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public PasswordAdapter(ArrayList<Password_Data> items) {
        Log.d("aaaa",""+items.size());
        mUsageItems = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.password_cell, parent, false);
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.password_cell_header, parent, false);
        return viewType == 0 ? new ViewHolder(view,viewType) : new ViewHolder(view2,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.
//            Log.d("aaa",mUsageItems.get(position).appid);

//        holder.itemView.setTag(mUsageItems.get(position));

//        PersonUtils pu = personUtils.get(position);
        if(mUsageItems.get(position).is_Sectioned){
            holder.Title.setText(mUsageItems.get(position).account);
        }else{
        holder.itemView.setTag(mUsageItems.get(position));

        try {


            byte[]   data = Base64.decode(mUsageItems.get(position).appid.getBytes("UTF-8"), Base64.DEFAULT);
            holder.Title.setText(new String(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        try {
            byte[] data2 = Base64.decode(mUsageItems.get(position).account.getBytes("UTF-8"), Base64.DEFAULT);
            holder.Account.setText(new String(data2, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        }

    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return mUsageItems.get(position).is_Sectioned==false ? 0: 1;
    }

    @Override
    public int getItemCount() {
        return mUsageItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView Title;
        public TextView Account;

        public ViewHolder(final View itemView,int viewType) {
            super(itemView);
            if (viewType == 0) {
                Title = (TextView) itemView.findViewById(R.id.textView3);
                Account = (TextView) itemView.findViewById(R.id.textView4);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Password_Data cpu = (Password_Data) view.getTag();

                        try {


                            byte[] data = Base64.decode(cpu.password.getBytes("UTF-8"), Base64.DEFAULT);
                            ClipboardManager clipboard = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(new String(data, "UTF-8"));
//                        clipboard.setPrimaryClip(clip);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        try {


                            byte[] data = Base64.decode(cpu.appid.getBytes("UTF-8"), Base64.DEFAULT);
                            String password = new String(data, "UTF-8");
                            Toast.makeText(view.getContext(), "已經將" + password + "密碼複製到剪貼簿內", Toast.LENGTH_SHORT).show();
//                        clipboard.setText();
//                        clipboard.setPrimaryClip(clip);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("編輯");
                        ViewGroup viewGroup = v.findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog, viewGroup, false);
                        builder.setView(dialogView);
//                    builder.getContext().view
                        final EditText title = dialogView.findViewById(R.id.editText);
                        final EditText account = dialogView.findViewById(R.id.editText2);
                        final EditText password = dialogView.findViewById(R.id.editText3);
                        final Password_Data cpu = (Password_Data) v.getTag();

                        try {


                            byte[] data = Base64.decode(cpu.account.getBytes("UTF-8"), Base64.DEFAULT);
                            String t_account = new String(data, "UTF-8");
                            account.setText(t_account);
//                        clipboard.setText();
//                        clipboard.setPrimaryClip(clip);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                        try {


                            byte[] data = Base64.decode(cpu.password.getBytes("UTF-8"), Base64.DEFAULT);
                            String t_password = new String(data, "UTF-8");
                            password.setText(t_password);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                        try {


                            byte[] data = Base64.decode(cpu.appid.getBytes("UTF-8"), Base64.DEFAULT);
                            String t_appid = new String(data, "UTF-8");
                            title.setText(t_appid);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                        builder.setPositiveButton("送出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Map<String, Object> data = new HashMap<>();


                                try {


                                    byte[] adata = Base64.encode(account.getText().toString().getBytes("UTF-8"), Base64.DEFAULT);
                                    String t_appid = new String(adata, "UTF-8");
//                                title.setText(t_appid);
                                    data.put("account", t_appid);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }


                                try {


                                    byte[] adata2 = Base64.encode(password.getText().toString().getBytes("UTF-8"), Base64.DEFAULT);
                                    String t_password = new String(adata2, "UTF-8");
                                    data.put("password", t_password);
//                                title.setText(t_appid);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }


                                try {


                                    byte[] adata3 = Base64.encode(title.getText().toString().getBytes("UTF-8"), Base64.DEFAULT);
                                    String t_title = new String(adata3, "UTF-8");
                                    data.put("appid", t_title);
//                                title.setText(t_appid);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                db.collection("App_Password").document(currentUser.getEmail()).collection("data").document(cpu.documetid).update(data);
                            }
                        });
                        builder.setNeutralButton("刪除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("App_Password").document(currentUser.getEmail()).collection("data").document(cpu.documetid).delete();
                            }
                        });

                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                        return false;
                    }
                });
            }
            else{
                Title = (TextView) itemView.findViewById(R.id.textView);

            }
        }

    }

}