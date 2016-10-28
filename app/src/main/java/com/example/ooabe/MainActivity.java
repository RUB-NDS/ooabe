package com.example.ooabe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ooabe.abe.AttributeUniverse;
import com.example.ooabe.abe.CipherText;
import com.example.ooabe.abe.RSABE;
import com.example.ooabe.abeTestContent.ABETestContent;

import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    public static RSABE myRSABE;
    public static int security_level = 0;
    public static AttributeUniverse U;
    public static CipherText CT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }



    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ABETestContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<ABETestContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<ABETestContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane)
                    {
                        switch( holder.getAdapterPosition())
                        {
                            case 0:
                            {
                                SetUpFragment setupFragment = new SetUpFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.item_detail_container, setupFragment)
                                        .commit();
                                        break;
                            }

                            case 1:
                            {
                                EncryptionTestFragment encFragment = new EncryptionTestFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.item_detail_container, encFragment)
                                        .commit();
                                break;
                            }

                            case 2:
                            {
                                DecryptionTestFragment decFragment = new DecryptionTestFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.item_detail_container, decFragment)
                                        .commit();
                                break;

                            }

                            default:
                            {
                                Bundle arguments = new Bundle();
                                arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                                ItemDetailFragment fragment = new ItemDetailFragment();
                                fragment.setArguments(arguments);
//                                the default behavior is to show the introduction text
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.item_detail_container, fragment)
                                        .commit();
                                    break;
                            }
                        }
                    }
                    else
                    {
                        Context context = v.getContext();
                        Intent intent;
                        switch( holder.getAdapterPosition())
                        {
                            case 0:
                                intent = new Intent(context, SetUpActivity.class);
                                break;
                            case 1:
                                intent = new Intent(context, EncryptionTestActivity.class);
                                break;
                            case 2:
                                intent = new Intent(context, DecryptionTestActivity.class);
                                break;
                            default:
                            {
//                                the default behavior is to show the introduction text
                                intent = new Intent(context, ItemDetailActivity.class);
                                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                                break;
                            }
                        }

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
//modify this class to change behavior
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public ABETestContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
