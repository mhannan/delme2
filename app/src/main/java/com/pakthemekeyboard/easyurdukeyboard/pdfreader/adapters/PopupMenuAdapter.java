package com.pakthemekeyboard.easyurdukeyboard.pdfreader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R;
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PopupMenuModel;
import com.skydoves.powermenu.MenuBaseAdapter;

public class PopupMenuAdapter extends MenuBaseAdapter<PopupMenuModel> {
    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(R.layout.layout_popup_menu, viewGroup, false);
        }

        PopupMenuModel item = (PopupMenuModel) getItem(index);
        final ImageView icon = view.findViewById(R.id.iv_popup);
        icon.setImageDrawable(item.getIcon());
        final TextView title = view.findViewById(R.id.tv_popup);
        title.setText(item.getTitle());
        return super.getView(index, view, viewGroup);
    }
}
