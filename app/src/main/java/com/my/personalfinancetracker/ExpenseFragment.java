package com.my.personalfinancetracker;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.personalfinancetracker.Model.Data;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    private RecyclerView recyclerView;
    private  TextView expense_total;
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;
    private Button update_btn;
    private Button delete_btn;
    private String type;
    private String note;
    private int amount;
    private String firebaseKey;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
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
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);
       mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        expense_total = myview.findViewById(R.id.expense_txt_result);
        recyclerView = myview.findViewById(R.id.recycler_id_expense);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int sum =0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    sum += data.getAmount();
                    String stSum = String.valueOf(sum);
                    expense_total.setText(stSum+".00");
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

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mExpenseDatabase, Data.class).setLifecycleOwner(this).build();
        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setType(model.getType());
                holder.setDate(model.getDate());
                holder.setNote(model.getNote());
                holder.setAmount(model.getAmount());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseKey = getRef(position).getKey();
                        amount= model.getAmount();
                        type = model.getType();
                        note = model.getNote();
                        updateDataItem();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
              View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data,parent,false);

                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        private void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_text_expense);
            mDate.setText(date);
        }
        private void setType(String type){
            TextView mType = mView.findViewById(R.id.type_text_expense);
            mType.setText(type);
        }
        private void setNote(String note){
            TextView mNote = mView.findViewById(R.id.note_text_expense);
            mNote.setText(note);
        }
        private void setAmount(int amount){
            TextView mAmount = mView.findViewById(R.id.amount_text_expense);
            String st_amount = String.valueOf(amount);
            mAmount.setText(st_amount);
        }
    }
    private void updateDataItem(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item,null);
        dialog.setView(myview);

        edtAmount = myview.findViewById(R.id.ammount_edit);
        edtType = myview.findViewById(R.id.type_edit);
        edtNote = myview.findViewById(R.id.note_edit);

        edtNote.setText(note);
        edtNote.setSelection(note.length());
        edtType.setText(type);
        edtType.setSelection(type.length());
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());
        update_btn = myview.findViewById(R.id.btn_update);
        delete_btn = myview.findViewById(R.id.btn_delete);

        AlertDialog mydialog = dialog.create();
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();
                String eAmount = String.valueOf(amount);
                eAmount = edtAmount.getText().toString().trim();
                int iAmount = Integer.parseInt(eAmount);
                String eDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(eDate, firebaseKey,note,type,iAmount);
                mExpenseDatabase.child(firebaseKey).setValue(data);
                mydialog.dismiss();
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpenseDatabase.child(firebaseKey).removeValue();
                mydialog.dismiss();
            }
        });
        mydialog.show();
    }
}