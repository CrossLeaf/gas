package com.chenghsi.lise.gas;


public class Constant {
    public static final String MAP_URL = "file:///android_asset/map.html";

    // The corresponding index for database table: "order"
    public static int ORDER_ID = 0;
    public static int ORDER_GET_TIME = 1;
    public static int ORDER_PREFER_TIME = 2;
    public static int ORDER_OVER_TIME = 3;
    public static int ORDER_STRIKE_BALANCE_TIME = 4;
    public static int ORDER_STATUS = 5;
    public static int ORDER_ACCEPT = 6;
    public static int ORDER_TASK = 7;
    public static int ORDER_PHONE = 8;
    public static int ORDER_CYLINDERS_LIST = 9;
    public static int ORDER_GAS_RESIDUAL = 10;
    public static int ORDER_DISCOUNT = 11;
    public static int ORDER_OTHER_SERVICE = 12;
    public static int ORDER_MONEY_CASH = 13;
    public static int ORDER_MONEY_CREDIT = 14;
    public static int ORDER_STRIKE_BALANCE = 15;
    public static int ORDER_HAPPY_DISCOUNT = 16;
    public static int ORDER_SHOULD_MONEY = 17;
    public static int ORDER_REMARK = 18;
    public static int ORDER_CUSTOMER_ID = 19;
    public static int ORDER_PRODUCT_ITEM_ID = 20;
    public static int ORDER_DAY = 21;
    public static int ORDER_ADDRESS = 22;
    public static int DELIVERY_TF = 23;
    public static int ORDER_CUSTOMER_NAME = 24;
    public static int ORDER_STAFF_HELP = 25;


    // The corresponding index for database table: "customer"
    public static int CUSTOMER_ID = 0;
    public static int CUSTOMER_BUILT_TIME = 1;
    public static int CUSTOMER_CHANGE_TIME = 2;
    public static int CUSTOMER_NAME = 3;
    public static int CUSTOMER_VAT_NUMBER = 4;
    public static int CUSTOMER_INVOICE_TITLE = 5;
    public static int CUSTOMER_INVOICE_ADDRESS = 6;
    public static int CUSTOMER_CONTACT_NAME = 7;
    public static int CUSTOMER_CONTACT_ADDRESS = 8;
    public static int CUSTOMER_EMAIL = 9;
    public static int CUSTOMER_FAX = 10;
    public static int CUSTOMER_REMARK = 11;
    public static int CUSTOMER_TYPE = 12;
    public static int CUSTOMER_SETTLE_TYPE = 13;
    public static int CUSTOMER_PRICE_CONDITIONS = 14;
    public static int CUSTOMER_TAX_RATE = 15;
    public static int CUSTOMER_SETTLE_DAY = 16;

    // The corresponding index for database table: "doddle"
    public static int DODDLE_ID = 0;
    public static int DODDLE_TIME = 1;
    public static int DODDLE_ACCEPT = 2;
    public static int DODDLE_ADDRESS = 3;
    public static int DODDLE_PRESSURE = 4;
    public static int DODDLE_1DEGREE_KG = 5;
    public static int DODDLE_GAS_UNIVALENT = 6;
    public static int DODDLE_THIS_PHASE_DEGREE = 7;
    public static int DODDLE_PRE_PHASE_DEGREE = 8;
    public static int DODDLE_USE_DEGREE = 9;
    public static int DODDLE_USE_AMOUNT = 10;
    public static int DODDLE_SHOULD_MONEY = 11;
    public static int DODDLE_REMARK = 12;
    public static int DODDLE_CUSTOMER_ID = 13;
    public static int DODDLE_STATUS = 14;

    // The corresponding index for database table: "price"
    public static int PRICE_ID = 0;
    public static int PRICE_CAPACITY = 1;
    public static int PRICE_20 = 2;
    public static int PRICE_16 = 3;
    public static int PRICE_BULID_DATE = 5;
    public static int PRICE_CHANGE_DATE = 6;
    public static int PRICE_REMARK = 8;


    // The corresponding index for database table: "delivery"
    public static int DELIVERY_ID = 0;
    public static int DELIVERY_BUILT_TIME = 1;
    public static int DELIVERY_STATUS = 2;
    public static int DELIVERY_RESERVE_TIME = 3;
    public static int DELIVERY_PREDICT_DAY_COUNT = 4;
    public static int DELIVERY_ORDER_ID = 5;

    // The corresponding index for database table: "phone"
    public static int PHONE_ID = 0;
    public static int PHONE_BUILT_TIME = 1;
    public static int PHONE_NUMBER = 2;
    public static int PHONE_CUSTOMER_ID = 3;

    // The corresponding index for database table: "facyln"
    public static int FACYLN_ID = 0;
    public static int SUPPLIERS_ID = 1;
    public static int FACYLN_CONTENT = 2;
    public static int FACYLN_CHANGE_DATE = 3;

    // The corresponding index for database table: "carcyln"
    public static int CARCYLN_ID = 0;
    public static int CAR_ID = 1;
    public static int CARCYLN_CONTENT = 2;
    public static int CARCYLN_CHANGE_DATE = 3;

    // The corresponding index for database table: "payment"
    public static int PAYMENT_ID = 0;
    public static int PAYMENT_TYPE = 1;
    public static int PAYMENT_STATUS = 2;
    public static int PAYMENT_CONTENT = 3;
    public static int PAYMENT_MONEY_CASH = 4;
    public static int PAYMENT_MONEY_CREDIT = 5;
    public static int PAYMENT_BUILD_DATE = 6;
    public static int PAYMENT_GET_DATE = 7;
    public static int PAYMENT_MONEY_REAL = 8;
    public static int SAVEOUT_ID = 9;


    // The corresponding index for database table: "cylinders"
    public static int cylinders_id=0;
    public static int cylinders_container_id=1;
    public static int cylinders_number=2;
    public static int cylinders_net_weight=3;
    public static int cylinders_purchase_price=4;
    public static int cylinders_capacity=5;
    public static int cylinders_type=6;
    public static int cylinders_buy_time=7;
    public static int cylinders_out_company_pressure_time=8;
    public static int cylinders_periodic_inspection_time=9;
    public static int cylinders_next_inspection_time=10;
    public static int cylinders_scrapped_time=11;
    public static int cylinders_inspection_company=12;
    public static int cylinders_remark=13;
    public static int suppliers_id=14;


}
