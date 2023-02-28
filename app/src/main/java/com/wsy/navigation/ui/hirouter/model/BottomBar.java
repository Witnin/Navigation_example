package com.wsy.navigation.ui.hirouter.model;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class BottomBar {

    public int selectTab;

    public List<Tab> tabs;


    public static class Tab{
        public int size;
        public boolean enable;
        public int index;
        public String pageUrl;
        public String title;
    }


}
