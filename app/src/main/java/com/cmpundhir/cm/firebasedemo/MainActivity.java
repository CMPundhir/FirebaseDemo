package com.cmpundhir.cm.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpundhir.cm.firebasedemo.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    Button btn;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    ImageButton imgBtn;
    Uri outPutfileUri;
    Bitmap bitmap=null;
    List<Message> messagesList = new ArrayList<>();
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        imgBtn = findViewById(R.id.imgBtn);
        editText = findViewById(R.id.editText);
        btn = findViewById(R.id.button2);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyAdapter adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = database.getReference("message");
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, PICK_FROM_GALLARY);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString();
                if(TextUtils.isEmpty(msg)){
                    editText.setError("Please enter your message.");
                    return;
                }
                Message message = new Message();
                message.setMessage(msg);
                SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                String format = s.format(new Date());
                message.setTimeStamp(format);
                message.setUserId(firebaseUser.getEmail());
                myRef.push().setValue(message);
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("messages",dataSnapshot.getValue().toString());
                    Message message = dataSnapshot.getValue(Message.class);
                    messagesList.add(message);
                    adapter.notifyDataSetChanged();
                    recyclerView.getLayoutManager().scrollToPosition(messagesList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.message_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Message message = messagesList.get(position);
            Log.d("current emails","firebaseUser.getEmail() : "+firebaseUser.getEmail()+" ,  message.getUserId() : "+message.getUserId());
            if(message.getUserId().trim().equals(firebaseUser.getEmail().trim())){
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//                params.gravity = Gravity.END;
//                holder.ll1.setLayoutParams(new FrameLayout.LayoutParams(params));
                holder.userId2.setText(message.getUserId());
                holder.msg2.setText(message.getMessage());
                holder.tm2.setText(message.getTimeStamp());
                holder.ll1.setVisibility(View.VISIBLE);
                holder.ll2.setVisibility(View.GONE);
            }else{
                holder.userId.setText(message.getUserId());
                holder.msg.setText(message.getMessage());
                holder.tm.setText(message.getTimeStamp());
                holder.ll1.setVisibility(View.GONE);
                holder.ll2.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return messagesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView userId,msg,tm,userId2,msg2,tm2;
            CardView ll1,ll2;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                userId = itemView.findViewById(R.id.userId);
                msg = itemView.findViewById(R.id.msg);
                tm = itemView.findViewById(R.id.tm);
                ll1 = itemView.findViewById(R.id.ll1);
                userId2 = itemView.findViewById(R.id.userId2);
                msg2 = itemView.findViewById(R.id.msg2);
                tm2 = itemView.findViewById(R.id.tm2);
                ll2 = itemView.findViewById(R.id.ll2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    //pic coming from camera

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), outPutfileUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_FROM_GALLARY:
                if (resultCode == Activity.RESULT_OK) {
                    //pick image from gallery
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    // Get the cursor
                    Cursor cursor = MainActivity.this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    bitmap = BitmapFactory.decodeFile(imgDecodableString);
                }
                break;
        }
    }
}
