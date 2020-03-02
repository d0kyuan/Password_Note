package tw.com.rayyuan.password_note;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Password_List_Page extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    RecyclerView recyclerView;
    PasswordAdapter passwordAdapter;
    ArrayList<Password_Data> mydata = new ArrayList();
    ArrayList<String> sections = new ArrayList<>();
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_list);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


//        mydata.add(new Password_Data("abc","password","app2",1234,0));
//        mydata.add(new Password_Data("abc","password","app",1234,0));
        Log.d("sss",""+mydata.size());
        recyclerView   = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        sections.add("未分類");

        passwordAdapter  = new PasswordAdapter(mydata);
        recyclerView.setAdapter(passwordAdapter);



        recyclerView = findViewById(R.id.recyclerView);

        enableSwipeToDeleteAndUndo();


        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("App_Password").document(currentUser.getEmail()).collection("sections").orderBy("type", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sections.add(document.getData().get("name").toString());
                                Log.d("a", document.getId() + " => " + document.getData());
                            }



                            db.collection("App_Password").document(currentUser.getEmail()).collection("sections").orderBy("type", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshot,
                                                    @Nullable FirebaseFirestoreException e) {
                                    sections.clear();
                                    sections.add("未分類");
                                    long last_type = -1;
                                    for (QueryDocumentSnapshot doc : snapshot) {
                                     sections.add(doc.getString("name"));
                                    }
                                    for (Password_Data o:mydata
                                         ) {
                                        if(o.is_Sectioned){
                                            o.account = sections.get((int)o.type);
                                        }
                                    }
                                    passwordAdapter.notifyDataSetChanged();
                                }
                            });

                            db.collection("App_Password").document(currentUser.getEmail()).collection("data").orderBy("type", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshot,
                                                    @Nullable FirebaseFirestoreException e) {
                                    mydata.clear();
                                    long last_type = -1;
                                    for (QueryDocumentSnapshot doc : snapshot) {
                                        if((long)doc.get("type")>last_type){
                                            last_type = (long)doc.get("type");
                                            mydata.add(new Password_Data("",sections.get((int)last_type),"","",0 ,(int)last_type,true));
                                            mydata.add(new Password_Data(doc.getId(),doc.getString("account"),doc.getString("password"),doc.getString("appid"),(long)doc.get("dataId") ,(long)doc.get("type"),false));

                                        }else if((long)doc.get("type") == last_type){
                                            mydata.add(new Password_Data(doc.getId(),doc.getString("account"),doc.getString("password"),doc.getString("appid"),(long)doc.get("dataId") ,(long)doc.get("type"),false));
                                        }

                                    }
                                    passwordAdapter.notifyDataSetChanged();
                                }
                            });

                        } else {
                            Log.d("a", "Error getting documents: ", task.getException());
                        }
                    }
                });











    }


    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
//                final String item = passwordAdapter.getData().get(position);
//
                mydata.remove(position);
                passwordAdapter.notifyDataSetChanged();


            }
        };

//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
//        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
    int selete_type = 0;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_favorite:

//                wheelView.get
                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("編輯");
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(this).inflate(R.layout.add_data_dialog, viewGroup, false);
                builder.setView(dialogView);



                WheelView wheelView = dialogView.findViewById(R.id.wheelview);

                wheelView.setCyclic(false);
                final List<String> mOptionsItems = new ArrayList<>();
                for (String o :sections
                     ) {
                    mOptionsItems.add(o);
                }


                wheelView.setAdapter(new ArrayWheelAdapter(mOptionsItems));
                wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        selete_type = index;
//                        Toast.makeText(Password_List_Page.this, "" + mOptionsItems.get(index), Toast.LENGTH_SHORT).show();
                    }
                });




//                    builder.getContext().view
                final EditText title = dialogView.findViewById(R.id.editText);
                final EditText account = dialogView.findViewById(R.id.editText2);
                final EditText password = dialogView.findViewById(R.id.editText3);


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
                        Long tsLong = System.currentTimeMillis()/1000;
                        data.put("dataId",tsLong);
                        data.put("type",selete_type);
                        db.collection("App_Password").document(currentUser.getEmail()).collection("data").add(data);
                    }
                });


                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_menu, menu);
        return true;
    }
}
