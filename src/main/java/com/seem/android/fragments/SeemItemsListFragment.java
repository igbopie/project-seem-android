package com.seem.android.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.R;

import com.seem.android.adapters.ItemViewAdapter;
import com.seem.android.customviews.ItemView;
import com.seem.android.model.Item;
import com.seem.android.model.Seem;
import com.seem.android.service.Api;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class SeemItemsListFragment extends Fragment {

    private static final String SEEM = "seem";
    private Seem seem;


    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private SwipeRefreshLayout swipeLayout;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ItemViewAdapter mAdapter;
    private List<Item> items = new ArrayList<Item>();

    public static SeemItemsListFragment newInstance(Seem seem) {
        SeemItemsListFragment fragment = new SeemItemsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(SEEM, seem);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SeemItemsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            seem = (Seem) getArguments().getSerializable(SEEM);

        }

        // TODO: Change Adapter to display your content
        mAdapter = new ItemViewAdapter(items,getActivity(),new ItemView.OnItemClickListener() {
            @Override
            public void onClick(Item item, ItemView itemView) {

            }

            @Override
            public void onProfileClick(String username) {

            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seemitemslist, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Now find the PullToRefreshLayout to setup
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetItems().execute();
            }
        });
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        swipeLayout.setRefreshing(true);

        getActivity().setTitle(seem.getTitle());
        new GetItems().execute();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.seem_view, menu);
        MenuItem menuItem = menu.findItem(R.id.action_add);
        boolean expired =  Calendar.getInstance().getTime().after(seem.getExpire());
        Utils.debug(getClass(),"Expired:"+expired);
        if(!MyApplication.isLoggedIn() || expired){
            menuItem.setVisible(false);
        }

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                PopupMenu popup = new PopupMenu(this.getActivity(), getActivity().findViewById(R.id.action_add));
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.camera_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.actionPopupCamera:
                                Utils.debug(this.getClass(), "NEW SEEM!");
                                ActivityFactory.startSeemAddItemActivity(SeemItemsListFragment.this.getActivity(), seem.getId(), GlobalVars.PhotoSource.CAMERA);
                                return true;
                            case R.id.actionPopupGallery:
                                Utils.debug(this.getClass(), "NEW SEEM!");
                                ActivityFactory.startSeemAddItemActivity(SeemItemsListFragment.this.getActivity(), seem.getId(), GlobalVars.PhotoSource.GALLERY);
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public class GetItems extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            items.clear();
            items.addAll(Api.getSeemItems(seem.getId(), 0, MyApplication.getToken()));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
            if(items.size() == 0){
                setEmptyText("No photos yet, add the first one!");
            }
            swipeLayout.setRefreshing(false);
        }
    }

}
