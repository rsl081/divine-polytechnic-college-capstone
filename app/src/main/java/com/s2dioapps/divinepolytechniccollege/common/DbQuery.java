package com.s2dioapps.divinepolytechniccollege.common;

import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.s2dioapps.divinepolytechniccollege.ui.category.CategoryModel;
import com.s2dioapps.divinepolytechniccollege.ui.test.TestModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbQuery {

    public static FirebaseFirestore g_firestore;
    public static List<CategoryModel> g_catList = new ArrayList<>();
    public static int g_selected_cat_index = 0;
    public static List<TestModel> g_testList = new ArrayList<>();



    public static void loadCategories(MyCompleteListener completeListener)
    {
        g_catList.clear();

        g_firestore = FirebaseFirestore.getInstance();

        g_firestore.collection("Quiz").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();

                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                        {
                            docList.put(doc.getId(), doc);
                        }

                        QueryDocumentSnapshot catListDoc = docList.get("Categories");

                        long catCount = catListDoc.getLong("COUNT");

                        for(int i = 1; i <= catCount; i++)
                        {
                            String catID = catListDoc.getString("CAT" + String.valueOf(i) + "_ID");

                            QueryDocumentSnapshot catDoc = docList.get(catID);

                            int noOfTest = catDoc.getLong("NO_OF_TESTS").intValue();

                            String catName = catDoc.getString("NAME");

                            g_catList.add(new CategoryModel(catID, catName, noOfTest));

                        }

                        completeListener.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }

    public static void loadTestData(MyCompleteListener completeListener)
    {
        g_testList.clear();

        g_firestore = FirebaseFirestore.getInstance();

        g_firestore.collection("Quiz").document(g_catList.get(g_selected_cat_index)
                .getDocID()).collection("TEST_LIST").document("TEST_INFO")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        int noOfTests = g_catList.get(g_selected_cat_index).getOnOfTests();

                        for(int i = 1; i <= noOfTests; i++)
                        {
                            g_testList.add(new TestModel(
                                    documentSnapshot.getString("TEST"+ String.valueOf(i) + "_ID"),
                                    0,
                                    documentSnapshot.getLong("TEST"+ String.valueOf(i) + "_TIME").intValue()
                            ));

                        }

                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }


}
