package io.playtext.dianux;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class StoreActivity extends AppCompatActivity {
    public static final String TAG = "StoreActivity";
    private static final int DEFAULT_LIMIT = 20;
    public static final String STORE_CSV = "Store.csv";
    public static int DEFAULT_OFFSET = 0;
    public static int[] LIMIT_LIST = {20, 50, 100};

    public void setMerged_queries(Cache cache, String merged_queries) {
        cache.putString("merged_queries", merged_queries);
    }

    public String getMerged_queries(Cache cache) {
        return cache.getString("merged_queries");
    }


    public StoreActivity storeActivity;
    protected List<Store> list = new ArrayList<>();

    private ProgressBar progressBar;

    public int count;

    public String space;


    public Toolbar toolbar;
    public TextView textView_limit, textView_offset, textView_sort;
    public ImageView imageView_sort_arrow, imageView_next, imageView_previous, imageView_limit;

    private String back_to;

    public String getBack_to() {
        return back_to;
    }

    public void setBack_to(String back_to) {
        this.back_to = back_to;
    }

    //    public View constraintLayout_ordering_and_sort, constraintLayout_database_navigation;
    public CheckBox checkBox_order;
    public RecyclerView recyclerView;

    public OffsetSetter offsetSetter;

    public LimitSetter limitSetter;


    public Cache cache_StoreActivity, cache_TextsActivity;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setSort(Cache cache, String sort) {
        cache.putString("sort", sort);
    }

    public String getSort(Cache cache) {
        return cache.getString("sort");
    }

    public void setOrder(Cache cache, String order) {
        cache.putString("order", order);
    }

    public String getOrder(Cache cache) {
        return cache.getString("order");
    }

    public void setLimit(Cache cache, int limit) {
        cache.putInt("limit", limit);
    }

    public int getLimit(Cache cache) {
        return cache.getInt("limit", DEFAULT_LIMIT);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        storeActivity = this;


        cache_StoreActivity = new Cache(TAG, this);



        space = getString(R.string.space);

        progressBar = findViewById(R.id.activity_store_progressBar);


        setMerged_queries(cache_StoreActivity, "SELECT * FROM" + space + StoreDB.TABLE_NAME);


        getExtra();



        rxSettersInitiation();



        setCount(itemsCountStore());
        myViewsInitiations();


        runOnlyOnce();


        myListeners();



        fillUpStoreDb();



        loadDatabase();
    }

    public void rxSettersInitiation() {


        limitSetter = new LimitSetter();
        limitSetter.getModelChanges()
                .subscribe(new Observer<LimitSetter>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(LimitSetter limitSetter) {
                        textView_limit.setText(limitSetter.itemsPerPage);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });


        offsetSetter = new OffsetSetter();
        offsetSetter.getModelChanges()
                .subscribe(new Observer<OffsetSetter>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(OffsetSetter offsetSetter) {
                        textView_offset.setText(offsetSetter.offset);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }


    private void runOnlyOnce() {
        Cache runOnlyOnce = new Cache("runOnlyOnce", this);
        boolean isFirstRun = runOnlyOnce.getBoolean("Dashboard_Activity_First_Run", true);

        if (isFirstRun) {
            // Code to run once -----------------------------------------------------------------

            setSort(cache_StoreActivity, "status");
            setOrder(cache_StoreActivity, "DESC");


            Log.i(TAG, "signOut 5: signOutAskDialog: createEmptyDbs:");


            // End of Code to run once
            // ----------------------------------------------------------------------------------
            runOnlyOnce.putBoolean("Dashboard_Activity_First_Run", false);
        }
    }

    public static class LimitSetter {
        private PublishSubject<LimitSetter> changeItemsPerPagePublishSubject = PublishSubject.create();
        private String itemsPerPage;

        String getItemsPerPage() {
            return itemsPerPage;
        }

        public void setItemsPerPage(String itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
            changeItemsPerPagePublishSubject.onNext(this);
        }

        PublishSubject<LimitSetter> getModelChanges() {
            return changeItemsPerPagePublishSubject;
        }

        @NonNull
        @Override
        public String toString() {
            return getItemsPerPage();
        }

    }

    public void setOffset(Cache cache, int offset) {
        cache.putInt("offset", offset);
    }

    public int getOffset(Cache cache) {
        return cache.getInt("offset", DEFAULT_OFFSET);
    }



    public static class OffsetSetter {
        private PublishSubject<OffsetSetter> changeTotalItemsPublishSubject = PublishSubject.create();
        private String offset;

        String getOffset() {
            return offset;
        }

        public void setOffset(String offset) {
            this.offset = offset;
            changeTotalItemsPublishSubject.onNext(this);
        }



        PublishSubject<OffsetSetter> getModelChanges() {
            return changeTotalItemsPublishSubject;
        }

        @NonNull
        @Override
        public String toString() {
            return getOffset();
        }
    }


    public int itemsCountStore() {
        int count;
        StoreDB storeDB = new StoreDB(this);
        storeDB.open();
        count = storeDB.getFilteredItemsCount(getMerged_queries(cache_StoreActivity));
        storeDB.close();
        Log.i(TAG, "itemsCountDownloads: count: " + count);
        return count;
    }
    public void myViewsInitiations() {

        toolbar = findViewById(R.id.my_toolbar);

        // sort and ordering
        imageView_sort_arrow = findViewById(R.id.imageView_sort);
        textView_sort = findViewById(R.id.textView_sort);
        checkBox_order = findViewById(R.id.checkBox_order);

        // database_navigation
        textView_limit = findViewById(R.id.textView_limit);
        imageView_limit = findViewById(R.id.imageView_limit);

        textView_offset = findViewById(R.id.textView_offset);
        imageView_previous = findViewById(R.id.imageView_previous_page);
        imageView_next = findViewById(R.id.imageView_next_page);

        // recyclerView
        recyclerView = findViewById(R.id.recycler_view);

    }

    private void loadDatabase() {

        setMyFilter(cache_StoreActivity);
        fillupList(cache_StoreActivity);
        displaySort(cache_StoreActivity);
        displayOrder(cache_StoreActivity);
        displayLimit(cache_StoreActivity);
        displayOffset(cache_StoreActivity);
        setAdapter();
    }

    public void displaySort(Cache cache) {
        textView_sort.setText(getString(R.string.sort_by, getSort(cache)));
    }

    public void displayOrder(Cache cache) {
        if (getOrder(cache).equals("ASC")) {
            checkBox_order.setChecked(false);
        } else if (getOrder(cache).equals("DESC")) {
            checkBox_order.setChecked(true);
        }
    }

    public void displayLimit(Cache cache) {
        limitSetter.setItemsPerPage(String.valueOf(getLimit(cache)));
    }


    public void displayOffset(Cache cache) {
        int total = getCount();

        int numberOfPages = (int) Math.floor((float) total / getLimit(cache));

        String offset = null;

        if (total == 0) {
            imageView_previous.setVisibility(View.INVISIBLE);
            imageView_next.setVisibility(View.INVISIBLE);
            offsetSetter.setOffset(offset);

        } else if (total <= getLimit(cache)) {
            imageView_previous.setVisibility(View.INVISIBLE);
            imageView_next.setVisibility(View.INVISIBLE);
            offset = 1 + space + getString(R.string.dash) + space + total + space + "/" + space + total;
            offsetSetter.setOffset(offset);

        } else if (total > getLimit(cache)) {

            if (getOffset(cache) == 0) {
                imageView_previous.setVisibility(View.INVISIBLE);
                imageView_next.setVisibility(View.VISIBLE);
                offset = (getOffset(cache) * getLimit(cache) + 1) +
                        space + getString(R.string.dash) + space +
                        (getOffset(cache) * getLimit(cache) + getLimit(cache)) + space + "/" + space + total;
                offsetSetter.setOffset(offset);

            } else if (getOffset(cache) > 0 && getOffset(cache) < numberOfPages) { // safehat vasat

                imageView_previous.setVisibility(View.VISIBLE);
                imageView_next.setVisibility(View.VISIBLE);

                offset = (getOffset(cache) * getLimit(cache) + 1) +
                        space + getString(R.string.dash) + space +
                        (getOffset(cache) * getLimit(cache) + getLimit(cache)) + space + "/" + space + total;
                offsetSetter.setOffset(offset);

            } else if (getOffset(cache) == numberOfPages) {
                imageView_previous.setVisibility(View.VISIBLE);
                imageView_next.setVisibility(View.INVISIBLE);

                offset = (getOffset(cache) * getLimit(cache) + 1) +
                        space + getString(R.string.dash) + space + total + space + "/" + space + total;
                offsetSetter.setOffset(offset);

            }

        }

        Log.i(TAG, "displayOffset: getOffset(cache)*getLimit(cache): " + getOffset(cache) * getLimit(cache));


    }


    public void setMyFilter(Cache cache) {


        if (cache.equals(cache_TextsActivity)) {
            cache.putString("myFilter",
                    getMerged_queries(cache) + space +
                            "LIMIT" + space + getLimit(cache) + space +
                            "OFFSET" + space + getOffset(cache) * getLimit(cache));

        } else {
            cache.putString("myFilter",
                    getMerged_queries(cache) + space +
                            "ORDER BY" + space + getSort(cache) + space + getOrder(cache) + space +
                            "LIMIT" + space + getLimit(cache) + space +
                            "OFFSET" + space + getOffset(cache) * getLimit(cache));
        }


        Log.i(TAG, "setMyFilter: " + cache.getString("myFilter"));

    }

    protected void getExtra() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            String back_to = (String) bundle.get("back_to");
            setBack_to(back_to);
        }
    }

    protected void myListeners() {


        // sorting listener ============================================================================
        final String[] sort_by = getResources().getStringArray(R.array.sort_store);
        imageView_sort_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int s = -1;
                if (getSort(cache_StoreActivity).equals(sort_by[0].toLowerCase())) {
                    s = 0;
                } else if (getSort(cache_StoreActivity).equals(sort_by[1].toLowerCase())) {
                    s = 1;
                } else if (getSort(cache_StoreActivity).equals(sort_by[2].toLowerCase())) {
                    s = 2;
                }

                AlertDialog.Builder builder =  new AlertDialog.Builder(storeActivity);
                builder.setTitle(getString(R.string.sort_by, ""));
                builder.setIcon(R.drawable.ic_sort);
                builder.setSingleChoiceItems(sort_by, s, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {

                        if (selectedIndex == 0) {
                            setSort(cache_StoreActivity, sort_by[0].toLowerCase());
                            dialogInterface.dismiss();

                        } else if (selectedIndex == 1) {
                            setSort(cache_StoreActivity, sort_by[1].toLowerCase());
                            dialogInterface.dismiss();

                        } else if (selectedIndex == 2) {
                            setSort(cache_StoreActivity, sort_by[2].toLowerCase());
                            dialogInterface.dismiss();

                        }

                        loadDatabase();

                    }
                });
                builder.create();
                builder.show();
            }
        });

        // order listener ============================================================================
        checkBox_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "changeOrder: " + getOrder(cache_StoreActivity));
                switch (getOrder(cache_StoreActivity)) {
                    case "ASC":
                        setOrder(cache_StoreActivity, "DESC");
                        loadDatabase();

                        break;
                    case "DESC":
                        setOrder(cache_StoreActivity, "ASC");
                        loadDatabase();

                        break;
                    default:

                        break;
                }

            }
        });

        // limit listener ============================================================================
        final String[] items_per_page = getResources().getStringArray(R.array.items_per_page);
        imageView_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int s = -1;
                if (getLimit(cache_StoreActivity) == LIMIT_LIST[0]) {
                    s = 0;
                } else if (getLimit(cache_StoreActivity) == LIMIT_LIST[1]) {
                    s = 1;
                } else if (getLimit(cache_StoreActivity) == LIMIT_LIST[2]) {
                    s = 2;
                }

                AlertDialog.Builder builder =  new AlertDialog.Builder(storeActivity);
                builder.setTitle(getString(R.string.items_per_page,"", ""));
                builder.setIcon(R.drawable.ic_download);
                builder.setSingleChoiceItems(items_per_page, s, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {

                        if (selectedIndex == 0) {
                            setLimit(cache_StoreActivity, LIMIT_LIST[0]);
                            dialogInterface.dismiss();

                        } else if (selectedIndex == 1) {
                            setLimit(cache_StoreActivity, LIMIT_LIST[1]);
                            dialogInterface.dismiss();

                        } else if (selectedIndex == 2) {
                            setLimit(cache_StoreActivity, LIMIT_LIST[2]);
                            dialogInterface.dismiss();

                        }

                        setOffset(cache_StoreActivity, 0);
                        loadDatabase();

                    }
                });
                builder.create();
                builder.show();

            }

        });

        // previous listener ============================================================================
        imageView_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOffset(cache_StoreActivity, getOffset(cache_StoreActivity) - 1);
                loadDatabase();
            }
        });

        // next listener ============================================================================
        imageView_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOffset(cache_StoreActivity, getOffset(cache_StoreActivity) + 1);
                loadDatabase();
            }
        });
    }

 /*   protected void displayToolbar() {
        toolbar.setTitle(R.string.playtext_store);
        toolbar.setSubtitle("Contain " + getCount() + " item(s)");
        setSupportActionBar(toolbar);
    }
   /* protected void displayToolbarSubtitle() {
        subTitleSetter.setSubTitle(getCount() + " founded");
    }*/


    /**
     *
     * In: list   Out: adapter
     * */
    protected void setAdapter() {
        StoreAdapter adapter = new StoreAdapter(storeActivity, list);


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(storeActivity);
        recyclerView.setLayoutManager(mLayoutManager);

//        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
    }


    /**
     *  In: myFilter   Out: list
     * */
    protected void fillupList(Cache cache) {
        list.clear();

        StoreDB storeDB = new StoreDB(storeActivity); //NON-NLS
        storeDB.open();
        list = storeDB.getFilteredItems(getMyFilter(cache));
        storeDB.close();
    }

    public String getMyFilter(Cache cache) {
        return cache.getString("myFilter");
    }


    // interface  ==============================================


    //                        String csvFilePath =  downloadsFolderPath() + File.separator  + Ctes.STORE_CSV;
    public void fillUpStoreDb() {

        String csvFilePath =  getAssets() + File.separator  + STORE_CSV;
        Log.i(TAG, "loadDatabase: csvFilePath: " + csvFilePath);

        showProgressBar(progressBar, true);








        Observable.just("")
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws IOException {

                        Log.i(TAG, "apply: csvFilePath: " + csvFilePath);


                        StoreDB storeDB = new StoreDB(storeActivity);
                        storeDB.open();
                        storeDB.delete();


                        CSVReader reader;

                        try {
                            reader = new CSVReader(new InputStreamReader(getAssets().open(STORE_CSV)));
                            String[] line;
                            int n = 0;
                            while ((line = reader.readNext()) != null) {
                                Log.i(TAG, "readCsvFile: n: " + n);
                                storeDB.createItem(line[1], line[2], line[3], line[4], Integer.parseInt(line[5]), line[6], line[7], Integer.parseInt(line[8]));
                                n++;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        storeDB.close();


                        return "";
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(String folders) {
                        Log.i(TAG, "onNext");
                    }


                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");

                        hideProgressBar(progressBar);
                        setCount(itemsCountStore());
                        loadDatabase();
                    }
                });

    }



    public void showProgressBar(ProgressBar progressBar, boolean isIndeterminate) {
        progressBar.setIndeterminate(isIndeterminate);
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideProgressBar(ProgressBar progressBar) {
        if (progressBar.isShown()) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");

        setMyFilter(cache_StoreActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}