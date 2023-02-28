package com.wsy.navigation.ui.hirouter.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.DialogFragmentNavigator;
import androidx.navigation.fragment.FragmentNavigator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wsy.navigation.R;
import com.wsy.navigation.ui.hirouter.HiFragmentNavigator;
import com.wsy.navigation.ui.hirouter.model.BottomBar;
import com.wsy.navigation.ui.hirouter.model.Destination;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NavUtil {

    private static HashMap<String, Destination> destinations;

    public static String  parseFile(Context context,String fileName) {
        AssetManager assets = context.getAssets();
        try {
            InputStream inputStream = assets.open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new BufferedReader(new InputStreamReader(inputStream)));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            inputStream.close();
            bufferedReader.close();

//            HashMap<String, Destination> destionationHashMap = JSON.parseObject(builder.toString(), new TypeReference<HashMap<String, Destination>>() {
//            }.getType());
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void builderNavGraph(FragmentActivity activity, FragmentManager childFragmentManager, NavController controller, int containerId) {
        String content = parseFile(activity, "destination.json");
        destinations = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
        });

        Iterator<Destination> iterator = destinations.values().iterator();
        NavigatorProvider provider = controller.getNavigatorProvider();
        NavGraphNavigator graphNavigator = provider.getNavigator(NavGraphNavigator.class);
        NavGraph navGraph = new NavGraph(graphNavigator);

        HiFragmentNavigator hiFragmentNavigator = new HiFragmentNavigator(activity, childFragmentManager, containerId);
        provider.addNavigator(hiFragmentNavigator);
        while (iterator.hasNext()) {
            Destination destination = iterator.next();
            if (destination.destType.equals("activity")) {
                ActivityNavigator navigator = provider.getNavigator(ActivityNavigator.class);
                ActivityNavigator.Destination node = navigator.createDestination();
                node.setId(destination.id);
                node.setComponentName(new ComponentName(activity.getPackageName(), destination.className));
                navGraph.addDestination(node);
            } else if (destination.destType.equals("fragment")) {
//                FragmentNavigator navigator = provider.getNavigator(FragmentNavigator.class);
                HiFragmentNavigator.Destination node = hiFragmentNavigator.createDestination();
                hiFragmentNavigator.createDestination();
                node.setId(destination.id);
                node.setClassName(destination.className);
                navGraph.addDestination(node);
            } else if (destination.destType.equals("dialog")) {
                DialogFragmentNavigator navigator = provider.getNavigator(DialogFragmentNavigator.class);
                DialogFragmentNavigator.Destination node = navigator.createDestination();
                node.setId(destination.id);
                node.setClassName(destination.className);
                navGraph.addDestination(node);
            }
            if (destination.asStarter) {
                navGraph.setStartDestination(destination.id);
            }
        }
        controller.setGraph(navGraph);
    }

    public static void builderBottomBar(BottomNavigationView navView) {
        String content = parseFile(navView.getContext(), "main_tabs_config.json");
        BottomBar bottomBar = JSON.parseObject(content, BottomBar.class);
        List<BottomBar.Tab> tabs = bottomBar.tabs;
        Menu menu = navView.getMenu();
        for (BottomBar.Tab tab : tabs) {
            if (!tab.enable) continue;
            Destination destination = destinations.get(tab.pageUrl);
            if (destination != null) {
                MenuItem menuItem = menu.add(0, destination.id, tab.index, tab.title);
                menuItem.setIcon(R.drawable.ic_dashboard_black_24dp);
            }
        }
    }


}