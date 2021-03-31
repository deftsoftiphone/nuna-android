package com.demo.on_boarding;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import com.demo.R;
import com.demo.databinding.ItemSliderBinding;
import java.util.ArrayList;


public class SlidingAdapter extends PagerAdapter {


    private ArrayList<String> mlist;
    private LayoutInflater inflater;
    public static FragmentActivity context;


    public SlidingAdapter(FragmentActivity context, ArrayList<String> list) {
        this.context = context;
        this.mlist = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.item_slider, view, false);
        ItemSliderBinding binding = DataBindingUtil.bind(imageLayout);
        assert imageLayout != null;
        ImageView imageview = (ImageView) imageLayout.findViewById(R.id.imageview);
        TextView upr_text = (TextView) imageLayout.findViewById(R.id.upr_text);
        TextView below_text = (TextView) imageLayout.findViewById(R.id.below_text);
        if (mlist.get(position).equalsIgnoreCase("one")) {
            binding.imvView.setImageResource(R.drawable.illustration);
            // below_text.setText(context.getResources().getString(R.string.below_one));
            upr_text.setText(context.getResources().getString(R.string.upr_one));
        } else if (mlist.get(position).equalsIgnoreCase("two")) {
//            binding.imvView.setImageResource(R.drawable.tutorial_two);
            // below_text.setText(context.getResources().getString(R.string.below_two));
            upr_text.setText(context.getResources().getString(R.string.upr_two));
        } else if (mlist.get(position).equalsIgnoreCase("three")) {
//            binding.imvView.setImageResource(R.drawable.tutorial_three);
            below_text.setText(context.getResources().getString(R.string.below_three));
            upr_text.setText(context.getResources().getString(R.string.upr_three));
        } else if (mlist.get(position).equalsIgnoreCase("four")) {
//            binding.imvView.setImageResource(R.drawable.tutorial_four);
            below_text.setText(context.getResources().getString(R.string.below_four));
            upr_text.setText(context.getResources().getString(R.string.upr_four));
        } else if (mlist.get(position).equalsIgnoreCase("five")) {
//            binding.imvView.setImageResource(R.drawable.tutorial_five);
            below_text.setText(context.getResources().getString(R.string.below_five));
            upr_text.setText(context.getResources().getString(R.string.upr_five));
        }
        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public String changedHeaderHtml(String htmlText) {
        String head = "<html><head><style type=\"text/css\">p{width:100%; max-width:100%; font-size:18px;  text-align:justify;}iframe{ max-width: 100%; max-height: 100%; width: 100%;}img{ max-width: 100%; max-height: 100%; width: 100%;}html{margin:0;padding:0;} body{margin:0;padding:0; width:100%; max-width:100%;}</style><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\" /></head><body>";

        String closedTag = "</body></html>";
        String changeFontHtml = head + htmlText + closedTag;

        return changeFontHtml;
    }
}