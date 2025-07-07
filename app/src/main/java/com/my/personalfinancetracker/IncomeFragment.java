package com.my.personalfinancetracker;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.my.personalfinancetracker.Model.Data;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private RecyclerView recyclerView;
    private TextView income_total;
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;
    private Button update_btn;
    private Button delete_btn;
    private String type;
    private String note;
    private int amount;
    private String firebaseKey;
    private FirebaseRecyclerAdapter<Data,MyViewHolder> adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IncomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IncomeFragment newInstance(String param1, String param2) {
        IncomeFragment fragment = new IncomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_income, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        recyclerView = myview.findViewById(R.id.recycler_id_income);
        income_total = myview.findViewById(R.id.income_txt_result);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    total+= data.getAmount();
                    String stTotal = String.valueOf(total);
                    income_total.setText(stTotal+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();
       FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mIncomeDatabase, Data.class).setLifecycleOwner(this).build();
        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
              //  String firebaseKey = getRef(position).getKey();
              //  model.setId(firebaseKey);
               // Log.d("FIREBASE_DATA", "Date: " + model.getDate() + ", Type: " + model.getType());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmmount(model.getAmount());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseKey = getRef(position).getKey();
                        type = model.getType();
                        amount = model.getAmount();
                        note = model.getNote();
                        updateDataItem();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);
                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
      // adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_text_income);
            mType.setText(type);
        }

        private void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.note_text_income);
            mNote.setText(note);
        }

        private void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_text_income);
            mDate.setText(date);
        }

        private void setAmmount(int ammount) {
            TextView mAmmount = mView.findViewById(R.id.amount_text_income);
            String stammount = String.valueOf(ammount);
            mAmmount.setText(stammount);
        }
    }
    private void updateDataItem() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        dialog.setView(myview);

        edtAmount = myview.findViewById(R.id.ammount_edit);
        edtType = myview.findViewById(R.id.type_edit);
        edtNote = myview.findViewById(R.id.note_edit);

        edtNote.setText(note);
        edtNote.setSelection(note.length());
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());
        edtType.setText(type);
        edtType.setSelection(type.length());
        update_btn = myview.findViewById(R.id.btn_update);
        delete_btn = myview.findViewById(R.id.btn_delete);

        AlertDialog mydialog = dialog.create();
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();
                String nAmount = String.valueOf(amount);
                nAmount = edtAmount.getText().toString().trim();
                int iAmount = Integer.parseInt(nAmount);

                String nDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(nDate,firebaseKey,note,type,iAmount);
                mIncomeDatabase.child(firebaseKey).setValue(data);
                mydialog.dismiss();
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mIncomeDatabase.child(firebaseKey).removeValue();
                mydialog.dismiss();
            }
        });
        mydialog.show();
    }
}