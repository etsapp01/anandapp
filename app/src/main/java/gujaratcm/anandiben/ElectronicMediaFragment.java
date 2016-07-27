package gujaratcm.anandiben;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.YoutubeInfo;
import gujaratcm.anandiben.modellist.YoutubeList;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayout;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayoutDirection;

/**
 * Created by ACER on 02-07-2016.
 */
public class ElectronicMediaFragment extends BaseFragment implements SwipyRefreshLayout.OnRefreshListener {
    int nocols = 2;
    private int columnWidth;
    private RecyclerView gvdata;
    GridLayoutManager mLayoutManager;
    LatestlistAdapter adapter;
    SwipyRefreshLayout swipeContainer;

    private FloatingActionMenu MainShare;

    FloatingActionButton facebook_share, google_share, tweet;

    public static ElectronicMediaFragment getInstance() {
        ElectronicMediaFragment fragment = new ElectronicMediaFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.electronic_media, container, false);


        MainShare = (FloatingActionMenu) v.findViewById(R.id.menu_fab);
        MainShare.setIconAnimated(false);

        facebook_share = (FloatingActionButton) v.findViewById(R.id.accion_delete1);
        google_share = (FloatingActionButton) v.findViewById(R.id.accion_delete);
        tweet = (FloatingActionButton) v.findViewById(R.id.accion_edit);


        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare("http://anandibenpatel.com/en/electronic-media/");
            }
        });


        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSharing(getActivity(), "http://anandibenpatel.com/en/electronic-media/");
            }
        });

        google_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSharing(getActivity());
            }
        });

        showProgress();
        YoutubeList.Instance().LoadData(0, new ModelDelegates.ModelDelegate<YoutubeInfo>() {
            @Override
            public void ModelLoaded(ArrayList<YoutubeInfo> list) {
                hideProgress();
                if (list != null && list.size() > 0) {
                    BindData(0);
                }

            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                hideProgress();

            }
        });


        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nocols = 2;
        gvdata = (RecyclerView) view.findViewById(R.id.gvdata);
        swipeContainer = (SwipyRefreshLayout) view
                .findViewById(R.id.swipeContainer);
//        final GridLayoutManager manager = new GridLayoutManager(getActivity(), nocols);
        mLayoutManager = new GridLayoutManager(getActivity(), nocols);
        gvdata.setLayoutManager(mLayoutManager);
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(this);

        initilizeGridLayout();
    }

    private void initilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                0, r.getDisplayMetrics());

        columnWidth = (int) ((getScreenWidth() - ((nocols + 1) * padding)) / nocols);
        BindData(0);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    public void BindData(int scrollto) {

        ArrayList<YoutubeInfo> newsInfos = YoutubeInfo.getAll();
        LatestlistAdapter adapter = new LatestlistAdapter(getActivity(), newsInfos, columnWidth);
        if (gvdata != null) {
            gvdata.setAdapter(adapter);
            gvdata.scrollToPosition(scrollto);
        }
//        adapter.notifyDataSetChanged();
        if (swipeContainer != null) {
            swipeContainer.setRefreshing(false);
        }

        //gvdata.setItemAnimator(new DefaultItemAnimator());
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    public class LatestlistAdapter extends RecyclerView.Adapter<LatestlistAdapter.ViewHolder> {
        //    private List<BooksObject> objects = new ArrayList<BooksObject>();
        ArrayList<YoutubeInfo> objects = new ArrayList<YoutubeInfo>();
        public int imagewidth;
        private Context context;
        private int lastPosition = -1;

        public LatestlistAdapter(Context context, ArrayList<YoutubeInfo> lists, int imagewidth) {
            this.context = context;
            this.objects = lists;
            this.imagewidth = imagewidth;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public LatestlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.interview_custom_item, null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData

            YoutubeInfo youtubeInfo = objects.get(position);
//            viewHolder.imgCoverPage.setImageResource(obj);
            viewHolder.txtTitle.setText(youtubeInfo.title);

            String currentdat = youtubeInfo.createdDate;
            if (currentdat != null && currentdat.length() > 0) {
                String formated = Utils.Instance().ChangeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy", currentdat);
                viewHolder.txtDate.setText(formated);
            }

            viewHolder.llmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                int position = (int) v.getTag();
                    YoutubeInfo PrintMediaInfo = objects.get(position);
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + PrintMediaInfo.videoId));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + PrintMediaInfo.videoId));
                        startActivity(intent);
                    }
                }


            });

            Picasso.with(getActivity())
                    .load(youtubeInfo.url_thumbnail).into(viewHolder.imgCoverPage);
            setAnimation(viewHolder.itemView, position);
        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

//        public SimpleDraweeView ivbook;

            ImageView imgCoverPage;
            TextView txtTitle, txtDate, txtViwer;
            RelativeLayout llmain;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                //ivbook = (SimpleDraweeView) itemLayoutView.findViewById(R.id.ivbook);
                imgCoverPage = (ImageView) itemLayoutView.findViewById(R.id.imgCoverPage);
                txtTitle = (TextView) itemLayoutView.findViewById(R.id.txtTitle);
                txtDate = (TextView) itemLayoutView.findViewById(R.id.txtDate);
                txtViwer = (TextView) itemLayoutView.findViewById(R.id.txtViwer);
                llmain = (RelativeLayout) itemLayoutView.findViewById(R.id.llmain);

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
}
