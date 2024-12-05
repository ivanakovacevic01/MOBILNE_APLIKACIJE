package com.example.eventapp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.eventapp.NotificationService;
import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.ActivityHomeBinding;

import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.HashSet;
import java.util.Set;


public class HomeActivity extends AppCompatActivity{
    private User loggedUser;
    private boolean isVisible=false;
    private ActivityHomeBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Set<Integer> topLevelDestinations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);


        drawer = binding.drawerLayout;
        navigationView = binding.navView;

        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_launcher_foreground);
            actionBar.setHomeButtonEnabled(false);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
         drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        topLevelDestinations.add(R.id.nav_language);
         navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            Log.i("ShopApp", "Destination Changed");
            int id = navDestination.getId();
            Log.i("EventApp","pukao");
            boolean isTopLevelDestination = topLevelDestinations.contains(id);
            if (!isTopLevelDestination) {
                if (id == R.id.nav_products) {
                    Toast.makeText(HomeActivity.this, "Products", Toast.LENGTH_SHORT).show();

                }
                if (id == R.id.packagePageFragment) {
                    Toast.makeText(HomeActivity.this, "Packages", Toast.LENGTH_SHORT).show();

                }
                if (id == R.id.serviceFragment) {
                    Toast.makeText(HomeActivity.this, "Services", Toast.LENGTH_SHORT).show();
                    isVisible=false;
                } else if (id == R.id.employeesPageFragment) {
                    //Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    isVisible=true;
                }
                else if (id == R.id.serviceFragment) {
                    //Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    isVisible=false;
                }


                // Close the drawer if the destination is not a top level destination
                drawer.closeDrawers();
            }else{
                //if (id == R.id.nav_settings) {
                  //  Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();

                if (id == R.id.nav_language) {
                    Toast.makeText(HomeActivity.this, "Language", Toast.LENGTH_SHORT).show();

                }


            }

        });


        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_products,R.id.mainActivity,R.id.packagePageFragment, R.id.serviceFragment, R.id.nav_language,R.id.employeesPageFragment, R.id.overviewFragment, R.id.event_fragment, R.id.eventTypesPageFragment, R.id.categoriesPageFragment, R.id.subcategorySuggestionPageFragment, R.id.home_page_fragment, R.id.employeeProfile, R.id.reservationPageFragment, R.id.ownerRequestsPageFragment, R.id.reports, R.id.myProfile, R.id.notificationsPage,R.id.priceList, R.id.chatFragment)


                .setOpenableLayout(drawer)
                .build();
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //invalidateOptionsMenu();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu1) {
        // Provjerite ulogu korisnika i postavite vidljivost stavki u meniju na osnovu toga

        Menu menu = navigationView.getMenu();   //dodato jer menu1 ne reaguje
        String userRole = SharedPreferencesManager.getUserRole(this); // Dobijanje uloge korisnika

        // Postavite vidljivost stavki u meniju na osnovu uloge korisnika
        if (UserType.ADMIN.toString().equals(userRole)) {
            menu.findItem(R.id.eventTypesPageFragment).setVisible(true);
            menu.findItem(R.id.categoriesPageFragment).setVisible(true);
            menu.findItem(R.id.subcategorySuggestionPageFragment).setVisible(true);
            menu.findItem(R.id.mainActivity).setVisible(true);
            menu.findItem(R.id.ownerRequestsPageFragment).setVisible(true);
            menu.findItem(R.id.employeesPageFragment).setVisible(false);
            menu.findItem(R.id.employeeProfile).setVisible(false);
            menu.findItem(R.id.overviewFragment).setVisible(false);
            menu.findItem(R.id.event_fragment).setVisible(false);
            menu.findItem(R.id.nav_management).setVisible(false);
            menu.findItem(R.id.packagePageFragment).setVisible(false);
            menu.findItem(R.id.nav_products).setVisible(false);
            menu.findItem(R.id.serviceFragment).setVisible(false);
            menu.findItem(R.id.reports).setVisible(true);
            menu.findItem(R.id.myProfile).setVisible(false);
            menu.findItem(R.id.priceList).setVisible(false);

            //ovo sluzi da ne vidi home page(ni na prvu)
            NavController navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
            navController.navigate(R.id.categoriesPageFragment);

        }
        else if ((UserType.OWNER.toString().equals(userRole))) {
            menu.findItem(R.id.eventTypesPageFragment).setVisible(false);
            menu.findItem(R.id.categoriesPageFragment).setVisible(false);
            menu.findItem(R.id.subcategorySuggestionPageFragment).setVisible(false);
            menu.findItem(R.id.mainActivity).setVisible(true);
            menu.findItem(R.id.employeesPageFragment).setVisible(true);
            menu.findItem(R.id.employeeProfile).setVisible(false);
            menu.findItem(R.id.nav_management).setVisible(false);
            menu.findItem(R.id.overviewFragment).setVisible(false);
            menu.findItem(R.id.nav_management).setVisible(true);
            menu.findItem(R.id.packagePageFragment).setVisible(true);
            menu.findItem(R.id.nav_products).setVisible(true);
            menu.findItem(R.id.serviceFragment).setVisible(true);
            menu.findItem(R.id.myProfile).setVisible(true);
            menu.findItem(R.id.ownerRequestsPageFragment).setVisible(false);
            menu.findItem(R.id.reports).setVisible(false);
            menu.findItem(R.id.priceList).setVisible(true);


        }
        else if ((UserType.EMPLOYEE.toString().equals(userRole))) {
            menu.findItem(R.id.eventTypesPageFragment).setVisible(false);
            menu.findItem(R.id.categoriesPageFragment).setVisible(false);
            menu.findItem(R.id.subcategorySuggestionPageFragment).setVisible(false);
            menu.findItem(R.id.mainActivity).setVisible(true);
            menu.findItem(R.id.employeesPageFragment).setVisible(false);
            menu.findItem(R.id.employeeProfile).setVisible(true);
            menu.findItem(R.id.nav_management).setVisible(false);
            menu.findItem(R.id.overviewFragment).setVisible(false);
            menu.findItem(R.id.event_fragment).setVisible(false);
            menu.findItem(R.id.nav_management).setVisible(true);
            menu.findItem(R.id.packagePageFragment).setVisible(true);
            menu.findItem(R.id.nav_products).setVisible(true);
            menu.findItem(R.id.serviceFragment).setVisible(true);
            menu.findItem(R.id.ownerRequestsPageFragment).setVisible(false);
            menu.findItem(R.id.reports).setVisible(false);
            menu.findItem(R.id.myProfile).setVisible(true);
            menu.findItem(R.id.priceList).setVisible(true);


        }
        else if ("NOT_LOGGED_IN".equals(userRole)) {
            menu.findItem(R.id.overviewFragment).setVisible(true);
            menu.findItem(R.id.employeeProfile).setVisible(false);
            menu.findItem(R.id.eventTypesPageFragment).setVisible(false);
            menu.findItem(R.id.categoriesPageFragment).setVisible(false);
            menu.findItem(R.id.employeesPageFragment).setVisible(false);
            menu.findItem(R.id.subcategorySuggestionPageFragment).setVisible(false);
            menu.findItem(R.id.mainActivity).setVisible(false);
            menu.findItem(R.id.event_fragment).setVisible(false);
            menu.findItem(R.id.nav_management).setVisible(false);
            menu.findItem(R.id.packagePageFragment).setVisible(false);
            menu.findItem(R.id.nav_products).setVisible(false);
            menu.findItem(R.id.serviceFragment).setVisible(false);
            menu.findItem(R.id.ownerRequestsPageFragment).setVisible(false);
            menu.findItem(R.id.reports).setVisible(false);
            menu.findItem(R.id.myProfile).setVisible(false);
            menu.findItem(R.id.priceList).setVisible(false);


        }
        else if ("ORGANIZER".equals(userRole)) {
            menu.findItem(R.id.overviewFragment).setVisible(true);
            menu.findItem(R.id.employeeProfile).setVisible(false);
            menu.findItem(R.id.nav_management).setVisible(false);
            menu.findItem(R.id.packagePageFragment).setVisible(false);
            menu.findItem(R.id.nav_products).setVisible(false);
            menu.findItem(R.id.serviceFragment).setVisible(false);
            menu.findItem(R.id.eventTypesPageFragment).setVisible(false);
            menu.findItem(R.id.categoriesPageFragment).setVisible(false);
            menu.findItem(R.id.employeesPageFragment).setVisible(false);
            menu.findItem(R.id.subcategorySuggestionPageFragment).setVisible(false);
            menu.findItem(R.id.event_fragment).setVisible(true);
            menu.findItem(R.id.mainActivity).setVisible(true);
            menu.findItem(R.id.ownerRequestsPageFragment).setVisible(false);
            menu.findItem(R.id.reports).setVisible(false);
            menu.findItem(R.id.myProfile).setVisible(true);
            menu.findItem(R.id.priceList).setVisible(false);


        }

        return true;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        getMenuInflater().inflate(R.menu.nav_menu, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //item.setChecked(true);

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);



        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
//        Log.i("NESTO", "NAVIGACIJA");

        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }


    public boolean IsVisible(){
        return isVisible;
    }
    /*private void getLoggedUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        //SharedPreferencesManager.getToken(HomeActivity.this); vec je sacuvan
        if (firebaseUser != null) {
            UserRepo.getUserByEmail(firebaseUser.getEmail(), new UserRepo.UserFetchCallback() {
                @Override
                public void onUserObjectFetched(User user, String errorResponse) {
                    if (user != null && user.isActive()) {
                        loggedUser = user;
                        Log.i("provjera", user.getFirstName());
                    }
                }
            });

        } else {
            //neulogovan je, zabraniti mu stavke menija
            Log.i("neulog", "neulog");
        }
    }*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, NotificationService.class);
        stopService(intent);
    }

}