package com.jio.homelauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder> {
    public List<AppInfo> appsList;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item_app_label, item_app_name, item_class_name, item_version_code, item_version_name;
        public ImageView img;

        //This is the subclass ViewHolder which simply
        //'holds the views' for us to show on each row
        public ViewHolder(View itemView) {
            super(itemView);

            //Finds the views from our row.xml
            item_app_label = (TextView) itemView.findViewById(R.id.item_app_label);
            img = (ImageView) itemView.findViewById(R.id.img);
            item_app_name = (TextView) itemView.findViewById(R.id.item_app_name);
            item_class_name = (TextView) itemView.findViewById(R.id.item_class_name);
            item_version_code = (TextView) itemView.findViewById(R.id.item_version_code);
            item_version_name = (TextView) itemView.findViewById(R.id.item_version_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Context context = v.getContext();

            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appsList.get(pos).packageName.toString());
            context.startActivity(launchIntent);
            Toast.makeText(v.getContext(), appsList.get(pos).label.toString(), Toast.LENGTH_LONG).show();

        }
    }

    public RAdapter(Context c) {

        //This is where we build our list of app details, using the app
        //object we created to store the label, package name and icon

        PackageManager pm = c.getPackageManager();
        appsList = new ArrayList<AppInfo>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> allApps = pm.queryIntentActivities(i, 0);

        for (ResolveInfo ri : allApps) {
            AppInfo app = new AppInfo();
            app.label = ri.loadLabel(pm);
            app.packageName = ri.activityInfo.packageName;
            try {
                PackageInfo pInfo = c.getPackageManager().getPackageInfo(ri.activityInfo.packageName, 0);
                app.verCode = pInfo.versionCode;
                app.versionName = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            app.icon = ri.activityInfo.loadIcon(pm);
            String mainClassName = ri.activityInfo.name;
            int pos = mainClassName.lastIndexOf('.') + 1;
            String onlyClass = mainClassName.substring(pos);
            app.className = onlyClass;

            appsList.add(app);
            //ArrayList<AppInfo> itemList = new ArrayList<>();

            Collections.sort(appsList, new Comparator<AppInfo>() {
                @Override
                public int compare(AppInfo item, AppInfo t1) {
                    CharSequence s1 = item.label;
                    CharSequence s2 = t1.label;
                    return s1.toString().compareToIgnoreCase(s2.toString());
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(RAdapter.ViewHolder viewHolder, int i) {
        //Here we use the information in the list we created to define the views
        String appLabel = appsList.get(i).label.toString();
        String appPackage = appsList.get(i).packageName.toString();
        String classNane = appsList.get(i).className.toString();
        String versionName = appsList.get(i).versionName.toString();
        int versionCode = appsList.get(i).verCode;
        Drawable appIcon = appsList.get(i).icon;

        TextView item_app_label = viewHolder.item_app_label;
        TextView item_app_name = viewHolder.item_app_name;
        TextView item_class_name = viewHolder.item_class_name;
        TextView item_version_code = viewHolder.item_version_code;
        TextView item_version_name = viewHolder.item_version_name;

        item_app_name.setText(appPackage);
        item_app_label.setText(appLabel);
        item_class_name.setText(classNane);
        item_version_code.setText(String.valueOf(versionCode));
        item_version_name.setText(versionName);
        ImageView imageView = viewHolder.img;
        imageView.setImageDrawable(appIcon);

    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    @Override
    public RAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //This is what adds the code we've written in here to our target view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


}