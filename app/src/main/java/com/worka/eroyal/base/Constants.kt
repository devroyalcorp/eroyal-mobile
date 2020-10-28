package com.worka.eroyal.base

import android.Manifest

object Constants {
    const val IS_FROM_BOTTOM_BAR = "is-from-bottom-bar"
    const val USER = "user"
    const val ACCESS_TOKEN = "access-token"
    const val RESET_PASSWORD_TOKEN = "reset-password-token"
    const val TOKEN = "token"
    const val CLIENT = "client"
    const val UID = "uid"
    const val JPG = ".jpg"
    const val TEXT_PLAIN = "text/plain"
    const val IMAGE_TYPE = "image/jpeg"
    const val CLIENT_SIGNATURE_FILENAME= "client_signature_"
    const val EROYAL_FILENAME= "eroyal_"
    const val IMAGE_PROFILE_KEY = "user[image_profile]"
    const val IMAGE_FOLLOW_UP_VISIT = "visit_result[location_target]"
    const val IMAGE_PROFILE_CUSTOMER = "customer[image_profile]"
    const val IMAGE_CLOCK_IN = "absence[selfie]"
    const val EXTERNAL_SCOPE = "external"
    const val INTERNAL_SCOPE = "internal"
    const val SPG = "spg"
    const val SALES = "sales"
    const val DOC_URL = "api/v1/sales_orders/"

    const val ACTIVE_CUSTOMER = "active"

    const val REQUEST_LOCATION = 1
    const val REQUEST_CAMERA = 2
    val LOCATION_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    val CAMERA_STORAGE_PERMISSION = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    const val WRONG_ADDRESS = "Wrong address"
    const val TARGET_DOESNT_EXIST = "Target doesn't exist"

    const val FLOW_TYPE = "flow_type"
    const val TASK = "task"
    const val VISIT = "visit"
    const val ABSENCE = "absence"

    const val MENU_ITEM = "menu-item"
    const val MY_CUSTOMER_MENU = "eroyal_my_customers_index"
    const val MY_TEAMS_MENU = "eroyal_my_teams_index"
    const val MY_REPORTS_MENU = "eroyal_my_reports_index"
    const val MY_BRANCH_REPORT = "eroyal_report_by_branches_index"
    const val MY_BRANDS_REPORT = "eroyal_report_by_brands_index"
    const val BY_SALES_REPORT = "eroyal_report_by_sales_index"
    const val BRANCH = "branch"
    const val ROLE = "role"
    const val SELECTED_CUSTOMER = "selected_customer"
    const val LIMIT_GET_DATA = 20
    const val LIMIT_RADIUS = 100

    const val DECREASED = "decreased"
    const val INCREASED = "increased"

    const val SALES_ID = "sales-id"
    const val DATE = "date"
    const val MONTH = "month"
    const val DAILY = "daily"
    const val MONTHLY = "monthly"
    const val FREE_VISIT = "Free Visit"
    const val VISIT_RKB = "Visit RKB"

    const val ME_RKB_VISITS = 3
    const val ME_SALES_VALUE = 4
    const val TABLE_SALES_REPORT = 4
    const val SALES_REPORT_BY_ARTICLE_COLUMN_SIZE = 5
    const val SALES_REPORT_BY_CUSTOMER_COLUMN_SIZE = 11
    const val CUSTOMER_ACTIVE_RECAP_COLUMN_SIZE = 11
    const val CUSTOMER_ACTIVE_DETAIL_COLUMN_SIZE = 10
    const val WEEKLY_WORK_PLAN_COLUMN_SIZE = 10
    const val OUT_STANDING_ORDER_COLUMN_SIZE = 10


    const val FIREBASE_EVENT_FAKE_GPS = "fake_gps_detected"
    const val FIREBASE_EVENT_USER_ID = "user_id"

    /* QUALIFIER */

    const val LONG_TIME_OUT = "long-time-out"

    /* DEEPLINK */
    const val FORGOT_PASSWORD = "forgot-password"

}
