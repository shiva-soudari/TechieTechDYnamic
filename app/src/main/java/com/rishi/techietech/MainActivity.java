package com.rishi.techietech;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rishi.techietech.models.ItemInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int NUMBER_OF_PAGES = 2;

    MyAdapter mAdapter;
    VerticalViewPager mPager;

    List<ItemInfo> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide(); //<< this

        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        newsList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("news")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            newsList = task.getResult().toObjects(ItemInfo.class);
                            System.out.println("=== Size" + newsList.size());
                            loadData();
                        } else {
                            Log.w("Failed", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    public void loadData() {
        if (newsList.size() > 0) {
            mAdapter = new MyAdapter(getSupportFragmentManager());
            mPager = findViewById(R.id.viewpager);
            mPager.setAdapter(mAdapter);
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return newsList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentOne.newInstance(position, newsList.get(position));
        }
    }

    public static class FragmentOne extends Fragment {

        private static final String MY_NUM_KEY = "num";
        private static final String NEWS_KEY = "news";

        private int mNum;
        private ItemInfo newsInfo;

        // You can modify the parameters to pass in whatever you want
        static FragmentOne newInstance(int num, ItemInfo newsInfo) {
            FragmentOne f = new FragmentOne();
            Bundle args = new Bundle();
            args.putInt(MY_NUM_KEY, num);
            args.putParcelable(NEWS_KEY, newsInfo);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt(MY_NUM_KEY) : 0;
            newsInfo = getArguments().getParcelable(NEWS_KEY);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_item_view, container, false);
           // v.setBackgroundColor(mColor);
            TextView txtTitle = v.findViewById(R.id.txtTitle);
            TextView txtDesc = v.findViewById(R.id.txtDesc);

            ImageView image = (ImageView) v.findViewById(R.id.img);
          /*  Bitmap bitImg = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ipods);
            image.setImageBitmap(getRoundedCornerImage(bitImg));
*/

            txtTitle.setText(newsInfo.getTitle());
            txtDesc.setText(newsInfo.getDescription());

            System.out.println("=====Link "+newsInfo.getImage());
            Glide.with(getActivity()).load(newsInfo.getImage()).into(image);

            // textView.setText("Page " + mNum);
            return v;
        }
    }

    public static Bitmap getRoundedCornerImage(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 50;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }
}