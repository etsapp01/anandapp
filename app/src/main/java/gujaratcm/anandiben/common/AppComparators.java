/**
 * Copyright (c) @ Samcom Technobrains.
 * AvantarMay 4, 2011
 **/
package gujaratcm.anandiben.common;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;


public class AppComparators {

    private static AppComparators _instance = null;

    public static AppComparators Instance() {
        if (_instance == null) {
            synchronized (AppComparators.class) {
                _instance = new AppComparators();
            }
        }
        return _instance;
    }

    public Comparator<String> ComparatorByStringAtoZ = new Comparator<String>() {

        public int compare(String object1, String object2) {
            return object1.compareTo(object2);
        }
    };

//	public Comparator<OrderInfo> OrderComparator = new Comparator<OrderInfo>() {
//
//		public int compare(OrderInfo object1, OrderInfo object2) {
//			// 2012-02-21 12:36:46
//			SimpleDateFormat dateformat = new SimpleDateFormat(
//					"yyyy-MM-dd HH:mm:ss");
//			Date dt1 = new Date();
//			Date dt2 = new Date();
//			try {
//				dt1 = dateformat.parse(object1.created);
//				dt2 = dateformat.parse(object2.created);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//
//			return dt2.compareTo(dt1);
//		}
//	};

//    public Comparator<MyAudioBook> comparerecently = new
//            Comparator<MyAudioBook>() {
//
//                public int compare(MyAudioBook object1, MyAudioBook object2) {
//                    // 2012-02-21 12:36:46
//                    //"yyyy-MM-dd HH:mm:ss" "MM/dd/yyyy hh:mm a"
////                    Tue Feb 09 17:19:49 GMT+05:30 2016
//                    Date d1 = new Date(Utils.ConvertToLong(object1.LastUsed));
//                    Date d2 = new Date(Utils.ConvertToLong(object2.LastUsed));
//
//                    String ddt1 = Utils.Instance().ChangeDateFormat("EEE MMM dd hh:mm:ss ZZZ yyyy", "yyyy-MM-dd HH:mm:ss", d1.toString());
//                    String ddt2 = Utils.Instance().ChangeDateFormat("EEE MMM dd hh:mm:ss ZZZ yyyy", "yyyy-MM-dd HH:mm:ss", d2.toString());
//
//                    SimpleDateFormat dateformat = new SimpleDateFormat(
//                            "yyyy-MM-dd HH:mm:ss");
//                    Date dt1 = new Date();
//                    Date dt2 = new Date();
//                    try {
//                        dt1 = dateformat.parse(ddt1);
//                        dt2 = dateformat.parse(ddt2);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    return dt2.compareTo(dt1);
//                }
//            };

}
