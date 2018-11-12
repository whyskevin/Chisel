package com.example.creatingahabit;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;

public class SharedData {
    private static SharedData single_instance = null;

    public TreeMap<Integer, ArrayList> completeCalendarDates;
    public TreeMap<Integer,ArrayList> incompleteCalendarDates;
    public Hashtable<Integer, MaterialCalendarView> allCalendars;

    public Hashtable<Integer, MainActivity.ViewHolder> d;

    private SharedData(){
        completeCalendarDates = new TreeMap<>();
        incompleteCalendarDates = new TreeMap<>();
        allCalendars = new Hashtable<>();
        d = new Hashtable<>();
    }

    public static SharedData getInstance(){
        if(single_instance == null){
            single_instance = new SharedData();
        }
            return single_instance;
    }

}

