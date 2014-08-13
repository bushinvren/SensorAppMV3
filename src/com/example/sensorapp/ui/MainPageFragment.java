/**
 * 
 */
package com.example.sensorapp.ui;

import java.util.Calendar;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog.Calls;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sensorapp.R;

/**
 * @author Administrator
 * 
 */
public class MainPageFragment extends Object
{

	private View view;
	private Activity context;

	private TextView textViewCurrentdate;
	private TextView simpledigitalclock;
	private TextView textViewnewmessagenumber;
	private TextView textViewmissedincomingnumber;

	private View framelayoutnewmessage;
	private View framelayoutmissedincoming;
	private View framelayoutcenterline;

	private LockImageView pageStart;

	static int newSmsCount = 0;
	static int newMmsCount = 0;
	static int newCallCount = 0;
	private String timeString;
	private String dateString;

	private final static String[] WEEKSTRING =
	{ "日", "一", "二", "三", "四", "五", "六" };

	private final static String[] WEEKSTRINGEN =
	{ " Sun", " Mon", " Tues", " Wed", " Thur", " Fri", " Sat" };
	final private static int UPDATE_INTERVAL = 5000;

	final private Handler handler = new Handler()
	{
		public void dispatchMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
			{

				simpledigitalclock.setText(timeString);
				textViewCurrentdate.setText(dateString);
				setPhoneInfoView();
			}
				break;

			default:
				break;
			}
		}
	};

	private LockImageView.OnUnLockListener starterClickListener;

	public MainPageFragment(Activity context)
	{
		this.context = context;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup root,
			Bundle bundle)
	{
		view = inflater.inflate(R.layout.mainframe, null);
		// context = getActivity();
		init();

		return view;
	}

	private void init()
	{
		textViewCurrentdate = (TextView) view
				.findViewById(R.id.textViewCurrentdate);
		simpledigitalclock = (TextView) view
				.findViewById(R.id.simpledigitalclock);
		textViewnewmessagenumber = (TextView) view
				.findViewById(R.id.textViewnewmessagenumber);
		textViewmissedincomingnumber = (TextView) view
				.findViewById(R.id.textViewmissedincomingnumber);

		framelayoutmissedincoming = view
				.findViewById(R.id.framelayoutmissedincoming);
		framelayoutnewmessage = view.findViewById(R.id.framelayoutnewmessage);
		framelayoutcenterline = view.findViewById(R.id.framelayoutcenterline);

		pageStart = (LockImageView) view.findViewById(R.id.pageStart);
		if (pageStart != null)
		{
			pageStart.setOnLockListener(starterClickListener);
		}
		pageStart.setVisibility(View.INVISIBLE);

		Thread thread = new Thread()
		{
			public void run()
			{
				while (true)
				{
					getSysTime();
					findNewSmsCount();
					findNewMmsCount();
					findNewCallCount();

					// Message msgMessage= handler.obtainMessage(0);
					handler.sendEmptyMessage(0);
					try
					{
						sleep(30000);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}

	public void lock()
	{
		pageStart.setVisibility(View.VISIBLE);
	}

	public void unlock()
	{
		pageStart.setVisibility(View.INVISIBLE);
	}

	public void setOnUnlockListener(LockImageView.OnUnLockListener listener)
	{
		this.starterClickListener = listener;
	}

	private void getSysTime()
	{
		Calendar mCalendar = Calendar.getInstance();
		// Date date = mCalendar.getTime();
		int month = mCalendar.get(Calendar.MONTH) + 1;
		int day = mCalendar.get(Calendar.DAY_OF_MONTH);
		int week = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;

		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = mCalendar.get(Calendar.MINUTE);

		timeString = (hour < 10 ? "0" + hour : hour) + ":"
				+ (minute < 10 ? "0" + minute : minute);

		if (context.getResources().getConfiguration().locale.getCountry()
				.equals("CN"))
		{
			dateString = (month < 10 ? "0" + month : month)
					+ context.getResources().getString(R.string.monthStr)
					+ (day < 10 ? "0" + day : day)
					+ context.getResources().getString(R.string.dayStr)
					+ context.getResources().getString(R.string.weekStr)
					+ WEEKSTRING[week];
		}
		else
		{
			dateString = (month < 10 ? "0" + month : month)
					+ context.getResources().getString(R.string.monthStr)
					+ (day < 10 ? "0" + day : day)
					+ context.getResources().getString(R.string.dayStr)
					+ context.getResources().getString(R.string.weekStr)
					+ WEEKSTRINGEN[week];
		}

	}

	private void setPhoneInfoView()
	{

		int smsNoReadCount = newSmsCount + newMmsCount;
		textViewmissedincomingnumber.setText(newCallCount + "");
		textViewnewmessagenumber.setText(smsNoReadCount + "");
		if (smsNoReadCount <= 0)
		{
			framelayoutnewmessage.setVisibility(View.INVISIBLE);
		} else
		{
			framelayoutnewmessage.setVisibility(View.VISIBLE);
		}
		if (newCallCount <= 0)
		{
			framelayoutmissedincoming.setVisibility(View.INVISIBLE);
		} else
		{
			framelayoutmissedincoming.setVisibility(View.VISIBLE);
		}
		if (smsNoReadCount > 0 && newCallCount > 0)
		{
			framelayoutcenterline.setVisibility(View.VISIBLE);
		} else
		{
			framelayoutcenterline.setVisibility(View.INVISIBLE);
		}
	}

	private void findNewSmsCount()
	{
		Cursor csr = null;
		try
		{
			csr = context.getApplicationContext().getContentResolver()
					.query(Uri.parse("content://sms"), new String[]
					{ "read" }, "type = 1 and read = 0", null, null);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			newSmsCount = csr.getCount(); // 未读短信数目
			csr.close();
		}

	}

	private void findNewMmsCount()
	{
		Cursor csr = null;
		try
		{
			csr = context.getApplicationContext().getContentResolver()
					.query(Uri.parse("content://mms/inbox"), new String[]
					{ "read" }, "read = 0", null, null);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			newMmsCount = csr.getCount();// 未读彩信数目
			csr.close();
		}

	}

	private void findNewCallCount()
	{
		Cursor csr = null;
		int missedCallCount = 0;
		try
		{
			csr = context.getContentResolver().query(Calls.CONTENT_URI,
					new String[]
					{ Calls.NUMBER, Calls.TYPE, Calls.NEW }, null, null,
					Calls.DEFAULT_SORT_ORDER);
			if (null != csr)
			{
				while (csr.moveToNext())
				{
					int type = csr.getInt(csr.getColumnIndex(Calls.TYPE));
					switch (type)
					{
					case Calls.MISSED_TYPE:
						if (csr.getInt(csr.getColumnIndex(Calls.NEW)) == 1)
						{
							missedCallCount++;
						}
						break;
					case Calls.INCOMING_TYPE:
						break;
					case Calls.OUTGOING_TYPE:
						break;
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			newCallCount = missedCallCount;// 未读电话数目
			csr.close();
		}

	}

}
