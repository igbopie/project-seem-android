package com.seem.android.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.adapters.SeemAdapter;
import com.seem.android.model.Seem;
import com.seem.android.service.Api;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by igbopie on 03/04/14.
 */
public class SeemListFragment extends ListFragment {


    public  enum QueryType{EXPIRE,EXPIRED}

    public static SeemListFragment newInstance(QueryType type) {
        SeemListFragment f = new SeemListFragment();
        Bundle args = new Bundle();
        args.putString(GlobalVars.EXTRA_QUERY_TYPE, type.toString());
        f.setArguments(args);
        return f;
    }


    public QueryType getQueryType(){
        if(getArguments() != null) {
            String qTypeString = getArguments().getString(GlobalVars.EXTRA_QUERY_TYPE);
            return QueryType.valueOf(qTypeString);
        }else {
            return null;
        }
    }

    private SeemAdapter adapter;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.debug(this.getClass(), "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_seem_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        new GetSeemsTask(false).execute();
        adapter = new SeemAdapter(new ArrayList<Seem>(),this.getActivity());
        setListAdapter(adapter);


        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        Seem seem = adapter.getItem(position);
        Utils.debug(this.getClass(),"Item Clicked! seem "+seem);
        if(seem != null) {
            onItemClickListener.onClick(seem.getId());
        }else{
            Utils.debug(getClass(),"Whot! seem is null");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.seem_view, menu);
        MenuItem menuItem = menu.findItem(R.id.action_add);
        if(!MyApplication.isLoggedIn()){
            menuItem.setVisible(false);
        }

        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                Utils.debug(this.getClass(), "NEW SEEM!");
                ActivityFactory.startCreateSeemActivity(SeemListFragment.this.getActivity());
                return true;

            case R.id.action_refresh:
                //newGame();
                Utils.debug(this.getClass(),"Refresh Seems!");
                new GetSeemsTask(true).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GlobalVars.RETURN_CODE_CREATE_SEEM && resultCode == Activity.RESULT_OK) {
            Utils.debug(this.getClass(),"Seem created!");
            new GetSeemsTask(false).execute();
        }
    }



    private class GetSeemsTask extends AsyncTask<Void,Void,List<Seem>> {
        private boolean refresh = true;
        //private final ProgressDialog dialog = new ProgressDialog(SeemListFragment.this.getActivity());

        private GetSeemsTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            //dialog.setMessage("Downloading seems...");
            //dialog.show();
        }

        @Override
        protected List<Seem> doInBackground(Void... voids) {
            List<Seem> seems = null;
            if(getQueryType() != null){
                switch (getQueryType()){
                    case EXPIRE:
                        seems = Api.getSeemsByExpire();
                        break;
                    case EXPIRED:
                    default:
                        seems = Api.getSeemsByExpired();
                        break;
                }
            }
            Utils.debug(this.getClass(), "This is the seems:" + seems);
            return seems;
        }

        @Override
        protected void onPostExecute(List<Seem> result) {
            super.onPostExecute(result);
            adapter.setItemList(result);
            adapter.notifyDataSetChanged();

            progressBar.setVisibility(View.INVISIBLE);
            //dialog.dismiss();
        }
    }


    private OnSeemClickListener onItemClickListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onItemClickListener = (OnSeemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UserProfileInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Utils.debug(getClass(), "OnDetach");
        onItemClickListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.debug(getClass(),"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.debug(getClass(),"onStop");
    }

    public interface OnSeemClickListener {
        public void onClick(String seemId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.debug(getClass(),"onDestroyView");
        cleanMe();
    }

    private void cleanMe(){
        for(Field field:this.getClass().getFields()){
            try {
                if(!field.getType().isPrimitive()){
                    field.set(this,null);
                }
            } catch (IllegalAccessException e) {
                Utils.debug(getClass(),e.getMessage());
            }
        }
    }
}
