package com.mcucsie.flippedclass;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class FirstLoginActivity extends Activity implements OnGestureListener {
	private Animation left_in, left_out, right_in, right_out;
	private ViewFlipper viewFilpper;
	private GestureDetector detector;
	private Button btn_firstloginpass;
	private Bundle bundle;
	private int now = 0;
	private int picturecounts = 8;
	private LinearLayout layout;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_firstlogin);
		bundle = this.getIntent().getExtras();

		left_in = AnimationUtils.loadAnimation(this, R.anim.left_in);
		left_out = AnimationUtils.loadAnimation(this, R.anim.left_out);
		right_in = AnimationUtils.loadAnimation(this, R.anim.right_in);
		right_out = AnimationUtils.loadAnimation(this, R.anim.right_out);

		viewFilpper = (ViewFlipper) findViewById(R.id.viewFlipper1);
		detector = new GestureDetector(this);
		btn_firstloginpass = (Button) findViewById(R.id.first_login_pass);
		btn_firstloginpass.setOnClickListener(firstloginpass);

		viewFilpper.addView(getImageView(R.drawable.top1));
		viewFilpper.addView(getImageView(R.drawable.drawer2));
		viewFilpper.addView(getImageView(R.drawable.bb3));
		viewFilpper.addView(getImageView(R.drawable.exam5));
		viewFilpper.addView(getImageView(R.drawable.quickpick6));
		viewFilpper.addView(getImageView(R.drawable.chat7));
		viewFilpper.addView(getImageView(R.drawable.teamchat8));
		viewFilpper.addView(getImageView(R.drawable.setting9));

		// for(int i=0;i<8;i++){
		// viewFilpper.addView(getImageView(R.drawable.top1));
		// }

		layout = (LinearLayout) findViewById(R.id.fir_linear_layout);
		generatePageControl();

	}

	public ImageView getImageView(int id) {
		ImageView imageView = new ImageView(this);
		Bitmap bitmap = readBitMap(this, id);
		imageView.setImageBitmap(bitmap);
		return imageView;
	}

	public Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 獲取資源圖片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	private void generatePageControl() {
		layout.removeAllViews();

		for (int i = 0; i < picturecounts; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setPadding(0, 0, 0, 4);
			if (now == i) {
				imageView.setImageResource(R.drawable.pag01);// 當前選的圖片圓點
			} else {
				imageView.setImageResource(R.drawable.pag00);// 其他圖片圓點
			}
			this.layout.addView(imageView);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return this.detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if (e2.getX() - e1.getX() < -120) {
			viewFilpper.setInAnimation(left_in);
			viewFilpper.setOutAnimation(left_out);
			viewFilpper.showNext();
			now++;
			if (now > picturecounts - 1) {
				now = 0;
			}
			generatePageControl();
			return true;
		} else if (e2.getX() - e1.getX() > 120) {
			viewFilpper.setInAnimation(right_in);
			viewFilpper.setOutAnimation(right_out);
			viewFilpper.showPrevious();
			now--;
			if (now < 0) {
				now = picturecounts - 1;
			}
			generatePageControl();
			return true;
		}
		return false;
	}

	OnClickListener firstloginpass = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();

			intent.putExtras(bundle);
			intent.setClass(FirstLoginActivity.this, MainActivity.class);
			startActivity(intent);
			FirstLoginActivity.this.finish();
		}
	};
	
	
}
