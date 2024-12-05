package com.example.eventapp.fragments.packages;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.adapters.products.ProductPackageListAdapter;
import com.example.eventapp.adapters.services.ServicePackageListAdapter;
import com.example.eventapp.databinding.FragmentPackageDetailsBinding;
import com.example.eventapp.fragments.ChooseUserDialog;
import com.example.eventapp.fragments.CompanyInfoFragment;
import com.example.eventapp.fragments.FavouritesFragment;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.eventOrganizer.EventsForPackageFragment;
import com.example.eventapp.fragments.eventOrganizer.EventsForProductReservationFragment;
import com.example.eventapp.fragments.eventOrganizer.EventsForProductsPackageFragment;
import com.example.eventapp.fragments.eventOrganizer.EventsForServiceReservationFragment;
import com.example.eventapp.fragments.reservations.PackageReservationTimeFragment;
import com.example.eventapp.fragments.services.ServiceDetails;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.EventBudgetItem;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Favourites;
import com.example.eventapp.model.ItemType;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.ReservationStatus;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.ShowStatus;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.EventBudgetItemRepo;
import com.example.eventapp.repositories.EventBudgetRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.FavouritesRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PackageDetailsFragment extends Fragment implements EventsForPackageFragment.EventSelectionListener, PackageReservationTimeFragment.ReservationSelectionListener {

    private ArrayList<Reservation> created;
    private ArrayList<Notification> notifications;
    private PackageDetailsFragment thisFragment;
    private FragmentPackageDetailsBinding binding;
    private ProductPackageListAdapter productPackageListAdapter;
    private ServicePackageListAdapter servicePackageListAdapter;

    private ProductRepo productRepository=new ProductRepo();
    private ServiceRepo serviceRepository=new ServiceRepo();
    private TextView name;
    private TextView description;
    private TextView category;
    private TextView events;
    private TextView available;
    private TextView price;
    private TextView discount;
    private TextView discountPrice;
    private TextView visible;
    private TextView cancelationDeadline;
    private TextView reservationDeadlilne;
    private ListView productsList;
    private ListView servicesList;
    public ArrayList<Reservation> reservationsToCreate = new ArrayList<>();
    private ArrayList<Service> sendingServices;

    public boolean isHomePage = false;
    private Package paket;

    private Event SelectedEvent = null;
    @Override
    public void onEventSelected(Event selectedEvent) {
        // Ovde se obrađuje selektovani događaj
        SelectedEvent = selectedEvent;
        servicePackageListAdapter=new ServicePackageListAdapter(getActivity(),sendingServices, isHomePage, SelectedEvent, paket.getId(), this);
        servicesList.setAdapter(servicePackageListAdapter);
    }

    @Override
    public void onReservationSelected(Reservation selectedReservation) {
        reservationsToCreate.add(selectedReservation);
    }

    private List<Uri> selectedUriImages;
    private ArrayList<User> users = new ArrayList<>();


    public PackageDetailsFragment() {
        // Required empty public constructor
    }

    public static PackageDetailsFragment newInstance(Package p) {
        PackageDetailsFragment fragment = new PackageDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("PACKAGE", p);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            paket = getArguments().getParcelable("PACKAGE");
        }
        selectedUriImages = new ArrayList<>();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPackageDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        thisFragment = this;

        name=binding.packageName;
        description=binding.packageDescription;
        available=binding.packageAvailable;
        visible=binding.packageVisible;
        price=binding.packagePrice;
        category=binding.packageCategory;
        events=binding.packageEventType;
        discount=binding.packageDiscount;
        discountPrice=binding.packageDiscountPrice;
        cancelationDeadline=binding.packageCancelationDeadline;
        reservationDeadlilne=binding.packageReservationDeadline;
        productsList=root.findViewById(R.id.listPackages);
        servicesList=root.findViewById(R.id.listServices);

        SetData();

        List<Uri> selectedUriImages = new ArrayList<>();
        Button btnEdit = (Button) root.findViewById(R.id.btnEdit);
        Button btnDelete = (Button) root.findViewById(R.id.btnDelete);
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user != null)
        {
            UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                @Override
                public void onUserObjectFetched(User user, String errorResponse) {
                    if (user != null && user.isActive()) {
                        if(user.getType()!= UserType.OWNER) {
                            btnDelete.setVisibility(View.GONE);
                            btnEdit.setVisibility(View.GONE);
                        }

                    }
                }
            });
        }else{
            btnDelete.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
        }


        if(!isHomePage)
        {
            btnDelete.setOnClickListener(v -> {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                dialog.setMessage("Are you sure you want to delete this package?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PackageRepo.deletePacakge(paket.getId());
                                FragmentTransition.to(PackageListFragment.newInstance(),getActivity() ,
                                        true, R.id.scroll_package_list);
                            }

                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = dialog.create();
                alert.show();
            });
            btnEdit.setOnClickListener(v->{
                FragmentTransition.to(PackageFormFragment.newInstance(paket),getActivity(),true,R.id.scroll_package_list);
            });
        }


        //dugme za company info
        Button btnCompanyInfo = (Button) root.findViewById(R.id.btnCompanyInfo);
        if(isHomePage)
        {
            btnCompanyInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransition.to(CompanyInfoFragment.newInstance(paket.getFirmId()), getActivity(),
                            true, R.id.home_page_fragment);
                }
            });
        }else{
            btnCompanyInfo.setVisibility(View.GONE);
        }

        //button message
        Button btnMessage = (Button) binding.getRoot().findViewById(R.id.btnMessage);
        if(isHomePage && SharedPreferencesManager.getUserRole(getContext()).equals("ORGANIZER"))
        {
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChooseUserDialog dialog = ChooseUserDialog.newInstance(users);
                    dialog.show(getChildFragmentManager(), "CHAT");
                }
            });
        }else{
            btnMessage.setVisibility(View.GONE);
        }

        Button btnFavourite = binding.getRoot().findViewById(R.id.btnFavourite);
        if(isHomePage) {
            btnFavourite.setOnClickListener(v -> {
                    Favourites newFavourites = new Favourites();
                    newFavourites.getPackages().add(paket);
                    newFavourites.setUserEmail(SharedPreferencesManager.getEmail(getContext()));
                    Toast.makeText(getContext(), "Added to favourites!", Toast.LENGTH_SHORT).show();
                    FavouritesRepo favouritesRepo = new FavouritesRepo();
                    favouritesRepo.getByOrganizer(new FavouritesRepo.FavouriteFetchCallback() {
                        @Override
                        public void onFavouriteFetch(ArrayList<Favourites> favourites) {
                            if(favourites == null || favourites.isEmpty())
                                FavouritesRepo.create(newFavourites);
                            else
                            {
                                favourites.get(0).getPackages().add(paket);
                                FavouritesRepo.update(favourites.get(0));
                            }
                        }
                    }, SharedPreferencesManager.getEmail(getContext()));

                });
        }else
            btnFavourite.setVisibility(View.GONE);

        Button btnReservation = (Button) root.findViewById(R.id.btnReserve);
        btnReservation.setVisibility(View.GONE);
        if(SharedPreferencesManager.getUserRole(getContext()).equals(UserType.ORGANIZER.toString()))
        {

            btnReservation.setVisibility(View.VISIBLE);
        }


        btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // EventsForServiceReservationFragment dialog = EventsForServiceReservationFragment.newInstance(servis);
               // dialog.show(requireActivity().getSupportFragmentManager(), "EventsForServiceReservationFragment");

               if(paket.getServices()==null || paket.getServices().isEmpty()) { //ima samo proizvoda
                    //automatski se svi paketi kupuju:
                   EventsForProductsPackageFragment dialog = EventsForProductsPackageFragment.newInstance((ArrayList<String>) paket.getProducts(), paket.getId());
                   dialog.show(requireActivity().getSupportFragmentManager(), "EventsForProductsPackageFragment");

               }
               else {
                   if(SelectedEvent!=null && reservationsToCreate.size()==paket.getServices().size()) {    //dodati i ostale provjere
                        //rezerv paket i proizvode
                       createProductsAndPackageReservations();

                       ReservationRepo.createReservations(reservationsToCreate, new ReservationRepo.ReservationFetchCallback() {
                           @Override
                           public void onReservationObjectsFetched(ArrayList<Reservation> reservations, String errorMessage) {
                               if (errorMessage == null) {
                                   if(reservations!=null){
                                       created = reservations;

                                       for(Reservation r: reservations)
                                       {
                                           if(r.getProductId() == null || r.getProductId().isEmpty())
                                               continue;

                                           //add to budgeting
                                           EventBudgetRepo eventBudgetRepo = new EventBudgetRepo();
                                           eventBudgetRepo.getByEventId(new EventBudgetRepo.EventBudgetFetchCallback() {
                                               @Override
                                               public void onEventBudgetFetch(ArrayList<EventBudget> budgets) {
                                               }

                                               @Override
                                               public void onEventBudgetFetchByEvent(EventBudget budget) {

                                                   ProductRepo productRepo = new ProductRepo();
                                                   productRepo.getById(new ProductRepo.ProductFetchCallback() {
                                                       @Override
                                                       public void onProductFetch(ArrayList<Product> products) {
                                                           ProductRepo.ProductFetchCallback.super.onProductFetch(products);
                                                           EventBudgetItemRepo itemRepo = new EventBudgetItemRepo();
                                                           itemRepo.getByBudgetAndSubcategory(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
                                                               @Override
                                                               public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets) {
                                                                   if(budgets == null || budgets.isEmpty())
                                                                   {
                                                                       EventBudgetItem item = new EventBudgetItem();
                                                                       item.setPlannedBudget(0);
                                                                       ArrayList<String>ids = new ArrayList<>();
                                                                       ids.add(r.getProductId());
                                                                       item.setItemsIds(ids);
                                                                       item.setSubcategoryId(products.get(0).getSubcategory());

                                                                       itemRepo.create(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
                                                                           @Override
                                                                           public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets) {
                                                                               budget.getEventBudgetItemsIds().add(budgets.get(0).getId());
                                                                               EventBudgetRepo.update(budget);
                                                                           }
                                                                       }, item);
                                                                   }else{
                                                                       EventBudgetItem item = budgets.get(0);
                                                                       item.getItemsIds().add(r.getProductId());
                                                                       EventBudgetItemRepo.update(item);
                                                                   }
                                                               }
                                                           }, budget, products.get(0).getSubcategory());
                                                       }
                                                   }, r.getProductId());

                                               }
                                           }, r.eventId);

                                       }

                                       createNotifications();
                                       NotificationRepo.createNotifications(notifications, new NotificationRepo.NotificationFetchCallback() {
                                           @Override
                                           public void onNotificationObjectsFetched(ArrayList<Notification> notifications, String errorMessage) {
                                               getActivity().runOnUiThread(() -> {
                                                   if (errorMessage == null) {
                                                       Toast.makeText(getContext(), "Notifications successfully created.", Toast.LENGTH_SHORT).show();
                                                       // Uradite nešto sa notifikacijama ako je potrebno
                                                       Toast.makeText(getContext(), "Successfully reserved.", Toast.LENGTH_SHORT).show();
                                                   } else {
                                                       Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                                   }
                                               });
                                           }
                                       });
                                   }



                               }







                           }
                       });


                   }
                   else
                       Toast.makeText(getContext(), "Please choose event and other information.", Toast.LENGTH_SHORT).show();

               }
            }
        });

        if(!paket.getAvailable())
            btnReservation.setVisibility(View.GONE);

        Button btnEvent = (Button) binding.getRoot().findViewById(R.id.btnChooseEvent);
        btnEvent.setVisibility(View.GONE);
        if(SharedPreferencesManager.getUserRole(getContext()).equals(UserType.ORGANIZER.toString()) && paket.getServices()!=null && paket.getServices().size()!=0)
        {
            btnEvent.setVisibility(View.VISIBLE);
        }
        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsForPackageFragment dialog = EventsForPackageFragment.newInstance();
                dialog.listener = new EventsForPackageFragment.EventSelectionListener() {
                    @Override
                    public void onEventSelected(Event selectedEvent) {
                        // Handle the selected event here
                        SelectedEvent = selectedEvent;
                        servicePackageListAdapter=new ServicePackageListAdapter(getActivity(),sendingServices, isHomePage, SelectedEvent, paket.getId(),thisFragment);
                        servicesList.setAdapter(servicePackageListAdapter);
                    }
                };
                dialog.show(requireActivity().getSupportFragmentManager(), "EventsForPackageFragment");
            }
        });

        if(!paket.getAvailable())
            btnEvent.setVisibility(View.GONE);

        return root;

    }

    private void SetData(){
        name.setText(paket.getName());
        description.setText(paket.getDescription());
        if(paket.getAvailable())
            available.setText("Da");
        else
            available.setText("Ne");
        if(paket.getVisible())
            visible.setText("Da");
        else
            visible.setText("Ne");
        price.setText(paket.getPrice()+" din");
        CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> categories) {
                for(Category c:categories)
                    if(c.getId().equals(paket.getCategory()))
                        category.setText(c.getName());
            }
        });
        EventTypeRepo eventTypeRepo=new EventTypeRepo();
        eventTypeRepo.getAllActicateEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
            @Override
            public void onEventTypeFetch(ArrayList<EventType> eventTypes) {
                String event = "";
                if (eventTypes != null) {
                    for(EventType e:eventTypes)
                        for(String id:paket.getType())
                            if(e.getId().equals(id) && !event.contains(e.getName()+"   "))
                                event +=e.getName()+"   ";
                    events.setText(event);

                }
            }
        });
        discount.setText(paket.getDiscount()+" %");
        discountPrice.setText(Math.round(paket.getPrice()*(1-paket.getDiscount()/100))+" din");
        cancelationDeadline.setText(paket.getCancellationDeadline()+ "d");
        reservationDeadlilne.setText(paket.getReservationDeadline()+" d");



        productRepository.getAllProducts(new ProductRepo.ProductFetchCallback() {
            @Override
            public void onProductFetch(ArrayList<Product> products) {
                if (products != null) {
                    ArrayList<Product> packageProducts=new ArrayList<>();
                    for(String id:paket.getProducts()) {
                        for (Product product : products) {
                            if (product.getId().equals(id)) {
                                packageProducts.add(product);
                                getOwner(product);
                            }
                        }
                    }
                    productPackageListAdapter=new ProductPackageListAdapter(getActivity(),packageProducts, isHomePage);
                    productsList.setAdapter(productPackageListAdapter);
                }
            }
        });
        serviceRepository.getAllServices(new ServiceRepo.ServiceFetchCallback() {
            @Override
            public void onServiceFetch(ArrayList<Service> services) {
                if (services != null) {

                    ArrayList<Service> packageServices=new ArrayList<>();
                    for(String id:paket.getServices()) {
                        for (Service s : services) {
                            if (s.getId().equals(id)) {
                                packageServices.add(s);
                                getEmployees(s);
                            }
                        }
                    }
                    sendingServices = packageServices;
                    servicePackageListAdapter=new ServicePackageListAdapter(getActivity(),packageServices, isHomePage, SelectedEvent, paket.getId(), thisFragment);

                    servicesList.setAdapter(servicePackageListAdapter);
                }
            }
        });

    }

    private void getOwner(Product p)
    {
        OwnerRepo.getByFirmId(p.getId(), new OwnerRepo.OwnerFetchCallback() {
            @Override
            public void onOwnerFetch(ArrayList<Owner> o) {
                OwnerRepo.OwnerFetchCallback.super.onOwnerFetch(o);
                for(Owner o1: o){
                    if(users.stream().noneMatch(u -> u.getId().equals(o1.getId())))
                        users.add(o1);
                }
            }
        });
    }

    private void getEmployees(Service s)
    {
        EmployeeRepo.getByIds(s.getAttendants(), new EmployeeRepo.EmployeeFetchCallback() {
            @Override
            public void onEmployeeFetch(ArrayList<Employee> o) {
                EmployeeRepo.EmployeeFetchCallback.super.onEmployeeFetch(o);

                for(Employee o1: o){
                    if(users.stream().noneMatch(u -> u.getId().equals(o1.getId())))
                        users.add(o1);
                }
            }
        });
    }

    private void createProductsAndPackageReservations() {
        ArrayList<Reservation> reservations = new ArrayList<>();

        for(String id: paket.getProducts()) {
            Reservation reservation = new Reservation();
            reservation.setStatus(ReservationStatus.ACCEPTED);
            reservation.setType(ItemType.Package);
            reservation.setProductId(id);
            reservation.setPackageId(paket.getId());
            reservation.setServiceId("");
            reservation.setCreatedDate((new Date()).toString());
            reservation.setOrganizerEmail(SharedPreferencesManager.getEmail(getContext()));
            reservation.setEventId(SelectedEvent.getId());
            reservation.setEmployees(new ArrayList<>());
            reservation.setEventDate(SelectedEvent.getDate());
            reservation.setFirmId(paket.getFirmId());
            reservations.add(reservation);
        }
        //rezervacija samog paketa
        Reservation fullPackage = new Reservation();
        fullPackage.setStatus(ReservationStatus.NEW);
        fullPackage.setType(ItemType.Package);
        fullPackage.setProductId("");
        fullPackage.setPackageId(paket.getId());
        fullPackage.setServiceId("");
        fullPackage.setCreatedDate((new Date()).toString());
        fullPackage.setOrganizerEmail(SharedPreferencesManager.getEmail(getContext()));
        fullPackage.setEventId(SelectedEvent.getId());
        fullPackage.setEmployees(new ArrayList<>());
        fullPackage.setEventDate(SelectedEvent.getDate());
        fullPackage.setFirmId(paket.getFirmId());



        for(Reservation res: reservations) {
            reservationsToCreate.add(res);
        }

        //dodajem employee
        ArrayList<String> employeesIds = new ArrayList<>();
        for(Reservation res: reservationsToCreate) {
            if(res.getServiceId()!=null && !res.getServiceId().equals("")) {
                employeesIds.add(res.getEmployees().get(0));
            }
        }
        Set<String> uniqueEmployeesIds = new HashSet<>(employeesIds);
        employeesIds = new ArrayList<>(uniqueEmployeesIds);
        fullPackage.setEmployees(employeesIds);
        reservations.add(fullPackage);
        reservationsToCreate.add(fullPackage);

    }

    private void createNotifications() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        notifications = new ArrayList<>();
        //notifikacija za employea
        for(Reservation res: created) {
            if(!res.getServiceId().equals("")) {
                Notification newNotificationEmployee = new Notification();
                newNotificationEmployee.setMessage("1 new service in package reservation. ");
                newNotificationEmployee.setReceiverRole(UserType.EMPLOYEE);
                newNotificationEmployee.setSenderId(mAuth.getCurrentUser().getUid());
                newNotificationEmployee.setReceiverId(res.getEmployees().get(0));
                newNotificationEmployee.setShowStatus(ShowStatus.UNSHOWED);
                newNotificationEmployee.setDate(new Date().toString());
                notifications.add(newNotificationEmployee);

                Notification newNotificationOrganizer = new Notification();
                newNotificationOrganizer.setMessage("Reservation of service in 1 hour.\n" + res.getEventDate() + "-" + res.getFromTime() + "-" + res.getId());
                newNotificationOrganizer.setDate(new Date().toString());
                newNotificationOrganizer.setReceiverRole(UserType.ORGANIZER);
                newNotificationOrganizer.setSenderId(res.getEmployees().get(0));
                newNotificationOrganizer.setReceiverId(mAuth.getCurrentUser().getUid());
                newNotificationOrganizer.setShowStatus(ShowStatus.UNSHOWED);
                notifications.add(newNotificationOrganizer);
            }
        }


    }


}