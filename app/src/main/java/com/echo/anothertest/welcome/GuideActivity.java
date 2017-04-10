package com.echo.anothertest.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.echo.anothertest.R;
import com.echo.anothertest.Main.MainActivity;
import com.echo.anothertest.utils.DecodeBitmapHelper;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity {

    private ViewPager mPager;
    private LinearLayout mDotsLayout;
    private ImageButton mBtn;

    private List<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        mPager = (ViewPager) findViewById(R.id.guide_viewpager);
        mDotsLayout = (LinearLayout) findViewById(R.id.guide_dots);
        mBtn = (ImageButton) findViewById(R.id.guide_btn);

        initPager();
        mPager.setAdapter(new ViewPagerAdapter(viewList));
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                    if (i == arg0) {
                        mDotsLayout.getChildAt(i).setSelected(true);
                    } else {
                        mDotsLayout.getChildAt(i).setSelected(false);
                    }
                }
                if (arg0 == mDotsLayout.getChildCount() - 1) {
                    mBtn.setVisibility(View.VISIBLE);
                } else {
                    mBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}

            @Override
            public void onPageScrollStateChanged(int arg0) {}
        });

        mBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openHome();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_guide, menu);
        return true;
    }

    private void initPager() {
        viewList = new ArrayList<>();
        int[] images = new int[]{R.drawable.new01, R.drawable.new02, R.drawable.new03, R.drawable.new04};
        int[] texts = new int[]{R.drawable.new_text1, R.drawable.new_text2, R.drawable.new_text3, R.drawable.new_text4};
        for (int i = 0; i < images.length; i++) {
            viewList.add(initView(images[i], texts[i]));
        }
        initDots(images.length);
    }

    private void initDots(int count) {
        for (int j = 0; j < count; j++) {
            mDotsLayout.addView(initDot());
        }
        mDotsLayout.getChildAt(0).setSelected(true);
    }

    private View initDot() {
        return LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_dot, null);
    }

    private View initView(int res, int text) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_guide, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iguide_img);
        ImageView textview = (ImageView) view.findViewById(R.id.iguide_text);
        imageView.setImageBitmap(DecodeBitmapHelper.decodeSampledBitmapFromResource(getResources(),res,400,400));
        textview.setImageResource(text);
        return view;
    }

    private void openHome() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    class ViewPagerAdapter extends PagerAdapter {

        private List<View> data;


        public ViewPagerAdapter(List<View> data) {
            super();
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(data.get(position));
            return data.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(data.get(position));
        }

    }
}
