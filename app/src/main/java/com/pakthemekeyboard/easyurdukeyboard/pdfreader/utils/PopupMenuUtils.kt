package com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.adapters.PopupMenuAdapter
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PopupMenuModel
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.MenuBaseAdapter

object PopupMenuUtils {

    lateinit var customPowerMenu: CustomPowerMenu<PopupMenuModel, MenuBaseAdapter<PopupMenuModel>>
    fun initPopupMenu(context: Context): CustomPowerMenu<PopupMenuModel, MenuBaseAdapter<PopupMenuModel>> {
        customPowerMenu =
            CustomPowerMenu.Builder<PopupMenuModel, MenuBaseAdapter<PopupMenuModel>>(
                context,
                PopupMenuAdapter()
            ).setBackgroundAlpha(0.4f).build()
                    as CustomPowerMenu<PopupMenuModel, MenuBaseAdapter<PopupMenuModel>>

        customPowerMenu.apply {
            addItem(PopupMenuModel(ContextCompat.getDrawable(context, R.drawable.ic_rename)!!, "Rename"))
            addItem(PopupMenuModel(ContextCompat.getDrawable(context, R.drawable.ic_delete)!!, "Delete"))
            addItem(PopupMenuModel(ContextCompat.getDrawable(context, R.drawable.ic_share)!!, "Share"))
            addItem(PopupMenuModel(ContextCompat.getDrawable(context, R.drawable.ic_details)!!, "Details"))

            //setOnMenuItemClickListener(onIconMenuItemClickListener)
            setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
            setMenuRadius(24f)
            setMenuShadow(10f)
        }

        return customPowerMenu
    }
}