package com.example.eventapp.fragments.pricelists;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventapp.PdfExporter;
import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentPriceListBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.PdfItem;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class PriceList extends Fragment {

    private FragmentPriceListBinding binding;

    public PriceList() {
    }

    public static PriceList newInstance() {
        PriceList fragment = new PriceList();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPriceListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button onProducts=binding.ProductsPriceList;
        Button onServices=binding.ServicePriceList;
        Button onPackages=binding.PackagePriceList;
        Button onPdf=binding.pdf;

        onProducts.setOnClickListener(v->{
             FragmentTransition.to(PriceListProduct.newInstance(), getActivity(),
                            false, R.id.scroll_pp_list);

        });

        onServices.setOnClickListener(s->{
            FragmentTransition.to(PriceListService.newInstance(), getActivity(),
                    false, R.id.scroll_pp_list);
        });

        onPackages.setOnClickListener(p->{
            FragmentTransition.to(PriceListPackage.newInstance(), getActivity(),
                    false, R.id.scroll_pp_list);
        });


        onPdf.setOnClickListener(pdf->{
            Toast.makeText(this.getContext(), "PDF is exported", Toast.LENGTH_SHORT).show();

            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "izvestaj"+new Random().nextInt(101)+".pdf";

            ArrayList<PdfItem> allPricelists=new ArrayList<>();
            ProductRepo productRepo=new ProductRepo();
            final int[] i = {1};
            productRepo.getAllProducts(new ProductRepo.ProductFetchCallback() {
                @Override
                public void onProductFetch(ArrayList<Product> products) {
                    ProductRepo.ProductFetchCallback.super.onProductFetch(products);
                    if (products != null) {
                       for(Product product:products){
                          PdfItem pdfItem=new PdfItem();
                          pdfItem.setPrice(product.getPrice());
                          pdfItem.setDiscount(product.getDiscount());
                          pdfItem.setDiscountPrice(Math.round(product.getPrice()*(1-product.getDiscount()/100)));
                          pdfItem.setName(product.getName());
                          pdfItem.setType("proizvod");
                          pdfItem.setNumber(i[0]);
                          i[0]++;
                          allPricelists.add(pdfItem);
                       }
                    }
                    ServiceRepo serviceRepo=new ServiceRepo();
                    serviceRepo.getAllServices(new ServiceRepo.ServiceFetchCallback() {
                        @Override
                        public void onServiceFetch(ArrayList<Service> services) {
                            ServiceRepo.ServiceFetchCallback.super.onServiceFetch(services);
                            for(Service service:services){
                                PdfItem pdfItem=new PdfItem();
                                pdfItem.setPrice(service.getPrice());
                                pdfItem.setDiscount(service.getDiscount());
                                pdfItem.setDiscountPrice(Math.round(service.getPrice()*(1-service.getDiscount()/100)));
                                pdfItem.setName(service.getName());
                                pdfItem.setType("usluga");
                                pdfItem.setNumber(i[0]);
                                i[0]++;
                                allPricelists.add(pdfItem);
                            }


                            PackageRepo packageRepo=new PackageRepo();
                            packageRepo.getAllPackages(new PackageRepo.PackageFetchCallback() {
                                @Override
                                public void onPackageFetch(ArrayList<Package> packages) {
                                    for(Package p:packages){
                                            PdfItem pdfItem=new PdfItem();
                                            pdfItem.setPrice(p.getPrice());
                                            pdfItem.setDiscount(p.getDiscount());
                                            pdfItem.setDiscountPrice(Math.round(p.getPrice()*(1-p.getDiscount()/100)));
                                            pdfItem.setName(p.getName());
                                            pdfItem.setType("paket");
                                            pdfItem.setNumber(i[0]);
                                            i[0]++;
                                            allPricelists.add(pdfItem);
                                        }

                                    PdfExporter.exportToPdf(allPricelists,filePath);
                                }
                            });
                        }
                    });
                }
            });

        });




        return root;
    }


}