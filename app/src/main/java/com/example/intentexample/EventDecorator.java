package com.example.intentexample;

import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.HashSet;
import java.util.Set;

public class EventDecorator implements DayViewDecorator {
    private final HashSet<CalendarDay> datesWithEvents;

    public EventDecorator(Set<CalendarDay> datesWithEvents) {
        this.datesWithEvents = new HashSet<>(datesWithEvents);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return datesWithEvents.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        int dotColor = Color.parseColor("#fcaf17");
        view.addSpan(new DotSpan(10, dotColor)); // Customize color and radius
    }
}