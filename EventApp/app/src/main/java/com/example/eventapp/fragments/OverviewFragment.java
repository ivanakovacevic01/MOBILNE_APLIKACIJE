package com.example.eventapp.fragments;

import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventapp.DistanceCalculator;
import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.adapters.ItemListAdapter;
import com.example.eventapp.adapters.eventOrganizer.EventSpinnerAdapter;
import com.example.eventapp.databinding.AllFiltersViewBinding;
import com.example.eventapp.fragments.eventOrganizer.PopupDialogFragment;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.DialogType;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Favourites;
import com.example.eventapp.model.Filters;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.WeeklyEvent;
import com.example.eventapp.model.WorkingTime;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.EventRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.FavouritesRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.WeeklyEventRepo;
import com.example.eventapp.repositories.WorkingTimeRepo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;
import com.google.type.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class OverviewFragment extends Fragment {

    private AllFiltersViewBinding binding;
    private ItemListAdapter adapter;
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Package> packages = new ArrayList<>();
    private ArrayList<Service> services = new ArrayList<>();
    private ArrayList<Employee> employees = new ArrayList<>();
    private ArrayList<Event> events = new ArrayList<>();
    private ArrayList<Product> filteredProducts = new ArrayList<>();
    private ArrayList<Package> filteredPackages = new ArrayList<>();
    private ArrayList<Service> filteredServices = new ArrayList<>();
    private ArrayList<WorkingTime> workingTimes = new ArrayList<>();
    private ArrayList<WeeklyEvent> weeklyEvents = new ArrayList<>();
    private ArrayList<Address> addresses = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();
    private Filters filters = new Filters();
    private Event selectedEvent;
    private Favourites f;
    private OverviewFragment fragment;
    public static OverviewFragment newInstance(){
        OverviewFragment fragment = new OverviewFragment();
        //fragment.filters = new Filters();
        fragment.filters.text = "";
        fragment.filters.availability = 2;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = AllFiltersViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.getEvent();
        this.setAdapter(root);
        this.getWorkingCalendar();
        this.getAddresses();
        this.getReservations();

        //My favourites button
        Button btnFavourites = root.findViewById(R.id.button_favourites);
        if(SharedPreferencesManager.getUserRole(getContext()) != null && SharedPreferencesManager.getUserRole(getContext()).equals("ORGANIZER"))
        {
            btnFavourites.setVisibility(View.VISIBLE);
            btnFavourites.setOnClickListener(cat -> {
                FragmentTransition.to(FavouritesFragment.newInstance(), getActivity(),
                        true, R.id.home_page_fragment);
            });
        }


        //open filters
        Button btnFilters = root.findViewById(R.id.button_filters);
        btnFilters.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.FullScreenBottomSheetDialog);
            View dialogView = getLayoutInflater().inflate(R.layout.filters, null);
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();


            //price range slider
            RangeSlider priceRange = bottomSheetDialog.findViewById(R.id.price_range);
            priceRange.setValueFrom(0.0f);
            float maxPrice = getMaxPrice();
            priceRange.setValueTo(maxPrice);
            //filters.endPrice = maxPrice;
            priceRange.setValues(filters.startPrice, filters.endPrice);
            TextView priceMax = bottomSheetDialog.findViewById(R.id.price_max);
            priceMax.setText(String.valueOf(getMaxPrice()));

            //Search View
            SearchView searchView = bottomSheetDialog.findViewById(R.id.search_bar_filters);
            searchView.setQuery(filters.text, false);

            //RadioButtons
            RadioGroup radioGroup = bottomSheetDialog.findViewById(R.id.radio_group);
            if(filters.availability == 0)
                radioGroup.check(R.id.not_available);
            else if(filters.availability == 1)
                radioGroup.check(R.id.available);
            else
                radioGroup.check(R.id.all);

            //categories button
            Button btnCategories = bottomSheetDialog.findViewById(R.id.spinner_cat);
            btnCategories.setOnClickListener(cat -> {
                PopupDialogFragment popupDialog = PopupDialogFragment.newInstance(DialogType.Caterogies, filters);
                popupDialog.show(getChildFragmentManager(), "tag");

            });

            //subcategories button
            Button btnSubcategories = bottomSheetDialog.findViewById(R.id.spinner_subcat);
            btnSubcategories.setOnClickListener(cat -> {
                PopupDialogFragment popupDialog = PopupDialogFragment.newInstance(DialogType.SubCategories, filters);
                popupDialog.show(getChildFragmentManager(), "tag");
            });

            //types button
            Button btnTypes = bottomSheetDialog.findViewById(R.id.spinner_type);
            btnTypes.setOnClickListener(cat -> {
                PopupDialogFragment popupDialog = PopupDialogFragment.newInstance(DialogType.EventTypes, filters);
                popupDialog.show(getChildFragmentManager(), "tag");
            });

        Button btnDateRange = bottomSheetDialog.findViewById(R.id.button_set_date_range);
            btnDateRange.setOnClickListener(v2 -> {
            PopupDialogFragment popupDialog = PopupDialogFragment.newInstance(DialogType.Date, filters);
            popupDialog.show(getChildFragmentManager(), "tag");
            //filters.startDate = popupDialog.filters.startDate;
            //filters.endDate = popupDialog.filters.endDate;
        });

        //Available radio button
        RadioButton availableButton = bottomSheetDialog.findViewById(R.id.available);
        RadioButton notAvailableButton = bottomSheetDialog.findViewById(R.id.not_available);


            //open filters
            Button btnApply = bottomSheetDialog.findViewById(R.id.apply_filters);
            btnApply.setOnClickListener(v3 -> {

                //set filters
                PopupDialogFragment datePicker = (PopupDialogFragment) getChildFragmentManager().findFragmentByTag("DATE_PICKER");
                if(datePicker != null)
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(datePicker.filters.startDate);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    filters.startDate = new Date(calendar.getTimeInMillis());
                    calendar.setTime(datePicker.filters.endDate);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    filters.endDate = new Date(calendar.getTimeInMillis());
                }

                RangeSlider range = bottomSheetDialog.findViewById(R.id.price_range);
                filters.startPrice = range.getValues().get(0);
                filters.endPrice = range.getValues().get(1);
                filters.text = ((androidx.appcompat.widget.SearchView) bottomSheetDialog.findViewById(R.id.search_bar_filters)).getQuery().toString();

                if(availableButton.isChecked())
                    filters.availability = 1;
                else if(notAvailableButton.isChecked())
                    filters.availability = 0;
                else
                    filters.availability = 2;

                filter(root);
                bottomSheetDialog.dismiss();
            });

        });

        //filtering based on item type

        Spinner spinnerTypes = root.findViewById(R.id.spinner_type);
        spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filter(root);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //filtering based on event

        Spinner spinnerEvent = root.findViewById(R.id.spinner_event);

        if(SharedPreferencesManager.getUserRole(getContext())!=null &&!SharedPreferencesManager.getUserRole(getContext()).equals("ORGANIZER"))
            spinnerEvent.setVisibility(View.INVISIBLE);
        spinnerEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(spinnerEvent.getSelectedItem() != null)
                {
                    for(Event e: events)
                    {
                        if(e.getId().equals(((Event)spinnerEvent.getSelectedItem()).getId()))
                        {
                            if(e.getName().equals("All"))
                                selectedEvent = null;
                            else
                                selectedEvent = e;
                            filterBasedOnEvent(e);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setAdapter(View root)
    {
        RecyclerView recyclerView = root.findViewById(R.id.item_list_all);

        ProductRepo productRepo = new ProductRepo();
        ServiceRepo serviceRepo = new ServiceRepo();
        PackageRepo packageRepo = new PackageRepo();
        EmployeeRepo employeeRepo = new EmployeeRepo();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FavouritesRepo favouritesRepo = new FavouritesRepo();
        favouritesRepo.getByOrganizer(new FavouritesRepo.FavouriteFetchCallback() {
            @Override
            public void onFavouriteFetch(ArrayList<Favourites> favourites) {

                if(favourites.isEmpty())
                    f = new Favourites();
                else
                    f= favourites.get(0);
                productRepo.getAllProducts(new ProductRepo.ProductFetchCallback() {
                    @Override
                    public void onProductFetch(ArrayList<Product> types) {
                        products.clear();
                        if(SharedPreferencesManager.getUserRole(getContext()) != null && (SharedPreferencesManager.getUserRole(getContext()).equals("ORGANIZER") || SharedPreferencesManager.getUserRole(getContext()).equals("NOT_LOGGED_IN")))
                        {
                            for(Product p: types)
                            {
                                if(p.getVisible() && !p.getDeleted())
                                    products.add(p);
                            }
                        }
                        else
                            products.addAll(types);
                        adapter = new ItemListAdapter(packages, services, products, getActivity(), f);
                        recyclerView.setAdapter(adapter);
                        filters.endPrice = getMaxPrice();
                        filter(root);
                    }
                });
                packageRepo.getAllPackages(new PackageRepo.PackageFetchCallback() {
                    @Override
                    public void onPackageFetch(ArrayList<Package> types) {
                        packages.clear();
                        if(SharedPreferencesManager.getUserRole(getContext()) != null && (SharedPreferencesManager.getUserRole(getContext()).equals("ORGANIZER") || SharedPreferencesManager.getUserRole(getContext()).equals("NOT_LOGGED_IN")))
                        {
                            for(Package p: types)
                            {
                                if(p.getVisible() && !p.getDeleted())
                                    packages.add(p);
                            }
                        }
                        else
                            packages.addAll(types);
                        adapter = new ItemListAdapter(packages, services, products, getActivity(), f);
                        recyclerView.setAdapter(adapter);
                        filters.endPrice = getMaxPrice();
                        filter(root);
                    }
                });
                serviceRepo.getAllServices(new ServiceRepo.ServiceFetchCallback() {
                    @Override
                    public void onServiceFetch(ArrayList<Service> types) {
                        services.clear();
                        if(SharedPreferencesManager.getUserRole(getContext()) != null && (SharedPreferencesManager.getUserRole(getContext()).equals("ORGANIZER") || SharedPreferencesManager.getUserRole(getContext()).equals("NOT_LOGGED_IN")))
                        {
                            for(Service p: types)
                            {
                                if(p.getVisible() && !p.getDeleted())
                                    services.add(p);
                            }
                        }
                        else
                            services.addAll(types);
                        adapter = new ItemListAdapter(packages, services, products, getActivity(), f);
                        recyclerView.setAdapter(adapter);
                        filters.endPrice = getMaxPrice();
                        filter(root);
                    }
                });

            }
        }, SharedPreferencesManager.getEmail(getContext()));

        employeeRepo.getAll(new EmployeeRepo.EmployeeFetchCallback() {
            @Override
            public void onEmployeeFetch(ArrayList<Employee> types) {
                employees.clear();
                employees.addAll(types);
            }
        });
    }

    private void filter(View root){
        RecyclerView recyclerView = root.findViewById(R.id.item_list_all);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        filteredPackages = filterPackages();
        filteredProducts = filterProducts();
        filteredServices = filterServices();
        Spinner type = root.findViewById(R.id.spinner_type);
        String selectedType = type.getSelectedItem().toString();


        if(selectedType.equals("All"))
            adapter = new ItemListAdapter(filteredPackages, filteredServices, filteredProducts, getActivity(), f);
        else if(selectedType.equals("Products"))
            adapter = new ItemListAdapter(new ArrayList<>(), new ArrayList<>(), filteredProducts, getActivity(), f);
        else if(selectedType.equals("Services"))
            adapter = new ItemListAdapter(new ArrayList<>(), filteredServices, new ArrayList<>(), getActivity(), f);
        else if(selectedType.equals("Packages"))
            adapter = new ItemListAdapter(filteredPackages, new ArrayList<>(), new ArrayList<>(), getActivity(), f);

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<Package> filterPackages(){
        ArrayList<Package> packageList = new ArrayList<>();
        for(Package p: packages)
        {
            if((filters.text.isEmpty() || (p.getName().toLowerCase().contains(filters.text.toLowerCase()))) &&
                    (filters.categories.stream().anyMatch(c -> c.getId().equals(p.getCategory()))  || filters.categories.isEmpty()) &&
                    (p.getPrice()>=filters.startPrice && p.getPrice()<=filters.endPrice) &&
                    (filters.availability == 2 || (p.getAvailable()? 1:0 )== filters.availability) &&
                    isEventType(p.getType())
            ){
                for(String subcategory: p.getSubcategories())
                {
                    Geocoder geocoder = new Geocoder(getContext());
                    Address ad = new Address();
                    for(Address address: addresses)
                    {
                        if(address.getFirmId().equals(p.getFirmId()))
                        {
                            ad = address;
                            break;
                        }
                    }

                    if(!filters.subcategories.isEmpty())
                    {
                        if(filters.subcategories.stream().anyMatch(c -> c.getId().equals(subcategory)) &&
                        packageList.stream().noneMatch(c -> c.getId().equals(p.getId())))
                        {
                            if(selectedEvent != null
                        && DistanceCalculator.calculateDistanceBetweenAddresses(geocoder, selectedEvent.getLocation(), ad.getAddress()) <= selectedEvent.getMaxKms())
                                packageList.add(p);
                            else if(selectedEvent == null)
                                packageList.add(p);
                        }
                    }else{
                        if(packageList.stream().noneMatch(c -> c.getId().equals(p.getId()))) {
                            if (selectedEvent != null
                                    && DistanceCalculator.calculateDistanceBetweenAddresses(geocoder, selectedEvent.getLocation(), ad.getAddress()) <= selectedEvent.getMaxKms())
                                packageList.add(p);
                            else if(selectedEvent == null)
                                packageList.add(p);
                        }
                    }
                }
            }
                //subcat
        }
        return packageList;
    }

    private ArrayList<Product> filterProducts(){
        ArrayList<Product> productList = new ArrayList<>();
        for(Product p: products)
        {
            Geocoder geocoder = new Geocoder(getContext());
            Address ad = new Address();
            for(Address address: addresses)
            {
                if(address.getFirmId().equals(p.getFirmId()))
                {
                    ad = address;
                    break;
                }
            }

            if((filters.text.isEmpty() || (p.getName().toLowerCase().contains(filters.text.toLowerCase()))) &&
                    (filters.categories.stream().anyMatch(c -> c.getId().equals(p.getCategory()))  || filters.categories.isEmpty()) &&
                            (filters.subcategories.stream().anyMatch(s -> s.getId().equals(p.getSubcategory()))  || filters.subcategories.isEmpty()) &&
                            (p.getPrice()>=filters.startPrice && p.getPrice()<=filters.endPrice) &&
                    (filters.availability == 2 || (p.getAvailable()? 1:0 )== filters.availability) &&
                    isEventType(p.getType())
            ){
                if(selectedEvent != null
                        && DistanceCalculator.calculateDistanceBetweenAddresses(geocoder, selectedEvent.getLocation(), ad.getAddress()) <= selectedEvent.getMaxKms())
                        productList.add(p);
                else if(selectedEvent == null)
                    productList.add(p);
            }
        }
        return productList;
    }

    private ArrayList<Service> filterServices(){
        ArrayList<Service> serviceList = new ArrayList<>();
        for(Service p: services)
        {
            Geocoder geocoder = new Geocoder(getContext());
            Address ad = new Address();
            for(Address address: addresses)
            {
                if(address.getFirmId().equals(p.getFirmId()))
                {
                    ad = address;
                    break;
                }
            }

            if((filters.text.isEmpty() || (p.getName().toLowerCase().contains(filters.text.toLowerCase())) || (p.getLocation().toLowerCase().contains(filters.text.toLowerCase())) || isEmployee(p.getAttendants())) &&
                    (filters.categories.stream().anyMatch(c -> c.getId().equals(p.getCategory()))  || filters.categories.isEmpty()) &&
                    (filters.subcategories.stream().anyMatch(s -> s.getId().equals(p.getSubcategory()))  || filters.subcategories.isEmpty()) &&
                    (p.getPrice()>=filters.startPrice && p.getPrice()<=filters.endPrice) &&
                    (filters.availability == 2 || (p.getAvailable()? 1:0 )== filters.availability) &&
                    isEventType(p.getType()) &&
                    isInDateRange(p)
            ){
                if(selectedEvent != null
                        && DistanceCalculator.calculateDistanceBetweenAddresses(geocoder, selectedEvent.getLocation(), ad.getAddress()) <= selectedEvent.getMaxKms())
                        serviceList.add(p);
                else if(selectedEvent == null)
                    serviceList.add(p);
            }
        }
        return serviceList;
    }

    private boolean isEventType(List<String> eventTypeIds)
    {
        for(String id: eventTypeIds)
        {
            if(!filters.types.isEmpty())
            {
                if(filters.types.stream().anyMatch(c -> c.getId().equals(id)))
                    return true;
            }else
                return true;
        }
        return false;
    }

    private boolean isEmployee(List<String> employeeIds)
    {
        for(String id: employeeIds)
        {
            if(!filters.text.isEmpty())
            {
                for(Employee e: employees)
                {
                    if(e.getId().equals(id)){
                        if(e.getFirstName().toLowerCase().contains(filters.text.toLowerCase()) ||
                                e.getLastName().toLowerCase().contains(filters.text.toLowerCase()))
                            return true;
                    }
                }
            }else
                return true;
        }
        return false;
    }
    private List<Package> getPackage(){
        ArrayList<String> types = new ArrayList<String>();
        types.add("Svadba");
        ArrayList<Package>p = new ArrayList<Package>();
        //p.add(new Package("1", "paket", "opis paketa", true, true, 20, "kategorija", new ArrayList<String>() , new ArrayList<Product>(), new ArrayList<Service>(), types, 3000, new ArrayList<Integer>(), 2, 3, true));
        return p;
    }
    private List<Service> getService(){
        ArrayList<Service> services=new ArrayList<>();
        ArrayList<Integer> images=new ArrayList<>();
        images.add(R.drawable.foto_book);
        ArrayList<String> attendans=new ArrayList<>();
        attendans.add("Petar Petrovic");
        ArrayList<String> type=new ArrayList<>();
        type.add("vencanje");

        return services;
    }

    private List<Product> getProducts(){
        ArrayList<Product> products=new ArrayList<>();
        //Product product1 = new Product("1L", "Product 1", "Description of Product 1", 0, 50.0, 0.0, true, true, new ArrayList<>(), "Category1", "Subcategory1");
        //Product product2 = new Product("2L", "Product 2", "Description of Product 2", 1, 75.0, 10.0, true, true, new ArrayList<>(), "Category2", "Subcategory2");


        // Adding the products to the list
        //products.add(product1);
        //products.add(product2);
        return products;
    }

    private float getMaxPrice(){
        float maxPrice = 1.0f;
        for(Package p: packages)
        {
            if(p.getPrice() > maxPrice)
                maxPrice = (float) p.getPrice();
        }
        for(Product p: products)
        {
            if(p.getPrice() > maxPrice)
                maxPrice = (float) p.getPrice();
        }
        for(Service p: services)
        {
            if(p.getPrice() > maxPrice)
                maxPrice = (float) p.getPrice();
        }
        return maxPrice;
    }

    private void filterBasedOnEvent(Event event)
    {
        filters = new Filters();
        filters.startPrice = 0;
        filters.endPrice = getMaxPrice();
        filters.availability = 2;
        filters.text = "";
        if(!event.getName().equals("All"))
        {
            //sfilters.text = event.getLocation();
            filters.startDate = new Date(event.getDate().getTime());
            filters.endDate = new Date(event.getDate().getTime());

            EventTypeRepo repo = new EventTypeRepo();
            repo.getEventType(new EventTypeRepo.EventTypeFetchCallback() {
                @Override
                public void onEventTypeFetch(ArrayList<EventType> types) {
                    if(types != null && !types.isEmpty())
                    {
                        filters.types.add(types.get(0));
                        EventType type = types.get(0);
                        for(String sub: type.getSuggestedSubcategoriesIds()){
                            Subcategory s = new Subcategory();
                            s.setId(sub);
                            filters.subcategories.add(s);
                        }
                    }else{
                        filters.types = new ArrayList<>();
                    }

                }
            }, event.getEventTypeId());
        }
        filter(binding.getRoot());
    }

    private void getEvent(){
        View root = binding.getRoot();
        Spinner spinnerEvents = root.findViewById(R.id.spinner_event);
        EventRepo repo = new EventRepo();
        repo.getByOrganizer(new EventRepo.EventFetchCallback() {
            @Override
            public void onEventFetch(ArrayList<Event> types) {
                events.clear();
                Event event = new Event();
                event.setName("All");
                event.setDescription(" ");
                event.setId("event_all");
                events.add(event);
                events.addAll(types);
                EventSpinnerAdapter eventSpinnerAdapter = new EventSpinnerAdapter(getContext(), events);
                spinnerEvents.setAdapter(eventSpinnerAdapter);
            }
        }, SharedPreferencesManager.getEmail(getContext()));

    }

    private boolean isInDateRange(Service service){
        boolean isInRange = true;
        if(service.getAttendants() != null)
        {
            for(String a: service.getAttendants())
            {
                for(Employee e: employees)
                {
                    if(e.getId().equals(a) && filters.startDate!=null && filters.endDate!=null)
                    {
                        isInRange = isInRange && isFree(e, service);
                    }
                }
            }
        }
        return isInRange;
    }

    private boolean isFree(Employee e, Service service)
    {
        ArrayList<WeeklyEvent> eventsByEmployee = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date start = (filters.startDate);

            WorkingTime workingTime = null;


                for(WeeklyEvent we: weeklyEvents) {
                    try {
                        if (we.getEmployeeId().equals(e.getId()) && dateFormat.parse(we.getDate()).getTime() >= filters.startDate.getTime() && dateFormat.parse(we.getDate()).getTime() <= filters.endDate.getTime())
                            eventsByEmployee.add(we);
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                    while (start.getTime() <= filters.endDate.getTime()) {
                        workingTime = null;
                        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                                        for (WorkingTime w : workingTimes) {
                                            try{
                                                if (w.getUserId().equals(e.getId()))
                                                {
                                                    if(w.getStartDate().isEmpty() || w.getEndDate().isEmpty())
                                                    {
                                                        workingTime = w;
                                                        break;
                                                    }
                                                    else if(dateFormat.parse(w.getStartDate()).getTime() <= start.getTime() && dateFormat.parse(w.getEndDate()).getTime() >= start.getTime()) {
                                                        workingTime = w;
                                                        break;
                                                    }
                                                }
                                            }catch (ParseException ex) {
                                                throw new RuntimeException(ex);
                                            }

                                        }
                                        if(workingTime == null)
                                            return true;

                                        String startTime;
                                        String endTime;
                                        Calendar c = Calendar.getInstance();
                                        c.setTime(start);
                                        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                                        if (dayOfWeek == 1) {
                                            startTime = workingTime.getSundayStartTime();
                                            endTime = workingTime.getSundayEndTime();
                                        } else if (dayOfWeek == 2) {
                                            startTime = workingTime.getMondayStartTime();
                                            endTime = workingTime.getMondayEndTime();
                                        } else if (dayOfWeek == 3) {
                                            startTime = workingTime.getTuesdayStartTime();
                                            endTime = workingTime.getTuesdayEndTime();
                                        } else if (dayOfWeek == 4) {
                                            startTime = workingTime.getWednesdayStartTime();
                                            endTime = workingTime.getWednesdayEndTime();
                                        } else if (dayOfWeek == 5) {
                                            startTime = workingTime.getThursdayStartTime();
                                            endTime = workingTime.getThursdayEndTime();
                                        } else if (dayOfWeek == 6) {
                                            startTime = workingTime.getFridayStartTime();
                                            endTime = workingTime.getFridayEndTime();
                                        } else {
                                            startTime = workingTime.getSaturdayStartTime();
                                            endTime = workingTime.getSaturdayEndTime();
                                        }

                                        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                        Date date1;
                                        Date date2;
                                        long difference = 0;

                                        if(startTime.isEmpty() || endTime.isEmpty())
                                        {
                                            start = new Date(start.getTime() + (1000 * 60 * 60 * 24));
                                            continue;
                                        }

                                        try {
                                            date1 = format.parse(startTime);
                                            date2 = format.parse(endTime);
                                            difference = date2.getTime() - date1.getTime();
                                        } catch (Exception exception) {
                                        }

                                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        for (WeeklyEvent we1 : eventsByEmployee) {
                                            try {
                                                if(dateFormat.parse(we1.getDate()).equals(start))
                                                {
                                                    Date d1 = format.parse(we1.getFrom());
                                                    Date d2 = format.parse(we1.getTo());
                                                    difference = difference + d1.getTime() - d2.getTime();
                                                }
                                            } catch (Exception exception) {
                                            }

                                        }
                                        for (Reservation r : reservations) {
                                            try {
                                                if(dateFormat.parse(r.getEventDate().toString()).equals(start)
                                                && r.getEmployees().contains(e.getId()))
                                                {
                                                    Date d1 = format.parse(r.getFromTime());
                                                    Date d2 = format.parse(r.getToTime());
                                                    difference = difference + d1.getTime() - d2.getTime();
                                                }
                                            } catch (Exception exception) {
                                            }

                                        }
                                        if (difference >= service.getMinDuration()*60*60*1000)
                                            return true;

                                        start = new Date(start.getTime() + (1000 * 60 * 60 * 24));
                                    }
                /*
                    if(weeklyEvents.stream().noneMatch(w -> w.getEmployeeId().equals(e.getId())) ||
                            weeklyEvents.stream().noneMatch(w1 -> {
                                try {
                                    return dateFormat.parse(w1.getDate()).getTime() < filters.startDate.getTime();
                                } catch (ParseException ex) {
                                    throw new RuntimeException(ex);
                                }
                            })  ||
                            weeklyEvents.stream().noneMatch(w2 -> {
                                try {
                                    return dateFormat.parse(w2.getDate()).getTime() > filters.endDate.getTime();
                                } catch (ParseException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }) ){
                        return true;
                    }

                 */
        if(weeklyEvents.isEmpty())
            return true;

        return false;
    }

    private void getWorkingCalendar()
    {
        WeeklyEventRepo weeklyEventRepo = new WeeklyEventRepo();
        WorkingTimeRepo workingTimeRepo = new WorkingTimeRepo();
        weeklyEventRepo.getAll(new WeeklyEventRepo.WeeklyEventFetchCallback() {
            @Override
            public void onWeeklyEventFetch(ArrayList<WeeklyEvent> events) {
                weeklyEvents = events;
            }

        });

        workingTimeRepo.getAll(new WorkingTimeRepo.WorkingTimeFetchCallback() {
        @Override
        public void onWorkingTimeFetch(ArrayList<WorkingTime> w) {
            workingTimes = w;
        }
    });
    }

    private void getAddresses()
    {
        AddressRepo repo = new AddressRepo();
        repo.getAll(new AddressRepo.AddressFetchCallback() {
            @Override
            public void onAddressFetch(ArrayList<Address> a) {
                AddressRepo.AddressFetchCallback.super.onAddressFetch(a);
                if(a != null && !a.isEmpty())
                    addresses = a;
            }
        });
    }

    private void getReservations()
    {
        ReservationRepo.getAllAccepted(new ReservationRepo.ReservationFetchCallback() {
            @Override
            public void onReservationFetch(ArrayList<Reservation> r) {
                ReservationRepo.ReservationFetchCallback.super.onReservationFetch(r);
                reservations = r;
            }
        });
    }
}

