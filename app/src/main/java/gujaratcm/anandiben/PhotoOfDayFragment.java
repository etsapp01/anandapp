package gujaratcm.anandiben;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.PhotoDayInfo;
import gujaratcm.anandiben.modellist.PhotoDayList;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayout;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayoutDirection;

/**
 * Created by ACER on 02-07-2016.
 */
public class PhotoOfDayFragment extends BaseFragment implements SwipyRefreshLayout.OnRefreshListener {
    int nocols = 2;
    private int columnWidth;
    private RecyclerView gvdata;
    GridLayoutManager mLayoutManager;
    LatestlistAdapter adapter;
    ImageView backdrop;
    TextView txtDetail;
    FloatingActionButton facebook_share, google_share, tweet;
    CardView cardView;

    public static PhotoOfDayFragment getInstance() {
        PhotoOfDayFragment fragment = new PhotoOfDayFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.photo_of_day, container, false);
        backdrop = (ImageView) v.findViewById(R.id.backdrop);
        txtDetail = (TextView) v.findViewById(R.id.txtDetail);
        facebook_share = (FloatingActionButton) v.findViewById(R.id.accion_delete1);
        google_share = (FloatingActionButton) v.findViewById(R.id.accion_delete);
        cardView = (CardView) v.findViewById(R.id.tw__card_view);

        tweet = (FloatingActionButton) v.findViewById(R.id.accion_edit);
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare("http://anandibenpatel.com/en/category/in-the-press/");
            }
        });


        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSharing(getActivity(), "http://anandibenpatel.com/en/category/in-the-press/");
            }
        });
        cardView.setVisibility(View.VISIBLE);
        loadData();

        return v;
    }


    public void loadData() {
        showProgress();
        PhotoDayList.Instance().LoadData(0, new ModelDelegates.ModelDelegate<PhotoDayInfo>() {
            @Override
            public void ModelLoaded(ArrayList<PhotoDayInfo> list) {
                hideProgress();
                ArrayList<PhotoDayInfo> arr = PhotoDayInfo.getAll();
                if (arr != null && arr.size() > 0) {
                    String url = arr.get(0).fullimage;
                    Picasso.with(getActivity())
                            .load(url)
                            .into(backdrop);
                    if (arr.get(0).content != null && arr.get(0).content.length() > 0) {
                        txtDetail.setText(arr.get(0).content);
                        cardView.setVisibility(View.VISIBLE);
                    } else {
                        cardView.setVisibility(View.GONE);
                    }
                    BindData();
                } else {
                    Picasso.with(getActivity())
                            .load(R.drawable.default_image)
                            .into(backdrop);
                }

            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                hideProgress();

            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nocols = 2;
        gvdata = (RecyclerView) view.findViewById(R.id.gvdata);
//        swipeContainer = (SwipyRefreshLayout) view
//                .findViewById(R.id.swipeContainer);
//        final GridLayoutManager manager = new GridLayoutManager(getActivity(), nocols);
        mLayoutManager = new GridLayoutManager(getActivity(), nocols);
        gvdata.setLayoutManager(mLayoutManager);
//        swipeContainer.setColorSchemeResources(
//                android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//        swipeContainer.setOnRefreshListener(this);

        initilizeGridLayout();
    }

    private void initilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                0, r.getDisplayMetrics());

        columnWidth = (int) ((getScreenWidth() - ((nocols + 1) * padding)) / nocols);

    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    public void BindData() {
        ArrayList<PhotoDayInfo> books = PhotoDayInfo.getAll();
        if (books != null) {
            LatestlistAdapter adapter = new LatestlistAdapter(getActivity(), books, columnWidth);
            if (gvdata != null) {
                gvdata.setAdapter(adapter);
//            gvdata.scrollToPosition(scrollto);
            }
        } else {
            Utils.Instance().ShowSnack(gvdata, "No Images found");
        }
//        adapter.notifyDataSetChanged();
//        if (swipeContainer != null) {
//            swipeContainer.setRefreshing(false);
//        }

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
        List<PhotoDayInfo> objects = new ArrayList<PhotoDayInfo>();
        public int imagewidth;
        private Context context;
        private int lastPosition = -1;

        public LatestlistAdapter(Context con, List<PhotoDayInfo> lists, int image) {
            context = con;
            objects = lists;
            imagewidth = image;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public LatestlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_custom_item, null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData

            PhotoDayInfo obj = objects.get(position);
//            viewHolder.imgCoverPage.setImageResource(obj);
            Picasso.with(getActivity())
                    .load(obj.thumbnail)
                    .into(viewHolder.imgCoverPage);
            viewHolder.progressss.setVisibility(View.GONE);
            viewHolder.imgCoverPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                int position = (int) v.getTag();
                }
            });
            setAnimation(viewHolder.itemView, position);
            viewHolder.imgCoverPage.setImageDrawable(getResources().getDrawable(R.drawable.tempo));
        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

//        public SimpleDraweeView ivbook;

            ImageView imgCoverPage;
            ProgressBar progressss;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                //ivbook = (SimpleDraweeView) itemLayoutView.findViewById(R.id.ivbook);
                imgCoverPage = (ImageView) itemLayoutView.findViewById(R.id.imgCoverPage);
                progressss = (ProgressBar) itemLayoutView.findViewById(R.id.progressss);
                int hei = (int) (imagewidth * 1.3);
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
