package com.my.personalfinancetracker;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
 * Use the {@link DashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    private TextView fab_income_text;
    private TextView fab_expense_text;
    private boolean isOpen = false;
    private Animation FadeOpen, FadeClose;
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private TextView totalIncomeResult;
    private TextView totalexpenseResult;

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;
    private FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter;
    private FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashBoardFragment newInstance(String param1, String param2) {
        DashBoardFragment fragment = new DashBoardFragment();
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
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
//connecting floating button to layout
        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_ft_btn);

        //connecting floating text
        fab_income_text = myview.findViewById(R.id.income_ft_text);
        fab_expense_text = myview.findViewById(R.id.expense_ft_text);

        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        totalexpenseResult = myview.findViewById(R.id.expense_set_result);

        mRecyclerExpense = myview.findViewById(R.id.recycler_expense);
        mRecyclerIncome = myview.findViewById(R.id.recycler_income);

        FadeOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose =AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if(isOpen){
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_text.startAnimation(FadeClose);
                    fab_expense_text.startAnimation(FadeClose);
                    fab_expense_text.setClickable(false);
                    fab_income_text.setClickable(false);
                    isOpen = false;
                }else{
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_text.startAnimation(FadeOpen);
                    fab_expense_text.startAnimation(FadeOpen);
                    fab_expense_text.setClickable(true);
                    fab_income_text.setClickable(true);
                    isOpen = true;
                }
            }
        });
        //calculate total income
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int sum =0;
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    if(data != null) {
                        sum += data.getAmount();
                    }
                }
                String stSum = String.valueOf(sum);
                totalIncomeResult.setText(stSum+".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int eSum =0;
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    if(data != null){
                        eSum += data.getAmount();
                    }
                }
                String st_eSum = String.valueOf(eSum);
                totalexpenseResult.setText(st_eSum+".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);
        //new PagerSnapHelper().attachToRecyclerView(mRecyclerIncome);
        //mRecyclerIncome.setNestedScrollingEnabled(true);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;
    }
//floating button animation
    private void ftAnimation(){
        if(isOpen){
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_text.startAnimation(FadeClose);
            fab_expense_text.startAnimation(FadeClose);
            fab_expense_text.setClickable(false);
            fab_income_text.setClickable(false);
            isOpen = false;
        }else{
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_text.startAnimation(FadeOpen);
            fab_expense_text.startAnimation(FadeOpen);
            fab_expense_text.setClickable(true);
            fab_income_text.setClickable(true);
            isOpen = true;
        }
    }
    private  void addData(){

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }

    public void incomeDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View myviewm = layoutInflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myviewm);

        AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);

        EditText editAmount = myviewm.findViewById(R.id.ammount_edit);
        EditText editType = myviewm.findViewById(R.id.type_edit);
        EditText editNote = myviewm.findViewById(R.id.note_edit);
        Button btnSave = myviewm.findViewById(R.id.btn_save);
        Button btnCancel = myviewm.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    editType.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(amount)) {
                    editAmount.setError("Required Field");
                    return;
                }
                int amountint = Integer.parseInt(amount);
                if(TextUtils.isEmpty(note)){
                    editNote.setError("Required Field");
                    return;
                }
                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(mDate,id,note,type,amountint);

                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"Data ADDED",Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void expenseDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);

        AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);
        EditText ammount = myview.findViewById(R.id.ammount_edit);
        EditText type = myview.findViewById(R.id.type_edit);
        EditText note = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btn_save);
        Button btnCancel =myview.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmAmmount = ammount.getText().toString().trim();
                String tmtype = type.getText().toString().trim();
                String tmnote = note.getText().toString().trim();

                if(TextUtils.isEmpty(tmAmmount)){
                    ammount.setError("Required Field");
                    return;
                }
                int inamount = Integer.parseInt(tmAmmount);
                if(TextUtils.isEmpty(tmtype)){
                    type.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(tmnote)){
                    note.setError("Required Field");
                    return;
                }
                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(mDate,id,tmnote,tmtype,inamount);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data added", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> income_options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mIncomeDatabase,Data.class).setLifecycleOwner(this).build();
        incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(income_options) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
             holder.setmIncomeType(model.getType());
             holder.setmIncomeDate(model.getDate());
             holder.setmIncomeAmount(model.getAmount());
            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income,parent, false);

                return new IncomeViewHolder(view);
            }
        };

        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();
        FirebaseRecyclerOptions<Data> expense_options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mExpenseDatabase,Data.class).setLifecycleOwner(this).build();
        expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(expense_options) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {
               holder.setmExpenseType(model.getType() != null ? model.getType() : "");
               holder.setmExpenseAmount(model.getAmount());
               holder.setmExpenseDate(model.getDate() != null ? model.getDate() : "");
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense,parent,false);

                return new ExpenseViewHolder(view);
            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();
    }
//    public void onStop(){
//        super.onStop();
//        if (incomeAdapter != null) {
//            incomeAdapter.stopListening();
//        }
//        if (incomeAdapter != null){
//            expenseAdapter.stopListening();
//        }
//    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeview;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeview = itemView;
        }

        public void setmIncomeType(String type){
            TextView mtype = mIncomeview.findViewById(R.id.type_income_ds);
            mtype.setText(type);
        }
        public void setmIncomeAmount(int amount){
            TextView mAmount = mIncomeview.findViewById(R.id.amount_income_ds);
            String stAmount = String.valueOf(amount);
            mAmount.setText(stAmount);
        }

        public void setmIncomeDate(String date){
            TextView mDate = mIncomeview.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }
    }
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseview;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseview = itemView;
        }
        public void setmExpenseType(String type){
            TextView mtype = mExpenseview.findViewById(R.id.type_expense_ds);
            mtype.setText(type);
        }
        public void setmExpenseDate(String date){
            TextView mDate = mExpenseview.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
        public void setmExpenseAmount(int amount){
            TextView mAmount = mExpenseview.findViewById(R.id.amount_expense_ds);
            String stAmount = String.valueOf(amount);
            mAmount.setText(stAmount);
        }
    }
}