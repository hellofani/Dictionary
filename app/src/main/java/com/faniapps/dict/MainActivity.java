package com.faniapps.dict;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import database.DataHelper;
import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;


public class MainActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private DataAdapter mAdapter;
    DataHelper db_helper;
    SQLiteDatabase db;
    ArrayList<Dictionary> list_words;
    FloatingActionsMenu floatingActionsMenu;
    FloatingActionButton eng_search,tel_search;
    SearchView searchView;
    MenuItem search;
    LinkedHashMap<String, Integer> mapIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSoftKeyboard();
        mapIndex = new LinkedHashMap<>();
        db_helper = new DataHelper(MainActivity.this);
        getAllLanguageWords();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        loadWords();
        getIndexList(list_words);
        sortListIndex();
        displayIndex();
    }



    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();

        String locale = ims.getLocale();
        Log.e("locale",locale);

        /*if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }*/
    }

    private void sortListIndex() {
        Map<String,Integer> map = new TreeMap<String,Integer>(mapIndex);
        Set set2 = map.entrySet();
        mapIndex = new LinkedHashMap<String, Integer>();
        Iterator iterator2 = set2.iterator();
       // int i
        while(iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry)iterator2.next();
        //    mapIndex
            System.out.print(me2.getKey() + ": ");
            System.out.println(me2.getValue());
            mapIndex.put((String) me2.getKey(),(Integer) me2.getValue());
        }
    }

    private void getIndexList(ArrayList<Dictionary> list_words) {

        for (int i = 0; i < list_words.size(); i++) {
            String fruit = list_words.get(i).getTelugu_word().trim();
            Log.e("fruit",""+fruit + " "+i);
            if(fruit.length()>0) {
                String index = fruit.substring(0, 1).toUpperCase();

                if (mapIndex.get(index) == null)
                    mapIndex.put(index, i);

            }
        }

    }


    private void displayIndex() {
        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);

        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView selectedIndex = (TextView) v;
                    mRecyclerView.getLayoutManager().scrollToPosition(mapIndex.get(selectedIndex.getText()));
                }
            });
            indexLayout.addView(textView);
        }

    }
    private void loadWords() {
        mAdapter = new DataAdapter(list_words,MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initViews(){
       floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions_down);
        eng_search = (FloatingActionButton) findViewById(R.id.search_english);
        tel_search = (FloatingActionButton) findViewById(R.id.search_telugu);
        mRecyclerView = (RecyclerView)findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        floatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                floatingActionsMenu.setIcon(getResources().getDrawable(R.drawable.ic_search));
                search.expandActionView();
            }

            @Override
            public void onMenuCollapsed() {
                floatingActionsMenu.setIcon(getResources().getDrawable(R.drawable.ic_search));
                search.expandActionView();
            }
        });



        eng_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              search.expandActionView();
                floatingActionsMenu.collapse();
            }
        });

        tel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.expandActionView();
                floatingActionsMenu.collapse();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        search = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setIconified(false);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter != null) mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    public void getAllLanguageWords() {
        list_words =  new ArrayList<>();
        // dbHelper = new DataHelper(getActivity());
        try {
            db_helper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        db = db_helper.openDataBaseWithReadOnly();
        Cursor cursor = db.rawQuery("SELECT * FROM DICT", null);
        Log.e("cursor",""+cursor.getCount());

        while (cursor.moveToNext()) {
            Dictionary data = createDataObject(cursor);
            list_words.add(data);
        }
        cursor.close();
    }

    private Dictionary createDataObject(Cursor c) {
        Dictionary dict = new Dictionary();
       // if(c.getString(c.getColumnIndex("English")).length()>0) {
            dict.setEnglish_word(c.getString(c.getColumnIndex("English")));
            dict.setTelugu_word(c.getString(c.getColumnIndex("Telugu")));
        //}
        return dict;
    }
}
