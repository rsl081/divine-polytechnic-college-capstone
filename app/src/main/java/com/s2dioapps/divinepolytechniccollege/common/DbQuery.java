package com.s2dioapps.divinepolytechniccollege.common;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.s2dioapps.divinepolytechniccollege.login.ProfileModel;
import com.s2dioapps.divinepolytechniccollege.ui.category.CategoryModel;
import com.s2dioapps.divinepolytechniccollege.ui.leaderboard.RankModel;
import com.s2dioapps.divinepolytechniccollege.ui.lesson.LessonModel;
import com.s2dioapps.divinepolytechniccollege.ui.module.ModuleModel;
import com.s2dioapps.divinepolytechniccollege.ui.module.UserLesson;
import com.s2dioapps.divinepolytechniccollege.ui.question.QuestionModel;
import com.s2dioapps.divinepolytechniccollege.ui.test.TestModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DbQuery {

    public static FirebaseFirestore g_firestore;

    public static List<CategoryModel> g_catList = new ArrayList<>();
    public static int g_selected_cat_index = 0;
    public static List<TestModel> g_testList = new ArrayList<>();

    public static List<LessonModel> g_leList = new ArrayList<>();
    public static int g_selected_lesson_index = 0;
    public static List<ModuleModel> g_moduleList = new ArrayList<>();

    public static ProfileModel myProfile = new ProfileModel(null,"NA",null);

    public static int g_selected_test_index = 0;
    public static List<QuestionModel> g_questList = new ArrayList<>();
    public static  List<RankModel> g_usersList = new ArrayList<>();
    public  static int g_usersCount = 0;
    public static boolean isMeOnTopList = false;

    public static RankModel myPerformance = new RankModel("",0,-1);
    static int value = 0;

    public static List<UserLesson> g_userlesson = new ArrayList<>();

    public static void CountLessons(MyCompleteListener completeListener)
    {
        g_leList.clear();

        g_firestore = FirebaseFirestore.getInstance();

        g_firestore.collection("Lessons").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();

                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                        {
                            docList.put(doc.getId(), doc);
                        }

                        QueryDocumentSnapshot catListDoc = docList.get("SUBJECTS");

                        long catCount = catListDoc.getLong("COUNT");

                        WriteBatch batch = DbQuery.g_firestore.batch();

                        DocumentReference userDoc = DbQuery.g_firestore.collection("Users").document(FirebaseAuth.getInstance().getUid());

                        DocumentReference lessonDoc = userDoc.collection("USER_DATA")
                                .document("LESSONS");

                        Map<String, Object> lessonData = new ArrayMap<>();

                        for(int i = 1; i <= catCount; i++)
                        {

                            String catID = catListDoc.getString("SUB" + String.valueOf(i) + "_ID");

                            QueryDocumentSnapshot catDoc = docList.get(catID);

                            int noOfTest = catDoc.getLong("NO_OF_MODULES").intValue();

                            String catName = catDoc.getString("NAME");

                            g_leList.add(new LessonModel(catID, catName, noOfTest));

                            lessonData.put(catName, 1);

                        }
                        lessonData.put("Count", catCount);

                        batch.set(lessonDoc, lessonData);
                        batch.commit();

                        completeListener.onSuccess();

                    }
                });


    }


    public static void loadLessons(MyCompleteListener completeListener) {

        g_leList.clear();

        g_firestore = FirebaseFirestore.getInstance();

        g_firestore.collection("Lessons").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();

                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                        {
                            docList.put(doc.getId(), doc);
                        }

                        QueryDocumentSnapshot catListDoc = docList.get("SUBJECTS");

                        long catCount = catListDoc.getLong("COUNT");

                        WriteBatch batch = DbQuery.g_firestore.batch();

                        DocumentReference userDoc = DbQuery.g_firestore.collection("Users").document(FirebaseAuth.getInstance().getUid());

                        DocumentReference lessonDoc = userDoc.collection("USER_DATA")
                                .document("LESSONS");

                        Map<String, Object> lessonData = new ArrayMap<>();


//                        for(int i = 1; i <= catCount; i++)
//                        {
//
//                            String catID = catListDoc.getString("SUB" + String.valueOf(i) + "_ID");
//
//                            QueryDocumentSnapshot catDoc = docList.get(catID);
//
//                            int noOfTest = catDoc.getLong("NO_OF_MODULES").intValue();
//
//                            String catName = catDoc.getString("NAME");
//
//                            //g_leList.add(new LessonModel(catID, catName, noOfTest));
//
//                            lessonData.put(catName, 1);
//
//
//                        }
//                        lessonData.put("Count", catCount);
//
//                        batch.update(lessonDoc, lessonData);
//                        batch.commit();

                        lessonDoc
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                WriteBatch batch1 = DbQuery.g_firestore.batch();

                                if(documentSnapshot.exists()) {
                                    for(int i = 1; i <= catCount; i++)
                                    {

                                        String catID = catListDoc.getString("SUB" + String.valueOf(i) + "_ID");

                                        QueryDocumentSnapshot catDoc = docList.get(catID);

                                        int noOfTest = catDoc.getLong("NO_OF_MODULES").intValue();

                                        String catName = catDoc.getString("NAME");

                                        g_leList.add(new LessonModel(catID, catName, noOfTest));

                                        if(documentSnapshot.getLong(catName) == null)
                                        {
                                            lessonData.put(catName, 1);
                                        }else if(documentSnapshot.getLong(catName).intValue() <= 1){
                                            lessonData.put(catName, 1);
                                        }else{
                                            lessonData.put(catName, documentSnapshot.getLong(catName).intValue());
                                        }




                                    }
                                    lessonData.put("Count", catCount);

                                    batch1.set(lessonDoc, lessonData);
                                    batch1.commit();

                                    loadUserLesson(new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            completeListener.onSuccess();
                                        }

                                        @Override
                                        public void onFailure() {

                                            completeListener.onFailure();

                                        }
                                    });


                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                    Log.e("BEST", "On failuree");
                            }
                        });

                        completeListener.onSuccess();

                    }
                });


    }

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

    public static void getUserData(MyCompleteListener completeListener)
    {
        g_firestore.collection("Users").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        myProfile.setName(documentSnapshot.getString(NodeNames.NAME));
                        myProfile.setEmail(documentSnapshot.getString(NodeNames.EMAIL));
                        myProfile.setPhoto(documentSnapshot.getString(NodeNames.PHOTO));

                        myPerformance.setScore(Objects.requireNonNull(documentSnapshot.getLong("TOTAL_SCORE")).intValue());

                        myPerformance.setName(documentSnapshot.getString(NodeNames.NAME));

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

    public static void loadData(MyCompleteListener completeListener)
    {
        loadCategories(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                getUserData(new MyCompleteListener() {
                    @Override
                    public void onSuccess() {

                        getUsersCount(completeListener);
//                        loadTestData(completeListener);
//                        loadLessons(completeListener);
//                        loadUserLesson(completeListener);
                        loadTestData(new MyCompleteListener() {
                            @Override
                            public void onSuccess() {

                                loadLessons(new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {

//                                        loadUserLesson(new MyCompleteListener() {
//                                            @Override
//                                            public void onSuccess() {
//                                                completeListener.onSuccess();
//                                            }
//
//                                            @Override
//                                            public void onFailure() {
//
//                                                completeListener.onFailure();
//
//                                            }
//                                        });

                                        completeListener.onSuccess();
                                    }

                                    @Override
                                    public void onFailure() {
                                        completeListener.onFailure();
                                    }
                                });

                                completeListener.onSuccess();
                            }

                            @Override
                            public void onFailure() {
                                completeListener.onFailure();
                            }
                        });



                    }

                    @Override
                    public void onFailure() {
                        completeListener.onFailure();
                    }
                });
            }

            @Override
            public void onFailure() {
                completeListener.onFailure();
            }
        });


    }



    public static void loadUserLesson(MyCompleteListener completeListener)
    {
        g_userlesson.clear();

        g_firestore = FirebaseFirestore.getInstance();

        g_firestore.collection("Users").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("LESSONS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        int countAll = documentSnapshot.getLong("Count").intValue();
                        int count = documentSnapshot.getData().size();

                        WriteBatch batch = DbQuery.g_firestore.batch();

                        DocumentReference userDoc = DbQuery.g_firestore.collection("Users").document(FirebaseAuth.getInstance().getUid());

                        DocumentReference lessonDoc = userDoc.collection("USER_DATA")
                                .document("LESSONS");

                        Map<String, Object> lessonData = new ArrayMap<>();

                        for(int i = 0; i < countAll; i++)
                        {
                            g_userlesson.add(new UserLesson(
                                    documentSnapshot.getLong(g_leList.get(i).getName())
                                            .intValue()));

//                            if(documentSnapshot.getLong(g_leList.get(i).getName()).intValue() <= 1)
//                            {
//                            }
                        }

//                        lessonData.put("Math", 1);
//                        lessonData.put("English", 1);
//                        lessonData.put("Count", countAll);
//                        batch.set(lessonDoc, lessonData);
//                        batch.commit()
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//
//                                    }
//                                });


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


    public static void loadModuleData(MyCompleteListener completeListener)
    {
        g_moduleList.clear();

        g_firestore = FirebaseFirestore.getInstance();

        g_firestore.collection("Lessons").document(g_leList.get(g_selected_lesson_index)
                .getDocID()).collection("MODULE_LIST").document("MODULE_INFO")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        int noOfTests = g_leList.get(g_selected_lesson_index).getOnOfModules();

                        for(int i = 1; i <= noOfTests; i++)
                        {
                            g_moduleList.add(new ModuleModel(
                                    documentSnapshot.getString("MODULE"+ String.valueOf(i) + "_ID"),
                                    documentSnapshot.getString("MODULE"+ String.valueOf(i) + "_PDF"),
                                    documentSnapshot.getLong("COUNT").intValue()
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

    public static void loadQuestions(MyCompleteListener completeListener)
    {
        g_questList.clear();
        g_firestore.collection("Question")
                .whereEqualTo("CATEGORY", g_catList.get(g_selected_cat_index).getDocID())
                .whereEqualTo("TEST", g_testList.get(g_selected_test_index).getTestID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc: queryDocumentSnapshots)
                        {
                            g_questList.add(new QuestionModel(
                                    doc.getString("QUESTION"),
                                    doc.getString("A"),
                                    doc.getString("B"),
                                    doc.getString("C"),
                                    doc.getString("D"),
                                    doc.getLong("ANSWER").intValue(),
                                    -1
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

    public static void loadMyScores(MyCompleteListener completeListener)
    {
        g_firestore.collection("Users").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_SCORES")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        for(int i = 0; i < g_testList.size(); i++)
                        {
                            int top = 0;
                            if(documentSnapshot.get(g_testList.get(i).getTestID()) != null)
                            {
                                top = documentSnapshot.getLong(g_testList.get(i).getTestID()).intValue();
                            }

                            g_testList.get(i).setTopScore(top);
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

    public static void getTopUsers(MyCompleteListener completeListener)
    {
        g_usersList.clear();

        String myUID = FirebaseAuth.getInstance().getUid();

        g_firestore.collection("Users")
                .whereGreaterThan(NodeNames.TOTAL_SCORE,0)
                .orderBy(NodeNames.TOTAL_SCORE, Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        int rank = 1;

                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                        {
                            g_usersList.add(new RankModel(
                                    doc.getString("NAME"),
                                    doc.getLong(NodeNames.TOTAL_SCORE).intValue(),
                                    rank
                            ));

                            if(myUID.compareTo(doc.getId()) == 0)
                            {
                                isMeOnTopList = true;
                                myPerformance.setRank(rank);
                            }

                            rank++;
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

    public static void getUsersCount(MyCompleteListener completeListener)
    {
        g_firestore.collection("Users").document(NodeNames.TOTAL_USERS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        g_usersCount = documentSnapshot.getLong("COUNT").intValue();

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

    public static void saveModuleCount(int count)
    {
        count++;
        g_firestore = FirebaseFirestore.getInstance();

//        g_firestore.collection("Lessons").document(g_leList.get(g_selected_lesson_index)
//                .getDocID()).collection("MODULE_LIST").document("MODULE_INFO")
//                .update("COUNT",count);

        g_firestore.collection("Users").document(FirebaseAuth.getInstance().getUid())
                        .collection("USER_DATA").document("LESSONS")
                        .update(g_leList.get(g_selected_lesson_index).getName(),count);


    }

//    public static void count



    public static void saveResult(int score, MyCompleteListener completeListener)
    {
        WriteBatch batch = DbQuery.g_firestore.batch();


        if(score > DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTopScore())
        {
            DocumentReference userDoc = DbQuery.g_firestore.collection("Users").document(FirebaseAuth.getInstance().getUid());
            value = value + score;
            batch.update(userDoc, "TOTAL_SCORE", value);

            DocumentReference scoreDoc = userDoc.collection("USER_DATA")
                    .document("MY_SCORES");

            Map<String, Object> testData = new ArrayMap<>();
            testData.put(g_testList.get(g_selected_test_index).getTestID(), score);

            batch.set(scoreDoc, testData, SetOptions.merge());

        }

        batch.commit()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    if(score > g_testList.get(g_selected_test_index).getTopScore())
                    {
                        g_testList.get(g_selected_test_index).setTopScore(score);
                    }

                    myPerformance.setScore(score);

                    completeListener.onSuccess();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
    }




}
