package com.printhub.printhub.HomeScreen;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;
import com.printhub.printhub.image.MultipleImages;
import com.printhub.printhub.sidebar.aboutus.AboutusActivity;
import com.printhub.printhub.bunkManager.BunkActivity;
import com.printhub.printhub.Cart;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.sidebar.HelpCenter;
import com.printhub.printhub.image.ImageActivity;
import com.printhub.printhub.NotificationActivity;
import com.printhub.printhub.sidebar.oldOrders.OrdersActivity;
import com.printhub.printhub.sidebar.Profile;
import com.printhub.printhub.R;
import com.printhub.printhub.WelcomeActivity;
import com.printhub.printhub.sidebar.Wishlist;
import com.printhub.printhub.pdf.pdfActivity;
import com.printhub.printhub.prodcutscategory.Bags;
import com.printhub.printhub.prodcutscategory.Keychains;
import com.printhub.printhub.prodcutscategory.Stationary;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.webianks.easy_feedback.EasyFeedback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import es.dmoral.toasty.Toasty;

public class MainnewActivity extends AppCompatActivity {

   //  private SliderLayout sliderShow;
    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;

    //to get user session data
    SliderView sliderView;
    SharedPreferences prefs = null;

    public static String firebaseUserId=null,cityName= null, collegeName= null;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    SharedPreferences detail = null;
    DatabaseReference mref;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainnew);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        firebaseUserId=user.getUid();
        mref= FirebaseDatabase.getInstance().getReference();
        detail = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        db.collection("users").document(firebaseUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    cityName = documentSnapshot.getString("cityName");
                    collegeName = documentSnapshot.getString("collegeName");
                }
            }
        });

        //Push notification customer type subscription
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "successful";
                        if (!task.isSuccessful()) {
                            msg = "failed";
                        }
                        Log.d("notification subscription", msg);
                    }
                });

        sliderView = findViewById(R.id.slider);

        prefs = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);

        final SliderAdapterExample adapter = new SliderAdapterExample(this);
        adapter.setCount(5);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.DROP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();

        
//
        //retrieve session values and display on listviews
