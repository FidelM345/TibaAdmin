
package com.example.nikeadmin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    public static  String TAG="thebeast";
    PieChart donorsPiechart;
    BarChart donorsBarChart;
    BarChart usersBarChart;

    FirebaseAuth mAuth;

    Button postForum;

    TextView totals;

    String[] dataObjects =new String[] {"Jan","Feb","Mar","Apri","May","June","JUly"};
    float d_Total,d_Current,n_Current,n_Total=0;

    float bgA,bgB,bgAB,bgO;


    float riftValley,coast,nortEast,nyanza,central,eastern,western,nairobi;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        donorsPiechart=findViewById(R.id.donors_chart);
        donorsBarChart=findViewById(R.id.donors_chart1);
        postForum=findViewById(R.id.forum_post);

        totals=findViewById(R.id.mark);
        usersBarChart=findViewById(R.id.user_distro);


        mAuth=FirebaseAuth.getInstance();

        postForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(MainActivity.this,PostForum.class);
                startActivity(intent);

            }
        });

        donorsPiechart.animateX(3000);
        donorsPiechart.animateY(3000);


        donorsBarChart.animateX(3000);
        donorsBarChart.animateY(3000);



        usersBarChart.animateX(3000);
        usersBarChart.animateY(3000);



        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();



        bloodA();
        bloodB();
        bloodAB();
        bloodO();


        userDistro();




        db.collection("user_table")
                .whereEqualTo("donor", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "total added " + snapshots.size());

                                    d_Total=snapshots.size();
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "total removed " + snapshots.size());

                                    d_Total=snapshots.size();
                                    break;
                            }
                        }


                    }
                });




        db.collection("user_table")
                .whereEqualTo("donor", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "added " + snapshots.size());


                                    n_Total=snapshots.size();

                                    pieChartMex(n_Total,d_Total);

                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "removed " + snapshots.size());

                                    n_Total=snapshots.size();

                                    pieChartMex(n_Total,d_Total);
                                    break;
                            }
                        }


                    }
                });


    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()==null){
            Intent intent=new Intent(MainActivity.this,Login.class);
            startActivity(intent);
        }


    }



    private void userDistro() {

        db.collection("user_table")
                .whereEqualTo("location", "Nairobi")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group O" + snapshots.size());
                                    nairobi=snapshots.size();

                                    barGraphLocation();
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group O1" + snapshots.size());
                                    nairobi=snapshots.size();
                                    barGraphLocation();
                                    break;
                            }
                        }


                    }
                });





        db.collection("user_table")
                .whereEqualTo("location", "Rift valley")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group O" + snapshots.size());
                                    riftValley=snapshots.size();

                                    barGraphLocation();
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group O1" + snapshots.size());
                                    riftValley=snapshots.size();
                                    barGraphLocation();
                                    break;
                            }
                        }


                    }
                });






        db.collection("user_table")
                .whereEqualTo("location", "Central")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group O" + snapshots.size());
                                    central=snapshots.size();

                                    barGraphLocation();
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group O1" + snapshots.size());
                                    central=snapshots.size();
                                    barGraphLocation();
                                    break;
                            }
                        }


                    }
                });



        db.collection("user_table")
                .whereEqualTo("location", "Nyanza")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group O" + snapshots.size());
                                    nyanza=snapshots.size();

                                    barGraphLocation();
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group O1" + snapshots.size());
                                    nyanza=snapshots.size();
                                    barGraphLocation();
                                    break;
                            }
                        }


                    }
                });




        db.collection("user_table")
                .whereEqualTo("location", "Western")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group O" + snapshots.size());
                                    western=snapshots.size();

                                    barGraphLocation();
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group O1" + snapshots.size());
                                    western=snapshots.size();
                                    barGraphLocation();
                                    break;
                            }
                        }


                    }
                });




        db.collection("user_table")
                .whereEqualTo("location", "Eastern")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group O" + snapshots.size());
                                    eastern=snapshots.size();

                                    barGraphLocation();
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group O1" + snapshots.size());
                                    eastern=snapshots.size();
                                    barGraphLocation();
                                    break;
                            }
                        }


                    }
                });




        db.collection("user_table")
                .whereEqualTo("location", "Coasy")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group O" + snapshots.size());
                                    coast=snapshots.size();

                                    barGraphLocation();
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group O1" + snapshots.size());
                                    coast=snapshots.size();
                                    barGraphLocation();
                                    break;
                            }
                        }


                    }
                });



        db.collection("user_table")
                .whereEqualTo("location", "North Eastern")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group O" + snapshots.size());
                                    nortEast=snapshots.size();

                                    barGraphLocation();
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group O1" + snapshots.size());
                                    nortEast=snapshots.size();
                                    barGraphLocation();
                                    break;
                            }
                        }


                    }
                });



    }









    private void bloodO() {

        db.collection("user_table")
                .whereEqualTo("donor", true)
                .whereEqualTo("blood_group", "O")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group O" + snapshots.size());
                                    bgO=snapshots.size();

                                    barGraph();
                                    break;


                                case MODIFIED:

                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group O1" + snapshots.size());
                                    bgO=snapshots.size();
                                    barGraph();
                                    break;
                            }
                        }


                    }
                });
    }


    private void bloodAB() {
        db.collection("user_table")
                .whereEqualTo("donor", true)
                .whereEqualTo("blood_group", "AB")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group AB" + snapshots.size());
                                    bgAB=snapshots.size();

                                    barGraph();
                                    break;


                                case MODIFIED:

                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group AB1" + snapshots.size());
                                    bgAB=snapshots.size();
                                    barGraph();
                                    break;
                            }
                        }


                    }
                });
    }


    private void bloodB() {
        db.collection("user_table")
                .whereEqualTo("donor", true)
                .whereEqualTo("blood_group", "B")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group B" + snapshots.size());
                                    bgB=snapshots.size();

                                    barGraph();
                                    break;


                                case MODIFIED:

                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group B1" + snapshots.size());
                                    bgB=snapshots.size();
                                    barGraph();
                                    break;
                            }
                        }


                    }
                });
    }


    public void bloodA(){
        db.collection("user_table")
                .whereEqualTo("donor", true)
                .whereEqualTo("blood_group", "A")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    Log.d("monster", "bloog_group A" + snapshots.size());
                                    bgA=snapshots.size();

                                    barGraph();
                                    break;


                                case MODIFIED:

                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;


                                case REMOVED:

                                    Log.d("monster", "bloog_group A1" + snapshots.size());
                                    bgA=snapshots.size();
                                    barGraph();
                                    break;
                            }
                        }


                    }
                });

    }




    public void barGraphLocation(){



        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, nairobi,"Nairobi"));
        entries.add(new BarEntry(1f, coast,"Coast"));
        entries.add(new BarEntry(2f, nyanza,"Nyanza"));
        entries.add(new BarEntry(3f,eastern ,"Eastern"));
        entries.add(new BarEntry(4f,central ,"Central"));
        entries.add(new BarEntry(5f,nortEast ,"North Eastern"));
        entries.add(new BarEntry(6f, riftValley,"Rift Valley"));
        entries.add(new BarEntry(7f, western,"Western"));



        BarDataSet set = new BarDataSet(entries, "BarDataSet");

        set.setColors(ColorTemplate.MATERIAL_COLORS);



        set.setValueFormatter(new DefaultValueFormatter(5) {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                //rather than diaplaying value show label
                return entry.getData().toString();
            }
        });



        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        usersBarChart.setData(data);
        usersBarChart.setFitBars(true); // make the x-axis fit exactly all bars
        usersBarChart.invalidate(); // refresh



    }






    public void barGraph(){



        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, bgA,"Blood A"));
        entries.add(new BarEntry(1f, bgB,"Blood B"));
        entries.add(new BarEntry(2f, bgAB,"Blood AB"));
        entries.add(new BarEntry(3f, bgO,"Blood O"));


        BarDataSet set = new BarDataSet(entries, "BarDataSet");

        set.setColors(ColorTemplate.MATERIAL_COLORS);



        set.setValueFormatter(new DefaultValueFormatter(5) {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                //rather than diaplaying value show label
                return entry.getData().toString();
            }
        });



        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        donorsBarChart.setData(data);
        donorsBarChart.setFitBars(true); // make the x-axis fit exactly all bars
        donorsBarChart.invalidate(); // refresh



    }


    public void pieChartMex(float a, float b){

        float sum=a+b;

        int man =(int)sum;
        totals.setText(""+man);
        Log.d(TAG, "pieChartMex: "+"donor="+a+" non_donor="+b);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(a, "Donor"));
        entries.add(new PieEntry(b, "Non Donor"));
        PieDataSet set = new PieDataSet(entries, "Donors distribution");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);
        donorsPiechart.setData(data);

        donorsPiechart.invalidate(); // refresh


    }


}
