package com.filote.cosmin1.metrorex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.filote.cosmin1.metrorex.Adapter.AbonamentAdapter;
import com.filote.cosmin1.metrorex.Adapter.CalatorieAdapter;
import com.filote.cosmin1.metrorex.Adapter.CreditAdapter;
import com.filote.cosmin1.metrorex.Model.UserInformation;
import com.filote.cosmin1.metrorex.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.instacart.library.truetime.TrueTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BillingProcessor.IBillingHandler {
    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private Button buttonLogout;

    //Database
    private DatabaseReference databaseReference;
    private TextView nameText;
    private TextView numberText;
    private Button buttonScaneaza;
    private TextView textViewCalatorii;
    private TextView textViewCredit;
    private TextView textViewAbonament;
    private Button buttonCumparaCredit;
    private ListView listViewCredit;
    private ListView listViewCalatorie;
    private ListView listViewAbonament;
    private List<Integer> listCredit;
    private List<Integer> listCalatorie;
    private List<Integer> listAbonament;
    private ImageView imageViewCloseButton;
    private Button buttonCalatorie;
    private Button buttonAbonament;
    private int calatorieNew;
    private int abonamentNew;
    private ActionBarDrawerToggle toggle;
    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMenu);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");

        /** facebook and billingProcessor initializers*/
        bp = new BillingProcessor(this, null, this);

        FacebookSdk.sdkInitialize(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            //user not logged in

            startActivity(new Intent(this, LoginRegister.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        textViewCalatorii = (TextView) findViewById(R.id.tvcalatorii);
        textViewCredit = (TextView) findViewById(R.id.tvcredit);
        textViewAbonament = (TextView) findViewById(R.id.tvabonament);


        textViewUserEmail = (TextView) findViewById(R.id.tvprofile);
        textViewUserEmail.setText("Welcome " + firebaseAuth.getCurrentUser().getEmail().toString().trim() + " !");

        /** In OnCreate it checks the database and corrects the data in the database.
         * It also gathers the data about the user and show it in app*/
        checkAbonament();


        databaseReference.
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                            if (firebaseAuth.getCurrentUser().getUid().toString().trim().equals(postSnapShot.getKey().toString().trim())) {
                                UserInformation userInformation = postSnapShot.getValue(UserInformation.class);
                                textViewCalatorii.setText(textViewCalatorii.getText().toString().trim() + userInformation.getNumarCalatorii());
                                textViewCredit.setText(textViewCredit.getText().toString().trim() + userInformation.getCredit());
                                textViewAbonament.setText(textViewAbonament.getText().toString().trim() + userInformation.getTipAbonament());
                                if (userInformation.getTipAbonament().equals("Activ")) {
                                    SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");

                                    textViewAbonament.setText(textViewAbonament.getText().toString().trim() + "\n" +
                                            "Expira la : " + fmtOut.format(userInformation.getExpirareAbonament()));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        System.out.println("The read failed: " + databaseError.getMessage());

                    }
                });

        /** lists for Credit,Calatorie and Abonament */

        listCredit = new ArrayList<>();
        listCredit.add(5);
        listCredit.add(10);
        listCredit.add(20);
        listCredit.add(40);
        listCredit.add(100);


        listCalatorie = new ArrayList<>();
        listCalatorie.add(1);
        listCalatorie.add(2);
        listCalatorie.add(10);
        listCalatorie.add(20);
        listCalatorie.add(50);
        listCalatorie.add(100);

        listAbonament = new ArrayList<>();
        listAbonament.add(1);
        listAbonament.add(10);
        listAbonament.add(30);
        listAbonament.add(90);
        listAbonament.add(180);
        listAbonament.add(365);

        listViewCredit = (ListView) findViewById(R.id.lv_credit);
        listViewCalatorie = (ListView) findViewById(R.id.lv_credit);
        listViewAbonament = (ListView) findViewById(R.id.lv_credit);

        checkAbonament();


    }

    /**
     * checks if the abonament is still valid.It gets the correct time from TruTime.I used an api for this.
     */

    private void checkAbonament() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final Date dataExpirareAbonament = dataSnapshot.child("expirareAbonament").getValue(Date.class);
                        if (dataExpirareAbonament != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        TrueTime.build().initialize();
                                        Date date = TrueTime.now();
                                        if (date.after(dataExpirareAbonament)) {
                                            databaseReference.child(dataSnapshot.getKey()).child("tipAbonament").setValue("Expirat");
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    /**
     * show the data from database correct .Without this method the output will look like :
     * Calatorie: 34 if we had 3 calatori and added one more.Corect output should be 4.
     */


    private void showUpdatedInfo() {
        String S = textViewCredit.getText().toString().replaceAll("[0-9]+", "");
        textViewCredit.setText(S);
        S = textViewCalatorii.getText().toString().replaceAll("[0-9]+", "");
        textViewCalatorii.setText(S);
        S = textViewAbonament.getText().toString().replaceAll("[a-zA-Z]+", "");
        textViewAbonament.setText("Abonament:");
    }

    /**
     * This method is used to buy Credit .When CumparaCredit is pressed a listView with credits is shown.
     * With the setOnItemClickListener if we press an item it will initialize the billingProcessor and
     * purchase the credit.
     */

    private void cumparaCredit() {
        listViewCalatorie.setVisibility(View.GONE);
        listViewAbonament.setVisibility(View.GONE);


        listViewCredit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //bp.purchase(Profile.this,listViewCredit.getItemAtPosition(position).toString());


                //Toast.makeText(Profile.this, "You added "+listViewCredit.getItemAtPosition(position).toString()+" credit", Toast.LENGTH_SHORT).show();
                int creditNew = (Integer) listViewCredit.getItemAtPosition(position);
                bp.initialize();
                bp.purchase(Profile.this, "credit_" + creditNew);
                bp.consumePurchase("credit_" + creditNew);


            }
        });


        CreditAdapter creditAdapter = new CreditAdapter(listCredit, Profile.this);
        listViewCredit.setAdapter(creditAdapter);
        listViewCredit.setVisibility(View.VISIBLE);


    }

    /**
     * This method is used to buy journeys. If we have enough credits we can buy the amount we desire.
     */

    private void cumparaCalatorie() {
        listViewAbonament.setVisibility(View.GONE);
        listViewCredit.setVisibility(View.GONE);

        listViewCalatorie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                calatorieNew = (Integer) listViewCalatorie.getItemAtPosition(position);
                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int calatorie = dataSnapshot.child("numarCalatorii").getValue(Integer.class);
                                int credit = dataSnapshot.child("credit").getValue(Integer.class);
                                int calatorieNou = calatorie + calatorieNew;
                                int creditNou = credit - 2 * calatorieNew;
                                if (creditNou >= 0) {
                                    showUpdatedInfo();
                                    UserInformation userInformation = new UserInformation(dataSnapshot.getValue(UserInformation.class));
                                    userInformation.setCredit(creditNou);
                                    userInformation.setNumarCalatorii(calatorieNou);
                                    databaseReference.child(dataSnapshot.getKey()).setValue(userInformation);
                                    Toast.makeText(Profile.this, "You added " + calatorieNew + " calatorii", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Profile.this, "You don't have enough credit", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
        listViewCredit.setVisibility(View.GONE);

        CalatorieAdapter calatorieAdapter = new CalatorieAdapter(listCalatorie, Profile.this);
        listViewCalatorie.setAdapter(calatorieAdapter);
        listViewCalatorie.setVisibility(View.VISIBLE);


    }

    /**
     * This method is used to buy subscriptions. If we have enough credits we can buy the amount we desire.
     */

    private void cumparaAbonament() {
        listViewCalatorie.setVisibility(View.GONE);
        listViewCredit.setVisibility(View.GONE);


        listViewAbonament.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                abonamentNew = (Integer) listViewAbonament.getItemAtPosition(position);
                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child(firebaseAuth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {

                                final Date dataExpirareAbonament = dataSnapshot.child("expirareAbonament").getValue(Date.class);
                                final int credit = dataSnapshot.child("credit").getValue(Integer.class);

                                final Calendar calendar = Calendar.getInstance();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            TrueTime.build().initialize();
                                            Date date = TrueTime.now();
                                            calendar.setTime(date);


                                            /** daca dataExpirare > Data Curenta Adaug n zile DataNouaExpirare dupa dataVecheExpirare
                                             altfel adaug n zile dupa data curenta
                                             rezolva cazul cand cumpar abonament la mai multe zile dupa ce mi-a expirat abonamentul */

                                            if (dataExpirareAbonament.after(calendar.getTime())) {
                                                calendar.setTime(dataExpirareAbonament);
                                                calendar.add(Calendar.DATE, abonamentNew);
                                            } else {
                                                calendar.add(Calendar.DATE, abonamentNew);


                                            }
                                            Date dataExpirareAbonamentNou = calendar.getTime();

                                            int creditNou = credit - 4 * abonamentNew;
                                            if (creditNou >= 0) {

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showUpdatedInfo();
                                                    }
                                                });

                                                UserInformation userInformation = new UserInformation(dataSnapshot.getValue(UserInformation.class));
                                                userInformation.setCredit(creditNou);
                                                userInformation.setTipAbonament("Activ");
                                                userInformation.setExpirareAbonament(dataExpirareAbonamentNou);
                                                databaseReference.child(dataSnapshot.getKey()).setValue(userInformation);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(Profile.this, "You added " + abonamentNew + " zile la abonament", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(Profile.this, "You don't have enough credit", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                return;
                                            }


                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }).start();


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        });
        AbonamentAdapter abonamentAdapter = new AbonamentAdapter(listAbonament, Profile.this);
        listViewAbonament.setAdapter(abonamentAdapter);
        listViewAbonament.setVisibility(View.VISIBLE);


    }

    /**
     * This method is used to scan the qr-code.
     */
    public void scan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(Profile.this, "Ai iesit din activitatea de scanare!", Toast.LENGTH_LONG).show();
            } else {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                if (storageReference.child(result.getContents().toString()) != null) {
                    String id = storageReference.child(result.getContents()).toString();

                    if (id.contains("MetrorexPass")) {
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int calatorie = dataSnapshot.child("numarCalatorii").getValue(Integer.class);
                                        String abonament = dataSnapshot.child("tipAbonament").getValue(String.class);
                                        final Date abonamentExpirare = dataSnapshot.child("expirareAbonament").getValue(Date.class);
                                        if (abonament.equals("Activ")) {


                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        TrueTime.build().initialize();
                                                        Date date = TrueTime.now();
                                                        final long diff = abonamentExpirare.getTime() - date.getTime();
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(Profile.this, "Succes! Zile ramase din abonament:" + (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1), Toast.LENGTH_LONG).show();

                                                            }
                                                        });
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }


                                                }
                                            }).start();


                                        }
                                        if (calatorie > 0 && abonament.equals("Expirat")) {
                                            showUpdatedInfo();
                                            UserInformation userInformation = new UserInformation(dataSnapshot.getValue(UserInformation.class));
                                            userInformation.setNumarCalatorii(calatorie - 1);
                                            int calatorieNew = calatorie - 1;
                                            databaseReference.child(dataSnapshot.getKey()).setValue(userInformation);
                                            Toast.makeText(Profile.this, "Succes! Calatorii ramase:" + calatorieNew, Toast.LENGTH_LONG).show();
                                        } else {
                                            if (abonament.equals("Expirat") && calatorie == 0) {
                                                Toast.makeText(Profile.this, "Nu ai destule calatorii !", Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    } else {
                        Toast.makeText(Profile.this, "Codul scanat nu exista!", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(Profile.this, "Codul scanat nu exista!", Toast.LENGTH_LONG).show();
                }

            }
        } else {
            if (!bp.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((listViewCredit.getVisibility() == View.VISIBLE) || (listViewAbonament.getVisibility() == View.VISIBLE) || (listViewCalatorie.getVisibility() == View.VISIBLE)) {
                listViewCalatorie.setVisibility(View.GONE);
                listViewAbonament.setVisibility(View.GONE);
                listViewCredit.setVisibility(View.GONE);

            } else {
                super.onBackPressed();
            }


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            scan();

            // Handle the camera action
        } else if (id == R.id.nav_abonament) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }


            cumparaAbonament();


        } else if (id == R.id.nav_calatorii) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            cumparaCalatorie();

        } else if (id == R.id.nav_credit) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            cumparaCredit();

        } else if (id == R.id.nav_logout) {

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }

            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            finish();
            startActivity(new Intent(Profile.this, LoginRegister.class));
        }
        else if (id == R.id.nav_edit_profile) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            finish();
            startActivity(new Intent(Profile.this,EditProfile.class));

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

        final int creditNew = Integer.parseInt(productId.toString().substring(7));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int credit = dataSnapshot.child("credit").getValue(Integer.class);
                        int creditNou = credit + creditNew;
                        databaseReference.child(dataSnapshot.getKey()).child("credit").setValue(creditNou);
                        showUpdatedInfo();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}
