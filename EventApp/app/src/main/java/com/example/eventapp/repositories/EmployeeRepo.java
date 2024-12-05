package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.R;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Report;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.User;
import com.example.eventapp.model.WorkingTime;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EmployeeRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createEmployee(Employee newEmployee, EmployeeRepo.EmployeeFetchCallback callback) {
        db.collection("employees")
                .document(newEmployee.getId())
                .set(newEmployee)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newEmployee.getId());
                    db.collection("users")
                            .document(newEmployee.getId())
                            .set((User)newEmployee)
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newEmployee.getId());
                                callback.onEmployeeObjectFetched(newEmployee, null);
                            })
                            .addOnFailureListener(e -> {
                                Log.w("REZ_DB", "Error adding document", e);
                                callback.onEmployeeObjectFetched(null, e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onEmployeeObjectFetched(null, e.getMessage());
                });
    }

    public static void deleteEmployee(String id){
        DocumentReference docRef = db.collection("employees").document(id);
        docRef.update("deleted", true)
                .addOnSuccessListener(aVoid -> Log.d("REZ_DB", "Employee successfully changed"))
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error getting documents.", e));
    }
    public static void update(Employee p) {
        db.collection("employees")
                .document(p.getId())
                .set(p)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + p.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                    }
                });
    }
    public static void getAll(EmployeeFetchCallback callback) {
        ArrayList<Employee> employees = new ArrayList<>();

        db.collection("employees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Employee employee = document.toObject(Employee.class);

                                employees.add(employee);

                        }
                        callback.onEmployeeFetch(employees);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        callback.onEmployeeFetch(null);
                    }
                });
    }

    public interface EmployeeFetchCallback {
        default void onEmployeeFetch(ArrayList<Employee> employees) {

        }
        default void onEmployeeObjectFetched(Employee employee, String errorMessage) {

        }
        default void onUpdateSuccess() {}
        default void onUpdateFailure(String errorMessage) {}
    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }
    public static void getByFirmId(String firmId, EmployeeFetchCallback callback) {
        Query query = db.collection("employees").whereEqualTo("firmId", firmId);
        ArrayList<Employee> employees = new ArrayList<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Employee employee = document.toObject(Employee.class);
                    employees.add(employee);
                }
                callback.onEmployeeFetch(employees);

            } else {
                Log.d("OwnerRepo", "Error getting owners by firmId", task.getException());
                callback.onEmployeeFetch(null);
            }
        });
    }

    public static void getByIds(List<String> ids, EmployeeFetchCallback callback) {

        if (ids == null || ids.isEmpty()) {

            callback.onEmployeeFetch(new ArrayList<>());
            return;
        }

        Query query = db.collection("employees").whereIn("id", ids);
        ArrayList<Employee> employees = new ArrayList<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Employee employee = document.toObject(Employee.class);
                    employees.add(employee);
                }
                callback.onEmployeeFetch(employees);

            } else {
                Log.d("OwnerRepo", "Error getting owners by firmId", task.getException());
                callback.onEmployeeFetch(null);
            }
        });
    }

    public static void getByEmail(String email, EmployeeRepo.EmployeeFetchCallback callback) {
        db.collection("employees")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Employee user = documentSnapshot.toObject(Employee.class);
                        callback.onEmployeeObjectFetched(user, null);
                    } else {
                        callback.onEmployeeObjectFetched(null, "User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onEmployeeObjectFetched(null, e.getMessage());
                });
    }
    public static void getById(String id, EmployeeRepo.EmployeeFetchCallback callback) {
        db.collection("employees")
                .whereEqualTo("id", id)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Employee user = documentSnapshot.toObject(Employee.class);
                        callback.onEmployeeObjectFetched(user, null);
                    } else {
                        callback.onEmployeeObjectFetched(null, "User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onEmployeeObjectFetched(null, e.getMessage());
                });
    }
   /* public static void updateEmployee(Employee employee, String id,EmployeeRepo.EmployeeFetchCallback callback) {
        db.collection("employees")
                .document(employee.getId())
                .set(employee)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + employee.getId());
                        callback.onUpdateSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                        callback.onUpdateFailure("Failed");
                    }
                });
    }*/

    public static void updateEmployee(Employee employee, String oldEmployeeId, EmployeeRepo.EmployeeFetchCallback callback) {
        DocumentReference oldEmployeeRef = db.collection("employees").document(oldEmployeeId);

        oldEmployeeRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> employeeData = documentSnapshot.getData();
                    //employeeData.put("id",employee.getId());
                    employeeData.put("deactivated",employee.isDeactivated());
                    DocumentReference newEmployeeRef = db.collection("employees").document(employee.getId());
                    Log.i("EventApp","repo: "+employeeData.get("deactivated")+" "+employeeData.get("id"));
                    newEmployeeRef.set(employeeData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            oldEmployeeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    updateEmployeeAddress(oldEmployeeId, employee.getId(), callback);
                                    callback.onUpdateSuccess();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("REZ_DB", "Error deleting old employee document", e);
                                    callback.onUpdateFailure("Failed");
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("REZ_DB", "Error setting data to new employee document", e);
                            callback.onUpdateFailure("Failed");
                        }
                    });
                } else {
                    Log.d("REZ_DB", "Old employee document does not exist");
                    callback.onUpdateFailure("Old employee document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Greška prilikom čitanja starog dokumenta
                Log.w("REZ_DB", "Error reading old employee document", e);
                callback.onUpdateFailure("Failed");
            }
        });
    }

    public static void getActiveByFirmId(String firmId, EmployeeFetchCallback callback) {
        Query query = db.collection("employees").whereEqualTo("firmId", firmId);
        ArrayList<Employee> employees = new ArrayList<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Employee employee = document.toObject(Employee.class);
                    if(employee.isActive())
                        employees.add(employee);
                }
                callback.onEmployeeFetch(employees);

            } else {
                Log.d("OwnerRepo", "Error getting owners by firmId", task.getException());
                callback.onEmployeeFetch(null);
            }
        });
    }

    /*public static void updateEmployee(Employee employee, String id, EmployeeRepo.EmployeeFetchCallback callback) {
        db.collection("employees")
                .document(id)
                .set(employee)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + employee.getId());
                        updateEmployeeAddress(id, employee.getId(), callback);
                        callback.onUpdateSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                        callback.onUpdateFailure("Failed");
                    }
                });
    }*/

    private static void updateEmployeeAddress(String idForChange, String employeeId,EmployeeRepo.EmployeeFetchCallback callback) {
        // Ažuriranje adresa
        db.collection("addresses")
                .whereEqualTo("userId", idForChange)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = db.batch();
                        Log.i("Addresses","koliko "+queryDocumentSnapshots.size());
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            batch.update(documentSnapshot.getReference(), "userId", employeeId);
                        }
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("REZ_DB", "Employee id updated successfully "+employeeId);
                                        updateEmployeeWorkHours(idForChange,employeeId, callback);
                                        callback.onUpdateSuccess();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("REZ_DB", "Error updating employee id", e);
                                        callback.onUpdateFailure("Failed");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error fetching employee id", e);
                        callback.onUpdateFailure("Failed");
                    }
                });
    }

    private static void updateEmployeeWorkHours(String idForChange,String employeeId, EmployeeRepo.EmployeeFetchCallback callback) {
        // Ažuriranje usluga
        db.collection("working_times")
                .whereEqualTo("userId", idForChange)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = db.batch();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            batch.update(documentSnapshot.getReference(), "userId", employeeId);
                        }
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("REZ_DB", "Employee working hours updated successfully");
                                        updateEmployeeServices(idForChange,employeeId, callback);
                                        callback.onUpdateSuccess();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("REZ_DB", "Error updating employee services", e);
                                        callback.onUpdateFailure("Failed");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error fetching employee services", e);
                        callback.onUpdateFailure("Failed");
                    }
                });
    }

    private static void updateEmployeeServices(String idForChange, String employeeId, EmployeeRepo.EmployeeFetchCallback callback) {
        // Ažuriranje usluga
        db.collection("services")
                .whereArrayContains("attendants", idForChange)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = db.batch();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            List<String> attendants = (List<String>) documentSnapshot.get("attendants");
                            if (attendants != null) {
                                int index = attendants.indexOf(idForChange);
                                if (index != -1) {
                                    attendants.set(index, employeeId);
                                    batch.update(documentSnapshot.getReference(), "attendants", attendants);
                                }
                            }
                        }
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("REZ_DB", "Employee service updated successfully");
                                        callback.onUpdateSuccess();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("REZ_DB", "Error updating employee service", e);
                                        callback.onUpdateFailure("Failed");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error fetching employee service", e);
                        callback.onUpdateFailure("Failed");
                    }
                });
    }


}
