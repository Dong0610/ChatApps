package com.g51.demo.myapp.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g51.demo.myapp.R;
import com.g51.demo.myapp.adapter.PostAdapter;
import com.g51.demo.myapp.adapter.UserAdapter;
import com.g51.demo.myapp.databinding.ActivityUserBinding;
import com.g51.demo.myapp.listener.UserListener;
import com.g51.demo.myapp.model.Post;
import com.g51.demo.myapp.model.User;
import com.g51.demo.myapp.utility.Contanst;
import com.g51.demo.myapp.utility.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends BaseActivity implements UserListener {


    private ActivityUserBinding binding;
    PreferenceManager preferenceManager;
    private UserAdapter adapter;
    private List<User> listUser;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        listUser = getUserInList();

        adapter = new UserAdapter(listUser, this);
        recyclerView = binding.userRecycleView;
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        binding.btHome.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        });
        binding.chatlist.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),MainActivity.class)));
    }

    private List<User> getUserInList() {
        List<User> list = new ArrayList<>();
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Contanst.KEY_COLLECTON_USER)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String curentUserID = preferenceManager.getString(Contanst.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (curentUserID.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.setName(queryDocumentSnapshot.getString(Contanst.KEY_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(Contanst.KEY_EMAIL));
                            user.setImage(queryDocumentSnapshot.getString(Contanst.KEY_IMAGE));
                            user.setToken(queryDocumentSnapshot.getString(Contanst.KEY_FCM_TOKEN));
                            user.setImageUri(queryDocumentSnapshot.getString(Contanst.KEY_USER_URI));
                            user.setStatus(queryDocumentSnapshot.getString(Contanst.KEY_USER_STATUS));
                            user.id = queryDocumentSnapshot.getId();
                            list.add(user);
                        }
                        if (list.size() > 0) {
                            binding.progress.setVisibility(View.INVISIBLE);
                            binding.userRecycleView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMes();
                        }
                    } else {
                        showErrorMes();
                    }
                });
        return list;
    }

    private void showErrorMes() {
        binding.texterrormes.setText(String.format("%s", "No user avaiable"));
        binding.texterrormes.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progress.setVisibility(View.VISIBLE);
        } else {
            binding.progress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Contanst.KEY_USE, user);
        startActivity(intent);
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }


}