//        //getValues();
//
       //Navigation Drawer with toolbar
       inflateNavDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CheckInternetConnection(this).checkConnection();
        firebaseUserId = user.getUid();
        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            tapview();
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
        }
    }

    private void tapview() {

            new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(R.id.notifintro), "Notifications", "Latest offers will be available here !")
                                    .targetCircleColor(R.color.colorAccent)
                                    .titleTextColor(R.color.colorAccent)
                                    .titleTextSize(25)
                                    .descriptionTextSize(15)
                                    .descriptionTextColor(R.color.colorAccent2)
                                    .drawShadow(true)                   // Whether to draw a drop shadow or not
                                    .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                    .tintTarget(true)
                                    .transparentTarget(true)
                                    .outerCircleColor(R.color.first),
                            TapTarget.forView(findViewById(R.id.view_profile), "Profile", "You can view and edit your profile here !")
                                    .targetCircleColor(R.color.colorAccent)
                                    .titleTextColor(R.color.colorAccent)
                                    .titleTextSize(25)
                                    .descriptionTextSize(15)
                                    .descriptionTextColor(R.color.colorAccent2)
                                    .drawShadow(true)                   // Whether to draw a drop shadow or not
                                    .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                    .tintTarget(true)
                                    .transparentTarget(true)
                                    .outerCircleColor(R.color.third),
                            TapTarget.forView(findViewById(R.id.cart), "Your Cart", "Here is Shortcut to your cart !")
                                    .targetCircleColor(R.color.colorAccent)
                                    .titleTextColor(R.color.colorAccent)
                                    .titleTextSize(25)
                                    .descriptionTextSize(15)
                                    .descriptionTextColor(R.color.colorAccent2)
                                    .drawShadow(true)
                                    .cancelable(false)// Whether tapping outside the outer circle dismisses the view
                                    .tintTarget(true)
                                    .transparentTarget(true)
                                    .outerCircleColor(R.color.second),
                            TapTarget.forView(findViewById(R.id.visitingcards), "Categories", "Product Categories have been listed here !")
                                    .targetCircleColor(R.color.colorAccent)
                                    .titleTextColor(R.color.colorAccent)
                                    .titleTextSize(25)
                                    .descriptionTextSize(15)
                                    .descriptionTextColor(R.color.colorAccent2)
                                    .drawShadow(true)
                                    .cancelable(false)// Whether tapping outside the outer circle dismisses the view
                                    .tintTarget(true)
                                    .transparentTarget(true)
                                    .outerCircleColor(R.color.fourth))
                    .listener(new TapTargetSequence.Listener() {
                        // This listener will tell us when interesting(tm) events happen in regards
                        // to the sequence
                        @Override
                        public void onSequenceFinish() {
                            Toasty.success(MainnewActivity.this, " You are ready to go !", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            // Boo
                        }
                    }).start();

    }

    private void inflateNavDrawer() {

        //set Custom toolbar to activity -----------------------------------------------------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.gradient_background)
                //.addProfiles(profile)
                .withCompactStyle(true)
                .build();

        //Adding nav drawer items ------------------------------------------------------------------
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.home).withIcon(R.drawable.home);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.myprofile).withIcon(R.drawable.profile);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("Orders").withIcon(R.drawable.orders);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.wishlist).withIcon(R.drawable.wishlist);
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.cart).withIcon(R.drawable.cart);
        PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(6).withName(R.string.logout).withIcon(R.drawable.logout);

        SecondaryDrawerItem item7 = new SecondaryDrawerItem().withIdentifier(8).withName("Offers").withIcon(R.drawable.tag);
        SecondaryDrawerItem item8 = new SecondaryDrawerItem().withIdentifier(9).withName(R.string.aboutus).withIcon(R.drawable.credits);
        SecondaryDrawerItem item9 = new SecondaryDrawerItem().withIdentifier(10).withName(R.string.feedback).withIcon(R.drawable.feedback);
        SecondaryDrawerItem item10 = new SecondaryDrawerItem().withIdentifier(11).withName(R.string.helpcentre).withIcon(R.drawable.helpccenter);

        SecondaryDrawerItem item12 = new SecondaryDrawerItem().withIdentifier(13).withName("App Tour").withIcon(R.drawable.tour);
        SecondaryDrawerItem item13 = new SecondaryDrawerItem().withIdentifier(14).withName("Explore").withIcon(R.drawable.explore);


        //creating navbar and adding to the toolbar ------------------------------------------------
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withDrawerLayout(R.layout.crossfade_drawer)
                .withAccountHeader(headerResult)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(true)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        item1, item2, item3, item4, item5, item6, new DividerDrawerItem(), item7, item8, item9, item10,new DividerDrawerItem(),item12,item13
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position) {

                            case 1:
                                if (result != null && result.isDrawerOpen()) {
                                    result.closeDrawer();
                                }
                                break;
                            case 2:
                                startActivity(new Intent(MainnewActivity.this, Profile.class));
                                break;
                            case 3:
                                startActivity(new Intent(MainnewActivity.this, OrdersActivity.class));
                                break;
                            case 4:
                                startActivity(new Intent(MainnewActivity.this, Wishlist.class));
                                break;
                            case 5:
                                startActivity(new Intent(MainnewActivity.this, Cart.class));
                                break;
                            case 6:
                                AuthUI.getInstance()
                                        .signOut(MainnewActivity.this)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                detail.edit().putBoolean("fillDetail", false).commit();
                                                Toasty.success(MainnewActivity.this,"Logout", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(MainnewActivity.this, WelcomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.success(MainnewActivity.this,"Logging Out Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;

                            case 8:
                                startActivity(new Intent(MainnewActivity.this, NotificationActivity.class));
                                break;

                            case 9:
                                startActivity(new Intent(MainnewActivity.this, AboutusActivity.class));
                                break;
                            case 10:
                                new EasyFeedback.Builder(MainnewActivity.this)
                                        .withEmail("printhub.connect@gmail.com")
                                        .withSystemInfo()
                                        .build()
                                        .start();
                                break;
                            case 11:
                                startActivity(new Intent(MainnewActivity.this, HelpCenter.class));
                                break;
                            case 13:
                                prefs.edit().putBoolean("firstrun", true).commit();
                                //session.setFirstTimeLaunch(true);
                                startActivity(new Intent(MainnewActivity.this, WelcomeActivity.class));
                                finish();
                                break;
                            case 14:
                                if (result != null && result.isDrawerOpen()) {
                                    result.closeDrawer();
                                }
                                tapview();
                                break;
                            default:
                                Toast.makeText(MainnewActivity.this, "Default", Toast.LENGTH_LONG).show();

                        }

                        return true;
                    }
                })
                .build();

        //Setting crossfader drawer------------------------------------------------------------

        crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();

        //define maxDrawerWidth
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));

        //add second view (which is the miniDrawer)
        final MiniDrawer miniResult = result.getMiniDrawer();

        //build the view for the MiniDrawer
        View view = miniResult.build(this);

        //set the background of the MiniDrawer as this would be transparent
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));

        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(400);

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
//
   public void viewProfile(View view) {
        startActivity(new Intent(MainnewActivity.this, Profile.class));
   }

   public void viewCart(View view) {
        startActivity(new Intent(MainnewActivity.this, Cart.class));
    }

    public void Notifications(View view) {
        startActivity(new Intent(MainnewActivity.this, NotificationActivity.class));
    }

    public void cardsActivity(View view) {
        Intent intent= new Intent(MainnewActivity.this, MultipleImages.class);
        String uploadkey = mref.push().getKey();
        intent.putExtra("uploadkey",uploadkey);
        startActivity(intent);
    }

    public void tshirtActivity(View view) {
        Intent intent= new Intent(MainnewActivity.this , pdfActivity.class);
        String uploadkey = mref.push().getKey();
        intent.putExtra("uploadkey",uploadkey);
        startActivity(intent);
    }


    public void bagsActivity(View view) {

        startActivity(new Intent(MainnewActivity.this, Bags.class));
    }

    public void stationaryAcitivity(View view) {

        startActivity(new Intent(MainnewActivity.this, Stationary.class));
    }

    public void calendarsActivity(View view) {

        startActivity(new Intent(MainnewActivity.this, BunkActivity.class));
    }

    public void keychainsActivity(View view) {

        startActivity(new Intent(MainnewActivity.this, Keychains.class));
    }

}
