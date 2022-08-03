package com.example.netane;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomListAdapter extends ArrayAdapter<CustomListItem> {

    private int mResource;
    private List<CustomListItem> mItems;
    private LayoutInflater mInflater;

    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param resource リソースID
     * @param items リストビューの要素
     */
    public CustomListAdapter(Context context, int resource, List<CustomListItem> items) {
        super(context, resource, items);

        mResource = resource;
        mItems = items;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        }
        else {
            view = mInflater.inflate(mResource, null);
        }

        // リストビューに表示する要素を取得
        CustomListItem item = mItems.get(position);

        // 入力を設定
        TextView ListViewInputTv = (TextView)view.findViewById(R.id.ListView_inputTv);

        List<String> NumbersStringList = item.getNumbers().stream()
                .map(String::valueOf)
                .collect(Collectors.toList());


        String NumString = Arrays.toString(item.getNumbers().toArray());
        NumString = NumString.replace("[", "");
        NumString = NumString.replace("]", "");
        NumString = NumString.replace(",", "");

        ListViewInputTv.setText(NumString);

        // 結果を設定
        TextView ListViewHit = (TextView)view.findViewById(R.id.ListView_Hit);
        ListViewHit.setText(item.getHitBlow().get(0));
        TextView ListViewBlow = (TextView)view.findViewById(R.id.ListView_Blow);
        ListViewBlow.setText(item.getHitBlow().get(1));

        return view;
    }
}
