/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bkhezry.persiandaterangepicker.date;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.github.bkhezry.persiandaterangepicker.HapticFeedbackController;
import com.github.bkhezry.persiandaterangepicker.R;
import com.github.bkhezry.persiandaterangepicker.TypefaceHelper;
import com.github.bkhezry.persiandaterangepicker.Utils;
import com.github.bkhezry.persiandaterangepicker.persiandateutils.LanguageUtils;
import com.github.bkhezry.persiandaterangepicker.persiandateutils.PersianDate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;


/**
 * Dialog allowing users to select a date.
 */
public class PersianDatePickerDialog extends DialogFragment implements
  OnClickListener, DatePickerController {

  private static final String TAG = "PersianDatePickerDialog";

  private static final int UNINITIALIZED = -1;
  private static final int MONTH_AND_DAY_VIEW = 0;
  private static final int YEAR_VIEW = 1;

  private static final String KEY_SELECTED_YEAR = "year";
  private static final String KEY_SELECTED_YEAR_END = "year_end";
  private static final String KEY_SELECTED_MONTH = "month";
  private static final String KEY_SELECTED_MONTH_END = "month_end";
  private static final String KEY_SELECTED_DAY = "day";
  private static final String KEY_SELECTED_DAY_END = "day_end";
  private static final String KEY_LIST_POSITION = "list_position";
  private static final String KEY_LIST_POSITION_END = "list_position_end";
  private static final String KEY_WEEK_START = "week_start";
  private static final String KEY_WEEK_START_END = "week_start_end";
  private static final String KEY_YEAR_START = "year_start";
  private static final String KEY_YEAR_START_END = "year_start_end";
  private static final String KEY_MAX_YEAR = "max_year";
  private static final String KEY_MAX_YEAR_END = "max_year_end";
  private static final String KEY_CURRENT_VIEW = "current_view";
  private static final String KEY_CURRENT_VIEW_END = "current_view_end";
  private static final String KEY_LIST_POSITION_OFFSET = "list_position_offset";
  private static final String KEY_LIST_POSITION_OFFSET_END = "list_position_offset_end";
  private static final String KEY_MIN_DATE = "min_date";
  private static final String KEY_MAX_DATE = "max_date";
  private static final String KEY_HIGHLIGHTED_DAYS = "highlighted_days";
  private static final String KEY_SELECTABLE_DAYS = "selectable_days";
  private static final String KEY_MIN_DATE_END = "min_date_end";
  private static final String KEY_MAX_DATE_END = "max_date_end";
  private static final String KEY_HIGHLIGHTED_DAYS_END = "highlighted_days_end";
  private static final String KEY_SELECTABLE_DAYS_END = "selectable_days_end";
  private static final String KEY_THEME_DARK = "theme_dark";
  private static final String KEY_ACCENT = "accent";
  private static final String KEY_VIBRATE = "vibrate";
  private static final String KEY_DISMISS = "dismiss";

  private static final int DEFAULT_START_YEAR = 1350;
  private static final int DEFAULT_END_YEAR = 1450;

  private static final int ANIMATION_DURATION = 300;
  private static final int ANIMATION_DELAY = 500;
  private final PersianDate mPersianDate = new PersianDate();
  private final PersianDate mPersianDateEnd = new PersianDate();
  private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());
  private static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());

  private OnDateSetListener mCallBack;
  private HashSet<OnDateChangedListener> mListeners = new HashSet<>();
  private DialogInterface.OnCancelListener mOnCancelListener;
  private DialogInterface.OnDismissListener mOnDismissListener;

  private PersianAccessibleDateAnimator mAnimator;

  private TextView mDayOfWeekView;
  private LinearLayout mMonthAndDayView;
  private TextView mSelectedMonthTextView;
  private TextView mSelectedDayTextView;
  private TextView mYearView;
  private DayPickerView mDayPickerView;
  private YearPickerView mYearPickerView;

  private int mCurrentView = UNINITIALIZED;
  private int mCurrentViewEnd = UNINITIALIZED;

  private int mWeekStart = Calendar.SATURDAY;
  private int mWeekStartEnd = Calendar.FRIDAY;
  private int mMinYear = DEFAULT_START_YEAR;
  private int mMaxYear = DEFAULT_END_YEAR;
  private PersianDate mMinDate;
  private PersianDate mMaxDate;
  private PersianDate[] highlightedDays;
  private PersianDate[] selectableDays;
  private PersianDate mMinDateEnd;
  private PersianDate mMaxDateEnd;
  private PersianDate[] highlightedDaysEnd;
  private PersianDate[] selectableDaysEnd;
  private boolean mAutoHighlight = false;

  private boolean mThemeDark;
  private int mAccentColor = -1;
  private boolean mVibrate;
  private boolean mDismissOnPause;

  private HapticFeedbackController mHapticFeedbackController;

  private boolean mDelayAnimation = true;

  // Accessibility strings.
  private String mDayPickerDescription;
  private String mSelectDay;
  private String mYearPickerDescription;
  private String mSelectYear;

  private TabHost tabHost;
  private LinearLayout mMonthAndDayViewEnd;
  private TextView mSelectedMonthTextViewEnd;
  private TextView mSelectedDayTextViewEnd;
  private TextView mYearViewEnd;
  private SimpleDayPickerView mDayPickerViewEnd;
  private YearPickerView mYearPickerViewEnd;
  private PersianAccessibleDateAnimator mAnimatorEnd;
  private int tabTag = 1;
  private String startTitle;
  private String endTitle;
  private String fontName = null;
  private Activity activity;

  /**
   * The callback used to indicate the user is done filling in the date.
   */
  public interface OnDateSetListener {

    /**
     * @param view        The view associated with this listener.
     * @param year        The year that was set.
     * @param monthOfYear The month that was set (0-11) for compatibility
     *                    with {@link Calendar}.
     * @param dayOfMonth  The day of the month that was set.
     */
    void onDateSet(PersianDatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd);
  }

  /**
   * The callback used to notify other date picker components of a change in selected date.
   */
  public interface OnDateChangedListener {

    void onDateChanged();
  }


  public PersianDatePickerDialog() {
    // Empty constructor required for dialog fragment.
  }

  /**
   * @param callBack    How the parent is notified that the date is set.
   * @param year        The initial year of the dialog.
   * @param monthOfYear The initial month of the dialog.
   * @param dayOfMonth  The initial day of the dialog.
   */
  public static PersianDatePickerDialog newInstance(OnDateSetListener callBack, int year,
                                                    int monthOfYear,
                                                    int dayOfMonth) {
    PersianDatePickerDialog ret = new PersianDatePickerDialog();
    ret.initialize(callBack, year, monthOfYear, dayOfMonth);
    return ret;
  }

  /**
   * @param callBack      How the parent is notified that the date is set.
   * @param year          The initial year of the dialog.
   * @param monthOfYear   The initial month of the dialog.
   * @param dayOfMonth    The initial day of the dialog.
   * @param yearEnd       The end year of the dialog.
   * @param montOfYearEnd The end month of the dialog.
   * @param dayOfMonthEnd The end day of the dialog.
   */
  public static PersianDatePickerDialog newInstance(OnDateSetListener callBack, int year,
                                                    int monthOfYear,
                                                    int dayOfMonth,
                                                    int yearEnd,
                                                    int montOfYearEnd,
                                                    int dayOfMonthEnd) {
    PersianDatePickerDialog ret = new PersianDatePickerDialog();
    ret.initialize(callBack, year, monthOfYear, dayOfMonth, yearEnd, montOfYearEnd, dayOfMonthEnd);
    return ret;
  }

  public void initialize(OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
    initialize(callBack, year, monthOfYear, dayOfMonth, year, monthOfYear, dayOfMonth);
  }

  public void initialize(OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth, int yearEnd,
                         int montOfYearEnd,
                         int dayOfMonthEnd) {
    mCallBack = callBack;
    mPersianDate.setPersianDate(year, monthOfYear, dayOfMonth);
    mPersianDateEnd.setPersianDate(yearEnd, montOfYearEnd, dayOfMonthEnd);
    mThemeDark = false;
    mAccentColor = -1;
    mVibrate = true;
    mDismissOnPause = false;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = getActivity();
    activity.getWindow().setSoftInputMode(
      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    if (savedInstanceState != null) {
      mPersianDate.setPersianDate(
        savedInstanceState.getInt(KEY_SELECTED_YEAR),
        savedInstanceState.getInt(KEY_SELECTED_MONTH),
        savedInstanceState.getInt(KEY_SELECTED_DAY)
      );
      mPersianDateEnd.setPersianDate(
        savedInstanceState.getInt(KEY_SELECTED_YEAR_END),
        savedInstanceState.getInt(KEY_SELECTED_MONTH_END),
        savedInstanceState.getInt(KEY_SELECTED_DAY_END)
      );
    }
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(KEY_SELECTED_YEAR, mPersianDate.getPersianYear());
    outState.putInt(KEY_SELECTED_MONTH, mPersianDate.getPersianMonth());
    outState.putInt(KEY_SELECTED_DAY, mPersianDate.getPersianDay());
    outState.putInt(KEY_WEEK_START, mWeekStart);
    outState.putInt(KEY_YEAR_START, mMinYear);
    outState.putInt(KEY_MAX_YEAR, mMaxYear);
    outState.putInt(KEY_CURRENT_VIEW, mCurrentView);
    outState.putInt(KEY_SELECTED_YEAR_END, mPersianDateEnd.getPersianYear());
    outState.putInt(KEY_SELECTED_MONTH_END, mPersianDateEnd.getPersianMonth());
    outState.putInt(KEY_SELECTED_DAY_END, mPersianDateEnd.getPersianDay());
    outState.putInt(KEY_WEEK_START_END, mWeekStartEnd);
    outState.putInt(KEY_YEAR_START_END, mMinYear);
    outState.putInt(KEY_MAX_YEAR_END, mMaxYear);
    outState.putInt(KEY_CURRENT_VIEW_END, mCurrentViewEnd);
    int listPosition = -1;
    int listPositionEnd = -1;
    if (mCurrentView == MONTH_AND_DAY_VIEW || mCurrentViewEnd == MONTH_AND_DAY_VIEW) {
      listPosition = mDayPickerView.getMostVisiblePosition();
      listPositionEnd = mDayPickerViewEnd.getMostVisiblePosition();

    } else if (mCurrentView == YEAR_VIEW || mCurrentViewEnd == YEAR_VIEW) {
      listPosition = mYearPickerView.getFirstVisiblePosition();
      listPositionEnd = mYearPickerViewEnd.getFirstVisiblePosition();
      outState.putInt(KEY_LIST_POSITION_OFFSET, mYearPickerView.getFirstPositionOffset());
      outState.putInt(KEY_LIST_POSITION_OFFSET_END, mYearPickerViewEnd.getFirstPositionOffset());
    }
    outState.putInt(KEY_LIST_POSITION, listPosition);
    outState.putInt(KEY_LIST_POSITION_END, listPositionEnd);
    outState.putSerializable(KEY_MIN_DATE, mMinDate);
    outState.putSerializable(KEY_MAX_DATE, mMaxDate);
    outState.putSerializable(KEY_MIN_DATE_END, mMinDateEnd);
    outState.putSerializable(KEY_MAX_DATE_END, mMaxDateEnd);
    outState.putSerializable(KEY_HIGHLIGHTED_DAYS, highlightedDays);
    outState.putSerializable(KEY_SELECTABLE_DAYS, selectableDays);
    outState.putSerializable(KEY_HIGHLIGHTED_DAYS_END, highlightedDaysEnd);
    outState.putSerializable(KEY_SELECTABLE_DAYS_END, selectableDaysEnd);
    outState.putBoolean(KEY_THEME_DARK, mThemeDark);
    outState.putInt(KEY_ACCENT, mAccentColor);
    outState.putBoolean(KEY_VIBRATE, mVibrate);
    outState.putBoolean(KEY_DISMISS, mDismissOnPause);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    View view = inflater.inflate(R.layout.range_date_picker_dialog, null);

    tabHost = view.findViewById(R.id.tabHost);
    tabHost.findViewById(R.id.tabHost);
    tabHost.setup();

    final Activity activity = getActivity();

    TabHost.TabSpec startDatePage = tabHost.newTabSpec("start");
    startDatePage.setContent(R.id.start_date_group);
    startDatePage.setIndicator((startTitle != null && !startTitle.isEmpty()) ? startTitle : activity.getResources().getString(R.string.mdtp_from));

    TabHost.TabSpec endDatePage = tabHost.newTabSpec("end");
    endDatePage.setContent(R.id.end_date_group);
    endDatePage.setIndicator((endTitle != null && !endTitle.isEmpty()) ? endTitle : activity.getResources().getString(R.string.mdtp_to));
    tabHost.addTab(endDatePage);
    tabHost.addTab(startDatePage);
    tabHost.setCurrentTab(1);
    if (fontName != null) {
      final TabWidget tw = tabHost.findViewById(android.R.id.tabs);
      for (int i = 0; i < tw.getChildCount(); ++i) {
        final View tabView = tw.getChildTabViewAt(i);
        final TextView tv = tabView.findViewById(android.R.id.title);
        tv.setTypeface(TypefaceHelper.get(activity, fontName));
      }
    }

    mDayOfWeekView = view.findViewById(R.id.date_picker_header);
    mMonthAndDayView = view.findViewById(R.id.date_picker_month_and_day);
    mMonthAndDayViewEnd = view.findViewById(R.id.date_picker_month_and_day_end);
    mMonthAndDayView.setOnClickListener(this);
    mMonthAndDayViewEnd.setOnClickListener(this);

    mSelectedMonthTextView = view.findViewById(R.id.date_picker_month);
    mSelectedMonthTextViewEnd = view.findViewById(R.id.date_picker_month_end);

    mSelectedDayTextView = view.findViewById(R.id.date_picker_day);
    mSelectedDayTextViewEnd = view.findViewById(R.id.date_picker_day_end);

    mYearView = view.findViewById(R.id.date_picker_year);
    mYearViewEnd = view.findViewById(R.id.date_picker_year_end);
    mYearView.setOnClickListener(this);
    mYearViewEnd.setOnClickListener(this);

    int listPosition = -1;
    int listPositionOffset = 0;
    int listPositionEnd = -1;
    int listPositionOffsetEnd = 0;
    int currentView = MONTH_AND_DAY_VIEW;
    int currentViewEnd = MONTH_AND_DAY_VIEW;
    if (savedInstanceState != null) {
      mWeekStart = savedInstanceState.getInt(KEY_WEEK_START);
      mWeekStartEnd = savedInstanceState.getInt(KEY_WEEK_START_END);
      mMinYear = savedInstanceState.getInt(KEY_YEAR_START);
      mMaxYear = savedInstanceState.getInt(KEY_MAX_YEAR);
      currentView = savedInstanceState.getInt(KEY_CURRENT_VIEW);
      currentViewEnd = savedInstanceState.getInt(KEY_CURRENT_VIEW_END);
      listPosition = savedInstanceState.getInt(KEY_LIST_POSITION);
      listPositionOffset = savedInstanceState.getInt(KEY_LIST_POSITION_OFFSET);
      listPositionEnd = savedInstanceState.getInt(KEY_LIST_POSITION_END);
      listPositionOffsetEnd = savedInstanceState.getInt(KEY_LIST_POSITION_OFFSET_END);
      mMinDate = (PersianDate) savedInstanceState.getSerializable(KEY_MIN_DATE);
      mMaxDate = (PersianDate) savedInstanceState.getSerializable(KEY_MAX_DATE);
      mMinDateEnd = (PersianDate) savedInstanceState.getSerializable(KEY_MIN_DATE_END);
      mMaxDateEnd = (PersianDate) savedInstanceState.getSerializable(KEY_MAX_DATE_END);
      highlightedDays = (PersianDate[]) savedInstanceState.getSerializable(KEY_HIGHLIGHTED_DAYS);
      selectableDays = (PersianDate[]) savedInstanceState.getSerializable(KEY_SELECTABLE_DAYS);
      highlightedDaysEnd = (PersianDate[]) savedInstanceState.getSerializable(KEY_HIGHLIGHTED_DAYS_END);
      selectableDaysEnd = (PersianDate[]) savedInstanceState.getSerializable(KEY_SELECTABLE_DAYS_END);
      mThemeDark = savedInstanceState.getBoolean(KEY_THEME_DARK);
      mAccentColor = savedInstanceState.getInt(KEY_ACCENT);
      mVibrate = savedInstanceState.getBoolean(KEY_VIBRATE);
      mDismissOnPause = savedInstanceState.getBoolean(KEY_DISMISS);
    }

    mDayPickerView = new SimpleDayPickerView(activity, this);
    mYearPickerView = new YearPickerView(activity, this);
    mDayPickerViewEnd = new SimpleDayPickerView(activity, this);
    mYearPickerViewEnd = new YearPickerView(activity, this);


    Resources res = getResources();
    mDayPickerDescription = res.getString(R.string.mdtp_day_picker_description);
    mSelectDay = res.getString(R.string.mdtp_select_day);
    mYearPickerDescription = res.getString(R.string.mdtp_year_picker_description);
    mSelectYear = res.getString(R.string.mdtp_select_year);

    int bgColorResource = mThemeDark ? R.color.mdtp_date_picker_view_animator_dark_theme : R.color.mdtp_date_picker_view_animator;
    view.setBackgroundColor(ContextCompat.getColor(activity, bgColorResource));

    mAnimator = view.findViewById(R.id.animator);
    mAnimatorEnd = view.findViewById(R.id.animator_end);

    mAnimator.addView(mDayPickerView);
    mAnimator.addView(mYearPickerView);
    mAnimator.setDateMillis(mPersianDate.getTimeInMillis());
    // TODO: Replace with animation decided upon by the design team.
    Animation animation = new AlphaAnimation(0.0f, 1.0f);
    animation.setDuration(ANIMATION_DURATION);
    mAnimator.setInAnimation(animation);
    // TODO: Replace with animation decided upon by the design team.
    Animation animation2 = new AlphaAnimation(1.0f, 0.0f);
    animation2.setDuration(ANIMATION_DURATION);
    mAnimator.setOutAnimation(animation2);

    mAnimatorEnd.addView(mDayPickerViewEnd);
    mAnimatorEnd.addView(mYearPickerViewEnd);
    mAnimatorEnd.setDateMillis(mPersianDateEnd.getTimeInMillis());
    // TODO: Replace with animation decided upon by the design team.
    Animation animationEnd = new AlphaAnimation(0.0f, 1.0f);
    animationEnd.setDuration(ANIMATION_DURATION);
    mAnimatorEnd.setInAnimation(animation);
    // TODO: Replace with animation decided upon by the design team.
    Animation animation2End = new AlphaAnimation(1.0f, 0.0f);
    animation2End.setDuration(ANIMATION_DURATION);
    mAnimatorEnd.setOutAnimation(animation2);

    Button okButton = view.findViewById(R.id.ok);
    okButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        tryVibrate();
        if (mCallBack != null) {
          mCallBack.onDateSet(PersianDatePickerDialog.this, mPersianDate.getPersianYear(),
            mPersianDate.getPersianMonth(), mPersianDate.getPersianDay(), mPersianDateEnd.getPersianYear(),
            mPersianDateEnd.getPersianMonth(), mPersianDateEnd.getPersianDay());
        }
        dismiss();
      }
    });


    Button cancelButton = view.findViewById(R.id.cancel);
    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        tryVibrate();
        if (getDialog() != null) {
          getDialog().cancel();
        }
      }
    });
    if (fontName == null) {
      okButton.setTypeface(TypefaceHelper.get(activity, "Roboto-Medium"));
      cancelButton.setTypeface(TypefaceHelper.get(activity, "Roboto-Medium"));
    } else {
      okButton.setTypeface(TypefaceHelper.get(activity, fontName));
      cancelButton.setTypeface(TypefaceHelper.get(activity, fontName));
    }

    cancelButton.setVisibility(isCancelable() ? View.VISIBLE : View.GONE);

    //If an accent color has not been set manually, try and get it from the context
    if (mAccentColor == -1) {
      int accentColor = Utils.getAccentColorFromThemeIfAvailable(getActivity());
      if (accentColor != -1) {
        mAccentColor = accentColor;
      }
    }
    if (mAccentColor != -1) {
      if (mDayOfWeekView != null) {
        mDayOfWeekView.setBackgroundColor(Utils.darkenColor(mAccentColor));
      }
      view.findViewById(R.id.day_picker_selected_date_layout).setBackgroundColor(mAccentColor);
      view.findViewById(R.id.day_picker_selected_date_layout_end).setBackgroundColor(mAccentColor);
      okButton.setTextColor(mAccentColor);
      cancelButton.setTextColor(mAccentColor);
      mYearPickerView.setAccentColor(mAccentColor);
      mDayPickerView.setAccentColor(mAccentColor);
      mYearPickerViewEnd.setAccentColor(mAccentColor);
      mDayPickerViewEnd.setAccentColor(mAccentColor);
    }

    updateDisplay(false);
    setCurrentView(currentView);

    if (listPosition != -1) {
      if (currentView == MONTH_AND_DAY_VIEW) {
        mDayPickerView.postSetSelection(listPosition);
      } else if (currentView == YEAR_VIEW) {
        mYearPickerView.postSetSelectionFromTop(listPosition, listPositionOffset);
      }
    }

    if (listPositionEnd != -1) {
      if (currentViewEnd == MONTH_AND_DAY_VIEW) {
        mDayPickerViewEnd.postSetSelection(listPositionEnd);
      } else if (currentViewEnd == YEAR_VIEW) {
        mYearPickerViewEnd.postSetSelectionFromTop(listPositionEnd, listPositionOffsetEnd);
      }
    }

    mHapticFeedbackController = new HapticFeedbackController(activity);

    tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
      @Override
      public void onTabChanged(String tabId) {
        MonthAdapter.CalendarDay calendarDay;
        if (tabId.equals("start")) {
          calendarDay = new MonthAdapter.CalendarDay(mPersianDate.getTimeInMillis());
          mDayPickerView.goTo(calendarDay, true, true, false);
        } else {
          calendarDay = new MonthAdapter.CalendarDay(mPersianDateEnd.getTimeInMillis());
          mDayPickerViewEnd.goTo(calendarDay, true, true, false);

        }
      }
    });
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    mHapticFeedbackController.start();
  }

  @Override
  public void onPause() {
    super.onPause();
    mHapticFeedbackController.stop();
    if (mDismissOnPause) {
      dismiss();
    }
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);
    if (mOnCancelListener != null) {
      mOnCancelListener.onCancel(dialog);
    }
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (mOnDismissListener != null) {
      mOnDismissListener.onDismiss(dialog);
    }
  }

  /**
   * Get whether auto highlighting is turned on or not
   *
   * @return true if on, false otherwise
   */
  @SuppressWarnings("unused")
  public boolean isAutoHighlight() {
    return mAutoHighlight;
  }

  /**
   * If set to true, all days between {@link #highlightedDays} and {@link #highlightedDaysEnd} will be highlighted.
   * This will reset manually inserted days to highlight using {@link #setHighlightedDays(PersianDate[], PersianDate[])}
   *
   * @param autoHighlight Set true to turn on auto highlighting, false otherwise
   */
  @SuppressWarnings("unused")
  public void setAutoHighlight(boolean autoHighlight) {
    this.mAutoHighlight = autoHighlight;
    if (autoHighlight) {
      calculateHighlightedDays();
    } else {
      highlightedDays = null;
      highlightedDaysEnd = null;
    }
  }

  private void setCurrentView(final int viewIndex) {
    long millis = mPersianDate.getTimeInMillis();
    long millisEnd = mPersianDateEnd.getTimeInMillis();

    switch (viewIndex) {
      case MONTH_AND_DAY_VIEW:
        ObjectAnimator pulseAnimator = Utils.getPulseAnimator(mMonthAndDayView, 0.9f,
          1.05f);
        ObjectAnimator pulseAnimatorTwo = Utils.getPulseAnimator(mMonthAndDayViewEnd, 0.9f,
          1.05f);
        if (mDelayAnimation) {
          pulseAnimator.setStartDelay(ANIMATION_DELAY);
          pulseAnimatorTwo.setStartDelay(ANIMATION_DELAY);
          mDelayAnimation = false;
        }
        mDayPickerView.onDateChanged();
        if (mCurrentView != viewIndex) {
          mMonthAndDayView.setSelected(true);
          mMonthAndDayViewEnd.setSelected(true);
          mYearView.setSelected(false);
          mYearViewEnd.setSelected(false);
          mAnimator.setDisplayedChild(MONTH_AND_DAY_VIEW);
          mAnimatorEnd.setDisplayedChild(MONTH_AND_DAY_VIEW);
          mCurrentView = viewIndex;
        }
        pulseAnimator.start();
        pulseAnimatorTwo.start();

        int flags = DateUtils.FORMAT_SHOW_DATE;
        String dayString = DateUtils.formatDateTime(getActivity(), millis, flags);
        String dayStringEnd = DateUtils.formatDateTime(getActivity(), millisEnd, flags);
        mAnimator.setContentDescription(mDayPickerDescription + ": " + dayString);
        mAnimatorEnd.setContentDescription(mDayPickerDescription + ": " + dayStringEnd);
        Utils.tryAccessibilityAnnounce(mAnimator, mSelectDay);
        Utils.tryAccessibilityAnnounce(mAnimatorEnd, mSelectDay);
        break;
      case YEAR_VIEW:
        pulseAnimator = Utils.getPulseAnimator(mYearView, 0.85f, 1.1f);
        pulseAnimatorTwo = Utils.getPulseAnimator(mYearViewEnd, 0.85f, 1.1f);
        if (mDelayAnimation) {
          pulseAnimator.setStartDelay(ANIMATION_DELAY);
          pulseAnimatorTwo.setStartDelay(ANIMATION_DELAY);
          mDelayAnimation = false;
        }
        mYearPickerView.onDateChanged();
        mYearPickerViewEnd.onDateChanged();
        if (mCurrentView != viewIndex) {
          mMonthAndDayView.setSelected(false);
          mYearView.setSelected(true);
          mAnimator.setDisplayedChild(YEAR_VIEW);
          mCurrentView = viewIndex;

          mMonthAndDayViewEnd.setSelected(false);
          mYearViewEnd.setSelected(true);
          mAnimatorEnd.setDisplayedChild(YEAR_VIEW);
          mCurrentViewEnd = viewIndex;
        }
        pulseAnimator.start();
        pulseAnimatorTwo.start();

        CharSequence yearString = YEAR_FORMAT.format(millis);
        CharSequence yearStringEnd = YEAR_FORMAT.format(millisEnd);
        mAnimator.setContentDescription(mYearPickerDescription + ": " + yearString);
        mAnimatorEnd.setContentDescription(mYearPickerDescription + ": " + yearStringEnd);
        Utils.tryAccessibilityAnnounce(mAnimator, mSelectYear);
        Utils.tryAccessibilityAnnounce(mAnimatorEnd, mSelectYear);
        break;
    }
  }

  private void updateDisplay(boolean announce) {
    if (mDayOfWeekView != null) {
      mDayOfWeekView.setText(mPersianDate.getPersianWeekDayName());
    }

    mSelectedMonthTextView.setText(LanguageUtils.
      getPersianNumbers(String.valueOf(mPersianDate.getPersianMonthName())));
    mSelectedMonthTextViewEnd.setText(LanguageUtils.
      getPersianNumbers(String.valueOf(mPersianDateEnd.getPersianMonthName())));
    mSelectedDayTextView.setText(LanguageUtils.
      getPersianNumbers(String.valueOf(mPersianDate.getPersianDay())));
    mSelectedDayTextViewEnd.setText(LanguageUtils.
      getPersianNumbers(String.valueOf(mPersianDateEnd.getPersianDay())));
    mYearView.setText(LanguageUtils.
      getPersianNumbers(String.valueOf(mPersianDate.getPersianYear())));
    mYearViewEnd.setText(LanguageUtils.
      getPersianNumbers(String.valueOf(mPersianDateEnd.getPersianYear())));
    if (fontName != null) {
      mSelectedMonthTextView.setTypeface(TypefaceHelper.get(activity, fontName));
      mSelectedMonthTextViewEnd.setTypeface(TypefaceHelper.get(activity, fontName));
      mSelectedDayTextView.setTypeface(TypefaceHelper.get(activity, fontName));
      mSelectedDayTextViewEnd.setTypeface(TypefaceHelper.get(activity, fontName));
      mYearView.setTypeface(TypefaceHelper.get(activity, fontName));
      mYearViewEnd.setTypeface(TypefaceHelper.get(activity, fontName));
    }
    // Accessibility.
    long millis = mPersianDate.getTimeInMillis();
    long millisEnd = mPersianDateEnd.getTimeInMillis();
    mAnimator.setDateMillis(millis);
    mAnimatorEnd.setDateMillis(millisEnd);
    int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR;
    String monthAndDayText = DateUtils.formatDateTime(getActivity(), millis, flags);
    String monthAndDayTextEnd = DateUtils.formatDateTime(getActivity(), millisEnd, flags);
    mMonthAndDayView.setContentDescription(monthAndDayText);
    mMonthAndDayViewEnd.setContentDescription(monthAndDayTextEnd);

    if (announce) {
      flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR;
      String fullDateText = DateUtils.formatDateTime(getActivity(), millis, flags);
      String fullDateTextEnd = DateUtils.formatDateTime(getActivity(), millisEnd, flags);
      Utils.tryAccessibilityAnnounce(mAnimator, fullDateText);
      Utils.tryAccessibilityAnnounce(mAnimatorEnd, fullDateTextEnd);
    }
  }

  /**
   * Set whether the device should vibrate when touching fields
   *
   * @param vibrate true if the device should vibrate when touching a field
   */
  public void vibrate(boolean vibrate) {
    mVibrate = vibrate;
  }

  /**
   * Set whether the picker should dismiss itself when being paused or whether it should try to survive an orientation change
   *
   * @param dismissOnPause true if the dialog should dismiss itself when it's pausing
   */
  public void dismissOnPause(boolean dismissOnPause) {
    mDismissOnPause = dismissOnPause;
  }

  /**
   * Set whether the dark theme should be used
   *
   * @param themeDark true if the dark theme should be used, false if the default theme should be used
   */
  public void setThemeDark(boolean themeDark) {
    mThemeDark = themeDark;
  }

  /**
   * Returns true when the dark theme should be used
   *
   * @return true if the dark theme should be used, false if the default theme should be used
   */
  @Override
  public boolean isThemeDark() {
    return mThemeDark;
  }

  /**
   * Set the accent color of this dialog
   *
   * @param accentColor the accent color you want
   */
  public void setAccentColor(int accentColor) {
    mAccentColor = accentColor;
  }

  /**
   * Get the accent color of this dialog
   *
   * @return accent color
   */
  public int getAccentColor() {
    return mAccentColor;
  }

  @SuppressWarnings("unused")
  public void setFirstDayOfWeek(int startOfWeek, int startWeekEnd) {
    if (startOfWeek < Calendar.SUNDAY || startOfWeek > Calendar.SATURDAY) {
      throw new IllegalArgumentException("Value must be between Calendar.SUNDAY and " +
        "Calendar.SATURDAY");
    }
    mWeekStart = startOfWeek;
    mWeekStartEnd = startWeekEnd;

    if (mDayPickerView != null) {
      mDayPickerView.onChange();
    }

    if (mDayPickerViewEnd != null) {
      mDayPickerViewEnd.onChange();
    }
  }

  @SuppressWarnings("unused")
  public void setYearRange(int startYear, int endYear) {
    if (endYear < startYear) {
      throw new IllegalArgumentException("Year end must be larger than or equal to year start");
    }

    mMinYear = startYear;
    mMaxYear = endYear;
    if (mDayPickerView != null && mDayPickerViewEnd != null) {
      mDayPickerView.onChange();
      mDayPickerViewEnd.onChange();
    }
  }

  /**
   * Sets the minimal date supported by this DatePicker. Dates before (but not including) the
   * specified date will be disallowed from being selected.
   *
   * @param calendar a Calendar object set to the year, month, day desired as the mindate.
   */
  @SuppressWarnings("unused")
  public void setMinDate(PersianDate calendar) {
    mMinDate = calendar;

    if (mDayPickerView != null && mDayPickerViewEnd != null) {
      mDayPickerView.onChange();
      mDayPickerViewEnd.onChange();
    }
  }

  /**
   * @return The minimal date supported by this DatePicker. Null if it has not been set.
   */
  @Override
  public PersianDate getMinDate() {
    return mMinDate;
  }

  /**
   * Sets the minimal date supported by this DatePicker. Dates after (but not including) the
   * specified date will be disallowed from being selected.
   *
   * @param calendar a Calendar object set to the year, month, day desired as the maxdate.
   */
  @SuppressWarnings("unused")
  public void setMaxDate(PersianDate calendar) {
    mMaxDate = calendar;

    if (mDayPickerView != null && mDayPickerViewEnd != null) {
      mDayPickerView.onChange();
      mDayPickerViewEnd.onChange();
    }
  }

  /**
   * @return The maximal date supported by this DatePicker. Null if it has not been set.
   */
  @Override
  public PersianDate getMaxDate() {
    return mMaxDate;
  }

  /**
   * Sets an array of dates which should be highlighted when the picker is drawn
   * This will turn off auto highlighting.
   *
   * @param highlightedDays an Array of Calendar objects containing the dates to be highlighted
   */
  @SuppressWarnings("unused")
  public void setHighlightedDays(PersianDate[] highlightedDays, PersianDate[] highlightedDaysEnd) {
    mAutoHighlight = false;

    // Sort the array to optimize searching over it later on
    Arrays.sort(highlightedDays);
    Arrays.sort(highlightedDaysEnd);
    this.highlightedDays = highlightedDays;
    this.highlightedDaysEnd = highlightedDaysEnd;
  }

  /**
   * @return The list of dates, as Calendar Objects, which should be highlighted. null is no dates should be highlighted
   */
  @Override
  public PersianDate[] getHighlightedDays() {
    return highlightedDays;
  }

  /**
   * Set's a list of days which are the only valid selections.
   * Setting this value will take precedence over using setMinDate() and setMaxDate()
   *
   * @param selectableDays an Array of Calendar Objects containing the selectable dates
   */
  @SuppressWarnings("unused")
  public void setSelectableDays(PersianDate[] selectableDays) {
    // Sort the array to optimize searching over it later on
    Arrays.sort(selectableDays);
    this.selectableDays = selectableDays;
  }

  @SuppressWarnings("unused")
  public void setSelectableDaysEnd(PersianDate[] selectableDaysEnd) {
    // Sort the array to optimize searching over it later on
    Arrays.sort(selectableDaysEnd);
    this.selectableDaysEnd = selectableDaysEnd;
  }

  /**
   * @return an Array of Calendar objects containing the list with selectable items. null if no restriction is set
   */
  @Override
  public PersianDate[] getSelectableDays() {
    return selectableDays;
  }


  @SuppressWarnings("unused")
  public void setOnDateSetListener(OnDateSetListener listener) {
    mCallBack = listener;
  }

  @SuppressWarnings("unused")
  public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
    mOnCancelListener = onCancelListener;
  }

  @SuppressWarnings("unused")
  public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
    mOnDismissListener = onDismissListener;
  }

  // If the newly selected month / year does not contain the currently selected day number,
  // change the selected day number to the last day of the selected month or year.
  //      e.g. Switching from Mar to Apr when Mar 31 is selected -> Apr 30
  //      e.g. Switching from 2012 to 2018 when Feb 29, 2012 is selected -> Feb 28, 2018
  private void adjustDayInMonthIfNeeded(Calendar calendar) {
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    if (day > daysInMonth) {
      calendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
    }
  }

  @Override
  public void onClick(View v) {
    tryVibrate();
    if (v.getId() == R.id.date_picker_year || v.getId() == R.id.date_picker_year_end) {
      setCurrentView(YEAR_VIEW);
    } else if (v.getId() == R.id.date_picker_month_and_day || v.getId() == R.id.date_picker_month_and_day_end) {
      setCurrentView(MONTH_AND_DAY_VIEW);
    }
  }

  @Override
  public void onYearSelected(int year) {
    //TODO
//    adjustDayInMonthIfNeeded(mPersianDate);
//    adjustDayInMonthIfNeeded(mPersianDateEnd);

    if (tabHost.getCurrentTab() == 1) {
      mPersianDate.setPersianDate(year, mPersianDate.getPersianMonth(),
        mPersianDate.getPersianDay());
    } else {
      mPersianDateEnd.setPersianDate(year, mPersianDateEnd.getPersianMonth(),
        mPersianDateEnd.getPersianDay());
    }
    updatePickers();
    setCurrentView(MONTH_AND_DAY_VIEW);
    updateDisplay(true);
  }

  @Override
  public void onDayOfMonthSelected(int year, int month, int day) {

    if (tabHost.getCurrentTab() == 1) {
      mPersianDate.setPersianDate(year, month, day);
    } else {
      mPersianDateEnd.setPersianDate(year, month, day);
    }

    if (mAutoHighlight) {
      calculateHighlightedDays();
    }

    updatePickers();
    updateDisplay(true);
  }

  private void calculateHighlightedDays() {
    int numDays = (int) Math.round(
      (mPersianDateEnd.getTimeInMillis() - mPersianDate.getTimeInMillis()) / 86400000d);

    // In case user chooses an end day before the start day.
    int dir = 1;
    if (numDays < 0) {
      dir = -1;
    }

    numDays = Math.abs(numDays);

    // +1 to account for the end day which should be highlighted as well
    highlightedDays = new PersianDate[numDays + 1];

    for (int i = 0; i < numDays; i++) {
      PersianDate persianDate = new PersianDate();
      persianDate.setPersianDate(mPersianDate.getPersianYear(), mPersianDate.getPersianMonth(), mPersianDate.getPersianDay() + (i * dir));
      highlightedDays[i] = persianDate;
    }
    highlightedDays[numDays] = mPersianDateEnd;
    highlightedDaysEnd = highlightedDays;
  }

  private void updatePickers() {
    for (OnDateChangedListener listener : mListeners) {
      listener.onDateChanged();
    }
  }


  @Override
  public MonthAdapter.CalendarDay getSelectedDay() {
    if (tabHost.getCurrentTab() == 1) {
      return new MonthAdapter.CalendarDay(mPersianDate);
    } else {
      return new MonthAdapter.CalendarDay(mPersianDateEnd);
    }

  }

  @Override
  public int getMinYear() {
    if (selectableDays != null) {
      return selectableDays[0].get(Calendar.YEAR);
    }
    // Ensure no years can be selected outside of the given minimum date
    return mMinDate != null && mMinDate.get(Calendar.YEAR) > mMinYear ? mMinDate.get(Calendar.YEAR) : mMinYear;
  }

  @Override
  public int getMaxYear() {
    if (selectableDays != null) {
      return selectableDays[selectableDays.length - 1].get(Calendar.YEAR);
    }
    // Ensure no years can be selected outside of the given maximum date
    return mMaxDate != null && mMaxDate.get(Calendar.YEAR) < mMaxYear ? mMaxDate.get(Calendar.YEAR) : mMaxYear;
  }

  @Override
  public int getFirstDayOfWeek() {
    return mWeekStart;
  }

  @Override
  public void registerOnDateChangedListener(OnDateChangedListener listener) {
    mListeners.add(listener);
  }

  @Override
  public void unregisterOnDateChangedListener(OnDateChangedListener listener) {
    mListeners.remove(listener);
  }

  @Override
  public void tryVibrate() {
    if (mVibrate) {
      mHapticFeedbackController.tryVibrate();
    }
  }

  @Override
  public void setTypeface(String fontName) {
    this.fontName = fontName;
  }

  @Override
  public String getTypeface() {
    return this.fontName;
  }

  /**
   * setStartTitle
   *
   * @param startTitle the title to display for start panel
   */
  public void setStartTitle(String startTitle) {
    this.startTitle = startTitle;
  }

  /**
   * setEndTitle
   *
   * @param endTitle the title to display for end panel
   */
  public void setEndTitle(String endTitle) {
    this.endTitle = endTitle;
  }
}
