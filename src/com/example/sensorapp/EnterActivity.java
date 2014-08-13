/**
 * 
 */
package com.example.sensorapp;

import java.util.ArrayList;
import java.util.List;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.example.sensorapp.ui.CallOutPageFragment;
import com.example.sensorapp.ui.CustomViewPager;
import com.example.sensorapp.ui.LockImageView;
import com.example.sensorapp.ui.MainPageFragment;
import com.example.sensorapp.ui.MediaPageFragment;
import com.example.sensorapp.ui.MessagePageFragment;
import com.example.sensorapp.ui.SuperFragmentActivity;
import com.example.sensorapp.ui.adapter.MyViewPagerAdapter;
import com.example.sensorapp.util.PowerManagerHelper;

/**
 * @author Administrator
 * 
 */
public class EnterActivity extends SuperFragmentActivity implements
		OnPageChangeListener, View.OnClickListener
{

	// private ViewPager pager;
	private CustomViewPager pager;

	private LinearLayout currentPage;
	private ImageView currentPagerFlag;

	private View hlayoutCurPage;
	// private View frameLayoutviewpage;
	// private View fragment_container;
	// private View frameLayoutrear;
	private MainPageFragment mainPageFragment;
	private FrameLayout lockPageLayout;

	private ImageView bgImageView;
	MediaPageFragment mediaPageFragment;
	// private int[] rBacks;
	private final static int[] rBacks =
	{ R.drawable.back00, R.drawable.back01, R.drawable.back02,
			R.drawable.back03, R.drawable.back04, R.drawable.back05,
			R.drawable.back06, R.drawable.back07, R.drawable.back08,
			R.drawable.back09 };

	private final static int selector_drawable[] =
	{ R.drawable.norpage, R.drawable.selpage };

	// private ArrayList<Class<? extends Fragment>> fragments = new
	// ArrayList<Class<? extends Fragment>>();
	private List<View> fragments = new ArrayList<View>();

	private PowerManagerHelper powerManagerHelper;

	private KeyguardLock kl;
	private KeyguardManager km;

	@Override
	public void onCreate(Bundle savedInstanceStates)
	{
		super.onCreate(savedInstanceStates);

		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
		// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// 禁止屏幕锁定
		// km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		// kl = km.newKeyguardLock("mylockscreen");
		// kl.disableKeyguard();

		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);  
	     getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);  
		
		setContentView(R.layout.enter_activity);
		init();

		// ((SensorApp) getApplication()).setInstance(this);
		// Intent intent = new Intent(this, SensorService.class);
		// startService(intent);
		// TelephonyManager telM = (TelephonyManager)
		// getSystemService(Context.TELEPHONY_SERVICE);

	}

	private void checkRegistry()
	{
		if (GlobalVar.checkId)
		{
			SharedPreferences preferences = getApplicationContext()
					.getSharedPreferences("registerInfo", Context.MODE_PRIVATE);
			boolean reg = preferences.getBoolean("register", false);
			if (!reg)
			{
				Intent intent = new Intent(this, RegisterActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				startActivity(intent);
				finish();
			}
		}
	}

	private void checkStartFlag()
	{
		if (GlobalVar.checkId)
		{
			SharedPreferences preferences = getApplicationContext()
					.getSharedPreferences("registerInfo", Context.MODE_PRIVATE);
			boolean reg = preferences.getBoolean("register", false);
			if (!reg)
			{
				return;
			}
		}
		String startFlag = getIntent().getStringExtra("startFlag");
		// 如果没有设置startFlag 则跳转到设置界面去.
		if (startFlag == null)
		{
			Intent intent = new Intent(this, SettingPageActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			return;
		} else
		{

		}

	}

	private void init()
	{
		checkRegistry();
		checkStartFlag();

		// powerManagerHelper = new PowerManagerHelper(this);
		// powerManagerHelper.wakeUp();
		pager = (CustomViewPager) findViewById(R.id.ViewSet);
		currentPage = (LinearLayout) findViewById(R.id.hlayoutCurPage);
		bgImageView = (ImageView) findViewById(R.id.bg);

		initPager();
		int count = fragments.size();
		ImageView[] pagerFlagsImageViews = new ImageView[count];
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		int current = pager.getCurrentItem();
		for (int i = 0; i < count; i++)
		{
			pagerFlagsImageViews[i] = new ImageView(this);
			if (current % count == i)
			{
				pagerFlagsImageViews[i]
						.setBackgroundResource(selector_drawable[1]);
				currentPagerFlag = pagerFlagsImageViews[i];
			} else
			{
				pagerFlagsImageViews[i]
						.setBackgroundResource(selector_drawable[0]);
			}
			currentPage.addView(pagerFlagsImageViews[i], params);
		}

		hlayoutCurPage = findViewById(R.id.hlayoutCurPage);

		// rBacks = getResources().getIntArray(R.array.rBacks);
		int bgIndex = getSharedPreferences("settingInfos", Context.MODE_PRIVATE)
				.getInt("bgSelectedIndex", 0);
		bgImageView.setBackgroundResource(rBacks[bgIndex]);

		lockPageLayout = (FrameLayout) findViewById(R.id.lockPageLayout);
		mainPageFragment = new MainPageFragment(this);
		mainPageFragment
				.setOnUnlockListener(new LockImageView.OnUnLockListener()
				{

					@Override
					public void onUnlocked(View v)
					{
						// TODO Auto-generated method stub
						unlock();
					}
				});

		View mainPage = mainPageFragment.onCreateView(getLayoutInflater(),
				null, null);
		lockPageLayout.addView(mainPage, new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
	}

	public void onStart()
	{
		super.onStart();

	}

	public void onStop()
	{
		super.onStop();

	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			finish();
		}
	};

	public void onResume()
	{
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("action.hs.finish");
		registerReceiver(broadcastReceiver, intentFilter);
		
		checkStartFlag();
		lock();

	}

	public void onPause()
	{
		super.onPause();
		
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		
		mediaPageFragment.unbindService();
		try
		{
			unregisterReceiver(broadcastReceiver);
		}
		catch(Exception ex)
		{
			
		}
		// powerManagerHelper.destroy();
	}

	public void lock()
	{
		// pager.setVisibility(View.INVISIBLE);
		showPagerLayer(false);
		lockPageLayout.setVisibility(View.VISIBLE);
		mainPageFragment.lock();
		changeFragment(null);
	}

	public void unlock()
	{
		lockPageLayout.setVisibility(View.GONE);
		mainPageFragment.unlock();

		showPagerLayer(true);
	}

	public void showPagerLayer(Boolean show)
	{
		hlayoutCurPage.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		pager.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}

	private void initPager()
	{

		LayoutInflater inflater = LayoutInflater.from(this);

		// 假的fragment. 用来处理每个页面的信息。 用fragment 没办法进行无限循环滑动。
		CallOutPageFragment callOutPageFragment = new CallOutPageFragment(this);
		mediaPageFragment = new MediaPageFragment(this);
		MainPageFragment mainPageFragment = new MainPageFragment(this);

		// liuzw.
		mediaPageFragment.setOnClickListener(this);

		fragments.add(mainPageFragment.onCreateView(inflater, null, null));
		fragments.add(callOutPageFragment.onCreateView(inflater, null, null));
		fragments.add(mediaPageFragment.onCreateView(inflater, null, null));

		pager.setAdapter(new MyViewPagerAdapter(this, fragments));

		// !liuzw import. viewpager 滑动到第0 张的时候就不能再向左滑动了，
		// 此时可以将其页面偏移位置向右移动很大一个值。这样可以制造左右皆能滑动的假象。实际到第0张时仍然无法再向左滑了。
		// 所以值一定要设大一点。Integer.MAX_VALUE/2.
		pager.setCurrentItem(fragments.size() * 10000);
		// pager.setCurrentItem(0);
		// mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		pager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0)
	{
		// TODO Auto-generated method stub
		// Log.e("ViewPager.onPageSelected.", arg0 + "," + (arg0 %
		// fragments.size()));
		int current = arg0 % fragments.size();
		if (currentPagerFlag != null)
		{
			currentPagerFlag.setBackgroundResource(selector_drawable[0]);
		}
		currentPagerFlag = (ImageView) currentPage.getChildAt(current);
		currentPagerFlag.setBackgroundResource(selector_drawable[1]);

	}

	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		switch (arg0.getId())
		{
		case R.id.imageViewmessageicon:
		{
			showPagerLayer(false);
			MessagePageFragment messagePageFragment = new MessagePageFragment();
			messagePageFragment.setOnClickListener(EnterActivity.this);

			addFragment(messagePageFragment,
					MessagePageFragment.class.getSimpleName());

		}
			break;
		case R.id.closeBtn:
		{
			showPagerLayer(true);
			break;
		}
		case 0:
		{
			// 更换背景图片.
			int tag = (Integer) arg0.getTag();
			bgImageView.setBackgroundResource(rBacks[tag]);
		}
		default:
			break;
		}
	}

}
