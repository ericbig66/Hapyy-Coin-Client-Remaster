package com.greeting.happycoin.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.greeting.happycoin.BuyDiary;
import com.greeting.happycoin.EventAttendRecord;
import com.greeting.happycoin.R;
import com.greeting.happycoin.RedEnvelopeDiary;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * 此程式碼多數為自動產生的，因此如未標示註解處請勿任意更動以免造成程式無法編譯
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    //TAB_TITLES 為頁籤標題，如需新增請在string.xml中新增相對應的字串再將其名稱加入至下方陣列中
    private static final int[] TAB_TITLES = new int[]{R.string.buy, R.string.redbag, R.string.attRecord};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        //請註解以下片段
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        // return PlaceholderFragment.newInstance(position + 1);

        //然後新增以下片段以switch判斷目前頁面所在位置以便return時轉跳的目的地(由0開始順序將等同於上方陣列順序)
        switch (position){
            case 0:
                return BuyDiary.newInstance();
            case 1:
                return RedEnvelopeDiary.newInstance();
            case 2:
                return EventAttendRecord.newInstance();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        //此處的return數量表示子頁面(頁籤)數量
        return 3;
    }
}