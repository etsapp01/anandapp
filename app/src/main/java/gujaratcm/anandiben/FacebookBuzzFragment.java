package gujaratcm.anandiben;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.BlogInfo;
import gujaratcm.anandiben.model.FacebookPostInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.modellist.BlogList;
import gujaratcm.anandiben.modellist.NewsList;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayout;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayoutDirection;

/**
 * Created by ACER on 23-07-2016.
 */
public class FacebookBuzzFragment extends BaseFragment implements SwipyRefreshLayout.OnRefreshListener {
    private RecyclerView gvdata;
    LinearLayoutManager mLayoutManager;
    LatestlistAdapter adapter;
    SwipyRefreshLayout swipeContainer;

    private FloatingActionMenu MainShare;

    FloatingActionButton facebook_share, google_share, tweet;

    public static FacebookBuzzFragment getInstance() {
        FacebookBuzzFragment fragment = new FacebookBuzzFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.facebook_post, container, false);
        MainShare = (FloatingActionMenu) v.findViewById(R.id.menu_fab);
        swipeContainer = (SwipyRefreshLayout) v
                .findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
        MainShare.setIconAnimated(false);
        facebook_share = (FloatingActionButton) v.findViewById(R.id.accion_delete1);
        google_share = (FloatingActionButton) v.findViewById(R.id.accion_delete);
        tweet = (FloatingActionButton) v.findViewById(R.id.accion_edit);
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare("http://anandibenpatel.com/en/category/blog/");
            }
        });
        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSharing(getActivity(), "http://anandibenpatel.com/en/category/blog/");
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gvdata = (RecyclerView) view.findViewById(R.id.gvdata);
        swipeContainer = (SwipyRefreshLayout) view
                .findViewById(R.id.swipeContainer);
//        final GridLayoutManager manager = new GridLayoutManager(getActivity(), nocols);
        mLayoutManager = new LinearLayoutManager(getActivity());
        gvdata.setLayoutManager(mLayoutManager);
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(this);
        showProgress();
        GetDataFromServer();
    }

    public void BindData(ArrayList<FacebookPostInfo> m_list) {
        if (m_list != null && m_list.size() > 0 && gvdata != null) {
            adapter = new LatestlistAdapter(getActivity(), m_list);
            gvdata.setAdapter(adapter);
        } else {
            gvdata.setAdapter(null);
            Utils.Instance().ShowSnack(gvdata, "No data found");
        }
        if (swipeContainer != null) {
            swipeContainer.setRefreshing(false);
        }
    }


    public class LatestlistAdapter extends RecyclerView.Adapter<LatestlistAdapter.ViewHolder> {
        //    private List<BooksObject> objects = new ArrayList<BooksObject>();
        ArrayList<FacebookPostInfo> objects = new ArrayList<FacebookPostInfo>();
        private Context context;
        private int lastPosition = -1;

        public LatestlistAdapter(Context context, ArrayList<FacebookPostInfo> lists) {
            this.context = context;
            this.objects = lists;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public LatestlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.facebook_post_item, null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData

            final FacebookPostInfo youtubeInfo = objects.get(position);


            Picasso.with(getActivity()).load(R.drawable.default_image).into(viewHolder.imgProfile);
            Picasso.with(getActivity())
                    .load("https://fbcdn-sphotos-e-a.akamaihd.net/hphotos-ak-xtl1/v/t1.0-9/13533041_1225662550780088_5871760602148577415_n.png?oh=370396ec72890c9fa3863784f15f3b3b&oe=5825CFA1&__gda__=1479806866_64ac591d0d772ee49bfd0bb5a498f7b3")
                    .into(viewHolder.imgProfile);

            viewHolder.txtTitle.setText(youtubeInfo.message);
            viewHolder.txtlikescount.setText(youtubeInfo.likecount + "");
            viewHolder.txtCommentscount.setText(youtubeInfo.commentcount + "");
            String currentdat = youtubeInfo.created_time;
            if (currentdat != null && currentdat.length() > 0) {
                String formated = Utils.Instance().ChangeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy", currentdat);
                viewHolder.txtDate.setText(formated);
            }

            viewHolder.llmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (youtubeInfo != null) {
//                        Intent start = new Intent(getActivity(), BlogDetailActivity.class);
//                        start.putExtra("id", youtubeInfo.blogid);
//                        startActivity(start);
//                    }
                }
            });
            if (youtubeInfo.full_picture != null && youtubeInfo.full_picture.length() > 0) {
                Picasso.with(getActivity())
                        .load(youtubeInfo.full_picture)
                        .into(viewHolder.imgCoverPage);
            } else {
                Picasso.with(getActivity()).load(R.drawable.default_image).into(viewHolder.imgCoverPage);
            }
            setAnimation(viewHolder.itemView, position);
        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

//        public SimpleDraweeView ivbook;

            ImageView imgCoverPage, imgProfile;
            TextView txtTitle, txtDate, txtlikescount, txtCommentscount;
            LinearLayout llmain;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                //ivbook = (SimpleDraweeView) itemLayoutView.findViewById(R.id.ivbook);
                imgCoverPage = (ImageView) itemLayoutView.findViewById(R.id.feedImage1);
                imgProfile = (ImageView) itemLayoutView.findViewById(R.id.profilePic);
                txtTitle = (TextView) itemLayoutView.findViewById(R.id.txtStatusMsg);
                txtDate = (TextView) itemLayoutView.findViewById(R.id.timestamp);
                txtlikescount = (TextView) itemLayoutView.findViewById(R.id.txtlikescount);
                txtCommentscount = (TextView) itemLayoutView.findViewById(R.id.txtCommentscount);
                llmain = (LinearLayout) itemLayoutView.findViewById(R.id.layout);

//                int hei = (int) (imagewidth * 1.3);
//                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imagewidth, hei);
//                imgCoverPage.setLayoutParams(layoutParams);
            }
        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.hint_slide_up);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return objects.size();
        }
    }

    public void GetDataFromServer() {
        getFacebookPosts(new ModelDelegates.FacebookPostDelegate() {
            @Override
            public void OnGetPost(ArrayList<FacebookPostInfo> arrayList) {
                hideProgress();
                BindData(arrayList);
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void OnFail(String fail) {
                hideProgress();
                swipeContainer.setRefreshing(false);
                Utils.Instance().ShowSnack(gvdata, fail);
            }
        });
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        swipeContainer.setRefreshing(true);
        GetDataFromServer();

    }
}
