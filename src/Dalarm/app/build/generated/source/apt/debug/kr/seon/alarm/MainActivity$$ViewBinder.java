// Generated code from Butter Knife. Do not modify!
package kr.seon.alarm;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends kr.seon.alarm.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689630, "field 'mSwitch', method 'toggle', and method 'slide'");
    target.mSwitch = finder.castView(view, 2131689630, "field 'mSwitch'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.toggle(p1);
        }
      });
    view.setOnTouchListener(
      new android.view.View.OnTouchListener() {
        @Override public boolean onTouch(
          android.view.View p0,
          android.view.MotionEvent p1
        ) {
          return target.slide(p1);
        }
      });
  }

  @Override public void unbind(T target) {
    target.mSwitch = null;
  }
}
