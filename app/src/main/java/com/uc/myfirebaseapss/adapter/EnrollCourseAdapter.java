package com.uc.myfirebaseapss.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.myfirebaseapss.R;
import com.uc.myfirebaseapss.model.Course;

import java.util.ArrayList;


public class EnrollCourseAdapter extends RecyclerView.Adapter<EnrollCourseAdapter.CardViewViewHolder> {


    private DatabaseReference mDatabase;
    FirebaseDatabase dbEnroll;


    private Context context;
    private ArrayList<Course> listCourse;

    private ArrayList<Course> getListCourse() {
        return listCourse;
    }

    public void setListCourse(ArrayList<Course> listCourse) {
        this.listCourse = listCourse;
    }

    public EnrollCourseAdapter(final Context context) {
        this.context = context;
    }



    DatabaseReference dbCourse = FirebaseDatabase.getInstance().getReference("course");

    @NonNull
    @Override
    public CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.enroll_course_adapter, parent, false);
        return new CardViewViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final CardViewViewHolder holder, int position) {
        final Course course = getListCourse().get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        holder.lbl_subject.setText(course.getSubject());
        holder.lbl_day.setText(course.getDay());
        holder.lbl_start.setText(course.getStart());
        holder.lbl_end.setText(course.getEnd());
        holder.lbl_lecturer.setText(course.getLecturer());

        holder.btn_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mDatabase.child("student").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("course").child(course.getId()).setValue(course);
                cekBentrokan(course);
            }
        });


    }

    @Override
    public int getItemCount() {
        return getListCourse().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder {

        TextView lbl_subject, lbl_start, lbl_end, lbl_day, lbl_lecturer;
        Button btn_enroll;

        CardViewViewHolder(View itemView) {
            super(itemView);

            lbl_subject = itemView.findViewById(R.id.lbl_subject_enroll_course_adp);
            lbl_day = itemView.findViewById(R.id.lbl_day_enroll_course_adp);
            lbl_start = itemView.findViewById(R.id.lbl_start_enroll_course_adp);
            lbl_end = itemView.findViewById(R.id.lbl_end_enroll_course_adp);
            lbl_lecturer = itemView.findViewById(R.id.lbl_lecturer_enroll_course_adp);

            btn_enroll = itemView.findViewById(R.id.btn_enroll);

        }
    }

    MutableLiveData<Course> courseToAdd = new MutableLiveData<>();

    public MutableLiveData<Course> getCourseToAdd(){
        return courseToAdd;
    }

    boolean conflict = false;

    public void cekBentrokan(final Course chosenCourse) {

        final int  chosenCourseStartInt = Integer.parseInt(chosenCourse.getStart().replace(":",""));
        final int chosenCourseEndInt = Integer.parseInt(chosenCourse.getEnd().replace(":",""));

        Log.d("chosenCourseStartInt",chosenCourseStartInt+"");
        Log.d("chosenCourseEndInt",chosenCourseEndInt+"");

        FirebaseDatabase.getInstance()// membaca data
                .getReference("student")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("course")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conflict = false;
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    Course course = childSnapshot.getValue(Course.class);
                    int courseStartInt = Integer.parseInt(course.getStart().replace(":",""));
                    int courseEndInt = Integer.parseInt(course.getEnd().replace(":",""));

                    Log.d("CourseStartInt",courseStartInt+"");
                    Log.d("CourseEndInt",courseEndInt+"");

                    if (!chosenCourse.getDay().equalsIgnoreCase(course.getDay()) || courseStartInt>=chosenCourseEndInt || courseEndInt<=chosenCourseStartInt){
                        conflict = false;
                        Log.d("CheckFalse","False");
                    }else{
                        conflict = true;
                        Log.d("CheckTrue","True");
                        break;
                    }
                }

                Log.d("Check",conflict+"");



                if (conflict){
                    Toast.makeText(context, "Course Conflict!", Toast.LENGTH_SHORT).show();
                } else {
                    courseToAdd.setValue(chosenCourse);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}   

