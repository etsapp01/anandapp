package gujaratcm.anandiben;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    boolean isAttri;
    MyModelDelegated.DoQuickmenu delegate;

    public NavDrawerListAdapter(Context context,
                                ArrayList<NavDrawerItem> navDrawerItems, boolean isAt, MyModelDelegated.DoQuickmenu del) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        isAttri = isAt;
        delegate = del;
    }


    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        RelativeLayout rlAtrr = (RelativeLayout) convertView.findViewById(R.id.rlAttr);
        TextView txtclickhere = (TextView) convertView
                .findViewById(R.id.clickhere);

        TextView txtScheme = (TextView) convertView
                .findViewById(R.id.txtScheme);

        TextView txtDecision = (TextView) convertView
                .findViewById(R.id.txtDecision);

        txtScheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.DoSchemeClick();
            }
        });

        txtDecision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.DoDecisionClick();
            }
        });


        imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
        txtTitle.setText(navDrawerItems.get(position).getTitle());

        // displaying count
        // check whether it set visible or not
        if (position == 8) {
            if (isAttri) {
                rlAtrr.setVisibility(View.VISIBLE);
            } else {
                rlAtrr.setVisibility(View.GONE);
            }
//			imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
//			imgIcon.setVisibility(View.VISIBLE);
//			txtclickhere.setVisibility(View.INVISIBLE);
        } else {
            rlAtrr.setVisibility(View.GONE);

            // hide the counter view
//			imgIcon.setVisibility(View.GONE);
//			txtclickhere.setVisibility(View.INVISIBLE);
        }
//		if (position == 8) {
//			txtclickhere.setVisibility(View.INVISIBLE);
//			txtTitle.setGravity(Gravity.RIGHT);
//		}

        return convertView;
    }

}
