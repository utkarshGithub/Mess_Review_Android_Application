/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.example.utkarsh.mess.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MenuProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MenuDbHelper mOpenHelper;

    static final int DATE = 100;
    static final int MENU_WITH_BREAKFAST = 101;
    static final int MENU_WITH_LUNCH  = 101;
    static final int MENU_WITH_DINNER = 101;

    private static final SQLiteQueryBuilder sMenuByDateSettingQueryBuilder;

    static{
        sMenuByDateSettingQueryBuilder = new SQLiteQueryBuilder();
        
        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMenuByDateSettingQueryBuilder.setTables(
                MenuContract.MenuEntry.TABLE_NAME + " INNER JOIN " +
                        MenuContract.BreakfastEntry.TABLE_NAME +
                        " ON " + MenuContract.MenuEntry.TABLE_NAME +
                        "." + MenuContract.MenuEntry.COLUMN_BRE_KEY +
                        " = " + MenuContract.BreakfastEntry.TABLE_NAME +
                        "." + MenuContract.BreakfastEntry._ID + " INNER JOIN " +
                        MenuContract.LunchEntry.TABLE_NAME +
                        " ON " + MenuContract.MenuEntry.TABLE_NAME +
                        "." + MenuContract.MenuEntry.COLUMN_LUN_KEY +
                        " = " + MenuContract.LunchEntry.TABLE_NAME +
                        "." + MenuContract.LunchEntry._ID + " INNER JOIN " +
                        MenuContract.DinnerEntry.TABLE_NAME +
                        " ON " + MenuContract.MenuEntry.TABLE_NAME +
                        "." + MenuContract.MenuEntry.COLUMN_DIN_KEY +
                        " = " + MenuContract.DinnerEntry.TABLE_NAME +
                        "." + MenuContract.DinnerEntry._ID);
    }


        //location.location_setting = ? AND date = ?

    private static final String sDateAndBreakfastSelection =
            MenuContract.MenuEntry.TABLE_NAME +
                    "." + MenuContract.MenuEntry.COLUMN_DATE + " = ? AND " +
                    MenuContract.BreakfastEntry.COLUMN_ITEM_NAME + " = ? AND"+
    MenuContract.BreakfastEntry.COLUMN_ITEM_RATING + " = ?";
    private static final String sDateAndLunchSelection =
            MenuContract.MenuEntry.TABLE_NAME +
                    "." + MenuContract.MenuEntry.COLUMN_DATE + " = ? AND " +
                    MenuContract.BreakfastEntry.COLUMN_ITEM_NAME + " = ? AND"+
                    MenuContract.BreakfastEntry.COLUMN_ITEM_RATING + " = ?";

    private static final String sDateAndDinnerSelection =
            MenuContract.MenuEntry.TABLE_NAME +
                    "." + MenuContract.MenuEntry.COLUMN_DATE + " = ? AND " +
                    MenuContract.BreakfastEntry.COLUMN_ITEM_NAME + " = ? AND"+
                    MenuContract.BreakfastEntry.COLUMN_ITEM_RATING + " = ?";;


    private Cursor gettotaldatemenu (Uri uri) {
        String MealType = MenuContract.MenuEntry.getMealTypeFromUri(uri);
        long Date = MenuContract.MenuEntry.getDateFromUri(uri);

        String[] selectionArgs;
        String selection;


            selectionArgs = null;
            selection = null;
String[] projection = new String[]{MenuContract.MenuEntry.COLUMN_DATE,MenuContract.BreakfastEntry.COLUMN_ITEM_NAME,MenuContract.BreakfastEntry.COLUMN_ITEM_RATING,
        MenuContract.LunchEntry.COLUMN_ITEM_NAME,MenuContract.LunchEntry.COLUMN_ITEM_RATING,
        MenuContract.DinnerEntry.COLUMN_ITEM_NAME,MenuContract.DinnerEntry.COLUMN_ITEM_RATING};

        return sMenuByDateSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

    }


        /*
            Students: Here is where you need to create the UriMatcher. This UriMatcher will
            match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
            and LOCATION integer constants defined above.  You can test this by uncommenting the
            testUriMatcher test within TestUriMatcher.
         */
        static UriMatcher buildUriMatcher() {
            // I know what you're thinking.  Why create a UriMatcher when you can use regular
            // expressions instead?  Because you're not crazy, that's why.

            // All paths added to the UriMatcher have a corresponding code to return when a match is
            // found.  The code passed into the constructor represents the code to return for the root
            // URI.  It's common to use NO_MATCH as the code for this case.
            final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
            final String authority = MenuContract.CONTENT_AUTHORITY;

            // For each type of URI you want to add, create a corresponding code.
            matcher.addURI(authority, MenuContract.PATH_DATE, DATE);

            return matcher;
        }

        /*
            Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
            here.
         */
        @Override
        public boolean onCreate() {
            mOpenHelper = new MenuDbHelper(getContext());
            return true;
        }

        /*
            Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
            test this by uncommenting testGetType in TestProvider.

         */
        @Override
        public String getType(Uri uri) {

            // Use the Uri Matcher to determine what kind of URI this is.
            final int match = sUriMatcher.match(uri);

            switch (match) {
                // Student: Uncomment and fill out these two cases
                case DATE:
                    return MenuContract.MenuEntry.CONTENT_TYPE;

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
            // Here's the switch statement that, given a URI, will determine what kind of request it is,
            // and query the database accordingly.
            Cursor retCursor;
            switch (sUriMatcher.match(uri)) {
                // "weather/*/*"
                case DATE:
                {
                    retCursor = gettotaldatemenu(uri);
                    break;
                }



                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
            return retCursor;
        }

        /*
            Student: Add the ability to insert Locations to the implementation of this function.
         */



        private void normalizeDate(ContentValues values) {
            // normalize the date value
            if (values.containsKey(MenuContract.MenuEntry.COLUMN_DATE)) {
                long dateValue = values.getAsLong(MenuContract.MenuEntry.COLUMN_DATE);
                values.put(MenuContract.MenuEntry.COLUMN_DATE, MenuContract.normalizeDate(dateValue));
            }
        }


        @Override
        public int bulkInsert(Uri uri, ContentValues[] values) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case WEATHER:
                    db.beginTransaction();
                    int returnCount = 0;
                    try {
                        for (ContentValues value : values) {
                            normalizeDate(value);
                            long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                            if (_id != -1) {
                                returnCount++;
                            }
                        }
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    getContext().getContentResolver().notifyChange(uri, null);
                    return returnCount;
                default:
                    return super.bulkInsert(uri, values);
            }
        }

        // You do not need to call this method. This is a method specifically to assist the testing
        // framework in running smoothly. You can read more at:
        // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
        @Override
        @TargetApi(11)
        public void shutdown() {
            mOpenHelper.close();
            super.shutdown();
        }
    }

    //location.location_setting = ? AND date = ?
