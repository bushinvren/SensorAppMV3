/**
 * 
 */
package com.example.sensorapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sensorapp.R;
import com.example.sensorapp.util.PhoneUtil;

/**
 * @author Administrator
 * 
 */
public class CallOutPageFragment extends Object implements View.OnClickListener {
	private View view;
	private Context context;

	private TextView[] phoneNumbers;
	private final static int rIds[] = { R.id.textViewDailNumber0,
			R.id.textViewDailNumber1, R.id.textViewDailNumber2,
			R.id.textViewDailNumber3, R.id.textViewDailNumber4,
			R.id.textViewDailNumber5, R.id.textViewDailNumber6,
			R.id.textViewDailNumber7, R.id.textViewDailNumber8,
			R.id.textViewDailNumber9, R.id.textViewDailStar,
			R.id.textViewDailPound };
	private TextView inputArea;

	private ImageView deleteBtn;
	private View callBtn;

	// 保存静态值。对象共享。
	private  String callNumber = "";

	private final static int maxShow = 15;

	public CallOutPageFragment(Context context) {
		this.context = context;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup root,
			Bundle bundle) {

		view = inflater.inflate(R.layout.calloutpage, null);

		// context = getActivity();
		init();
		return view;
	}

	public void init() {
		inputArea = (TextView) view.findViewById(R.id.textViewDailTelNumber);
		deleteBtn = (ImageView) view.findViewById(R.id.imageViewDailDelete);
		callBtn = view.findViewById(R.id.relativeLayout6);
		phoneNumbers = new TextView[rIds.length];

		for (int i = 0; i < rIds.length; i++) {
			phoneNumbers[i] = (TextView) view.findViewById(rIds[i]);
			phoneNumbers[i].setOnClickListener(this);
		}

		deleteBtn.setOnClickListener(this);
		// 长按清空输入。
		deleteBtn.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				callNumber = "";
				inputArea.setText("");
				return false;
			}
		});
		callBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageViewDailDelete: {
			if (callNumber.length() >= 1) {
				callNumber = callNumber.substring(0, callNumber.length() - 1);
			} else {
				callNumber = "";
			}
		}
			break;
		case R.id.relativeLayout6: {
			// 打电话。 此处有可能弹出多个可以呼叫的选择菜单。 需要进入步指定确定的包名。。
			new PhoneUtil(context).makCall(callNumber);
			callNumber = "";
			break;
		}
		case R.id.textViewDailNumber0:
		case R.id.textViewDailNumber1:
		case R.id.textViewDailNumber2:
		case R.id.textViewDailNumber3:
		case R.id.textViewDailNumber4:
		case R.id.textViewDailNumber5:
		case R.id.textViewDailNumber6:
		case R.id.textViewDailNumber7:
		case R.id.textViewDailNumber8:
		case R.id.textViewDailNumber9:
		case R.id.textViewDailPound:
		case R.id.textViewDailStar: {

			callNumber += ((TextView) v).getText();
			break;
		}

		default: {

		}
			break;
		}
		if (callNumber.length() > maxShow) {
			inputArea.setText(callNumber.subSequence(callNumber.length()
					- maxShow, callNumber.length()));
		} else {
			inputArea.setText(callNumber);
		}
	}
}
