/*
 * Copyright 23rd, 2019; Copyright Optogram 2024
 */

package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class OptoSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;

    private int rowCount;
    private int sectionRow1;
    private int inappCameraRow;
    private int emptyRow;
    private int disableGlobalSearch;
    private int disableParametersFromBotLinks;
    private int lockPremium;
    private int disableInstantCamera;


    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        rowCount = 0;

        sectionRow1 = rowCount++;
        inappCameraRow = rowCount++;
        disableGlobalSearch = rowCount++;
        disableParametersFromBotLinks = rowCount++;
        lockPremium = rowCount++;
        disableInstantCamera = rowCount++;

        return true;
    }

    public boolean toggleGlobalMainSetting(String option, View view, boolean byDefault) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        boolean optionBool = preferences.getBoolean(option, byDefault);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(option, !optionBool);
        editor.commit();
        if (view instanceof TextCheckCell) {
            ((TextCheckCell) view).setChecked(!optionBool);
        }
        return !optionBool;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("OptoSettingsTitle", R.string.OptoSettingsTitle));

        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        listView.setAdapter(listAdapter);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (position == inappCameraRow) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                SharedConfig.toggleInappCamera();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.inappCamera);
                }
            } else if (position == disableGlobalSearch) {
                toggleGlobalMainSetting("disableGlobalSearch", view, false);
            } else if (position == lockPremium) {
                toggleGlobalMainSetting("lockPremium", view, false);
            } else if (position == disableParametersFromBotLinks) {
                toggleGlobalMainSetting("disableParametersFromBotLinks", view, false);
            } else if (position == disableInstantCamera) {
                toggleGlobalMainSetting("disableInstantCamera", view, false);
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 2: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    break;
                }
                case 3: {
                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    if (position == inappCameraRow) {
                        String t = LocaleController.getString("InAppCamera", R.string.InAppCamera);
                        String info = LocaleController.getString("InAppCameraInfo", R.string.InAppCameraInfo);
                        textCell.setTextAndValueAndCheck(t, info, preferences.getBoolean("inappCamera", true), false, false);
                    } else if (position == disableGlobalSearch) {
                        String t = LocaleController.getString("DisableGlobalSearch", R.string.DisableGlobalSearch);
                        textCell.setTextAndCheck(t, preferences.getBoolean("disableGlobalSearch", false), false);
                    } else if (position == lockPremium) {
                        String t = LocaleController.getString("LockPremium", R.string.LockPremium);
                        String info = LocaleController.getString("SquareAvatarsInfo", R.string.SquareAvatarsInfo);
                        textCell.setTextAndValueAndCheck(t, info, preferences.getBoolean("lockPremium", false), true, false);
                    } else if (position == disableParametersFromBotLinks) {
                        String t = LocaleController.getString("DisableParametersFromBotLinks", R.string.DisableParametersFromBotLinks);
                        textCell.setTextAndCheck(t, preferences.getBoolean("disableParametersFromBotLinks", false), false);
                    } else if (position == disableInstantCamera) {
                        String t = LocaleController.getString("DisableInstantCamera", R.string.DisableInstantCamera);
                        textCell.setTextAndCheck(t, preferences.getBoolean("disableInstantCamera", false), false);
                    }
                    break;
                }
                case 4: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == sectionRow1) {
                        headerCell.setText(LocaleController.getString("General", R.string.General));
                    }
                    break;
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            boolean fork = false
                        || position == inappCameraRow
                        || position == lockPremium
                        || position == disableParametersFromBotLinks
                        || position == disableInstantCamera
                        || position == disableGlobalSearch;
            return fork;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 1:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 2:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new NotificationsCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == emptyRow) {
                return 1;
            } else if (0 == 1) {
                return 2;
            } else if (false
                || position == inappCameraRow
                || position == lockPremium
                || position == disableParametersFromBotLinks
                || position == disableInstantCamera
                || position == disableGlobalSearch) {
                return 3;
            } else if (position == sectionRow1) {
                return 4;
            }
            return 6;
        }
    }
}
