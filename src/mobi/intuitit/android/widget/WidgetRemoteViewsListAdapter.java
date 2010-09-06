package mobi.intuitit.android.widget;

import mobi.intuitit.android.content.LauncherIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * @author Florian Sundermann
 * 
 */
public class WidgetRemoteViewsListAdapter extends BaseAdapter {

    private BoundRemoteViews mRemoteViews = null;
    private Context mContext;
    private Cursor mCursor;
    private Intent mIntent;

    ComponentName mAppWidgetProvider;
    
    /**
     * 
     * @param context
     *            remote context
     * @param c
     *            cursor for reading data
     * @param intent
     *            broadcast intent initiated the replacement, don't save it
     * @param appWidgetId
     * @param listViewId
     */
    public WidgetRemoteViewsListAdapter(Context context, Intent intent, ComponentName provider,
            int appWidgetId, int listViewId) throws IllegalArgumentException {
        super();

        mContext = context;
        mAppWidgetProvider = provider;
        mIntent = intent;

        mRemoteViews = (BoundRemoteViews)intent.getParcelableExtra(LauncherIntent.Extra.Scroll.EXTRA_ITEM_LAYOUT_REMOTEVIEWS);
    	mCursor = mContext.getContentResolver().query(Uri.parse(mIntent
                .getStringExtra(LauncherIntent.Extra.Scroll.EXTRA_DATA_URI)), mIntent
                .getStringArrayExtra(LauncherIntent.Extra.Scroll.EXTRA_PROJECTION), mIntent
                .getStringExtra(LauncherIntent.Extra.Scroll.EXTRA_SELECTION), mIntent
                .getStringArrayExtra(LauncherIntent.Extra.Scroll.EXTRA_SELECTION_ARGUMENTS),
                mIntent.getStringExtra(LauncherIntent.Extra.Scroll.EXTRA_SORT_ORDER));
    	mRemoteViews.setBindingCursor(mCursor);
        mRemoteViews.setIntentComponentName(mAppWidgetProvider);
    }

    final Handler mHandler = new Handler();
	// Create runnable for posting
	final Runnable mQueryDataRunnable = new Runnable() {
		public void run() {
	    	if (mCursor != null)
	    		mCursor.close();
	    	mCursor = mContext.getContentResolver().query(Uri.parse(mIntent
	                .getStringExtra(LauncherIntent.Extra.Scroll.EXTRA_DATA_URI)), mIntent
	                .getStringArrayExtra(LauncherIntent.Extra.Scroll.EXTRA_PROJECTION), mIntent
	                .getStringExtra(LauncherIntent.Extra.Scroll.EXTRA_SELECTION), mIntent
	                .getStringArrayExtra(LauncherIntent.Extra.Scroll.EXTRA_SELECTION_ARGUMENTS),
	                mIntent.getStringExtra(LauncherIntent.Extra.Scroll.EXTRA_SORT_ORDER));
	    	mRemoteViews.setBindingCursor(mCursor);
			System.gc();
			notifyDataSetInvalidated();
		}
	};
    
    
    public synchronized void notifyToRegenerate() {
    	mHandler.post(mQueryDataRunnable);
    }
    
    @Override
    public int getCount() {
    	return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
    	mCursor.moveToPosition(position);
    	return mCursor;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	mCursor.moveToPosition(position);
    	if (convertView == null)
    		convertView = mRemoteViews.apply(mContext, null);
    	else
    		mRemoteViews.reapply(convertView);
    	return convertView;
    }
}
