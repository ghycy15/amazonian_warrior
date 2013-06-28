package com.hackathon.game1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;

/**
 * A class to help save and manage Accelerometer info in SQLite database on an Android
 * device.
 * 
 * @author Xingyue Zhou
 * 
 */
public class AccelmDatabase extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "amazon.game.database.accelm";

	private static final int VERSION = 1;

	/**
	 * Accelerometer table
	 */

	public static final String ACCELM_TABLE = "AccelmTable";

	public AccelmDatabase(Context context) {
		this(context, DATABASE_NAME, null, VERSION);
	}

	public AccelmDatabase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createAccelm(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	// create a Accelerometer table

	/**
	 * Create a SQLite database in Android device to save Accelerometer info.
	 * 
	 * @param AccelmDatabase
	 */
	public void createAccelm(SQLiteDatabase AccelmDatabase) {
		String sql = "create table "
				+ ACCELM_TABLE
				+ "(_id integer primary key autoincrement,label varchr(40),time varchr(40), x_axis varchar(40),y_axis varchar(40),z_axis varchar(60))";
		AccelmDatabase.execSQL(sql);
	}

	public void insertAccelm(String label,String time, String x_axis, String y_axis,
			String z_axis) {
		ContentValues c = new ContentValues();
		c.put("label", label);
		

		c.put("time", time);
		c.put("x_axis", x_axis);
		c.put("y_axis", y_axis);
		c.put("z_axis", z_axis);
	

		AccelmDatabase.this.getWritableDatabase().insert(ACCELM_TABLE, null, c);
	}

	
	public void deleteAccelm(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(ACCELM_TABLE, null, null);
		db.close();
	}
	
	
	/**
	 * List all GPS record in database.
	 * 
	 * @return List<String> All GPS record
	 */
	// public List<String> listGPS() {
	// List<String> list = new ArrayList<String>();
	// SQLiteDatabase db = this.getWritableDatabase();
	// Cursor listGPSCursor = null;
	// try {
	// listGPSCursor = db.query(ACCELM_TABLE, null, null, null, null, null,
	// null, null);
	// while (listGPSCursor.moveToNext()) {
	//
	// String longitude = listGPSCursor.getString(listGPSCursor
	// .getColumnIndex("longitude"));
	// String latitude = listGPSCursor.getString(listGPSCursor
	// .getColumnIndex("latitude"));
	// String time = listGPSCursor.getString(listGPSCursor
	// .getColumnIndex("time"));
	// String location = listGPSCursor.getString(listGPSCursor
	// .getColumnIndex("location"));
	// list.add("时间:" + time + "\n" + "经度:" + longitude + "\n" + "纬度:"
	// + latitude + "\n" + "参考位置：" + location);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// listGPSCursor.close();
	// db.close();
	// }
	// return list;
	// }

	// /**
	// * Delete all GPS record in database.
	// */
	// public void deleteGPS() {
	// SQLiteDatabase db = this.getWritableDatabase();
	// db.delete(GPS_TABLE, null, null);
	// db.close();
	// }

	
	
	
	/**
	 * Save Accelerometer information into String format.
	 */
	public void makeAccelmString() {
		
		StringBuffer AccelmString = new StringBuffer();
		SQLiteDatabase AccelmDB = this.getWritableDatabase();
		Cursor makeAccelmJSONCursor = null;
		try {
			makeAccelmJSONCursor = AccelmDB.query(ACCELM_TABLE, null, null,
					null, null, null, null, null);

			while (makeAccelmJSONCursor.moveToNext()) {
				//makeAccelmJSONCursor.moveToNext();// moveToNext的使用次数可以控制采样频率的大小
				String label = makeAccelmJSONCursor
				.getString(makeAccelmJSONCursor.getColumnIndex("label"));
				AccelmString.append(label+" ");
				
				String x_axis = makeAccelmJSONCursor
				.getString(makeAccelmJSONCursor
						.getColumnIndex("x_axis"));
				AccelmString.append("x:"+x_axis+" ");
				
				String y_axis = makeAccelmJSONCursor
				.getString(makeAccelmJSONCursor
						.getColumnIndex("y_axis"));
				AccelmString.append("y:"+y_axis+" ");
				
				
				String z_axis = makeAccelmJSONCursor
				.getString(makeAccelmJSONCursor
						.getColumnIndex("z_axis"));
				AccelmString.append("z:"+z_axis+" ");
				
				String time = makeAccelmJSONCursor
						.getString(makeAccelmJSONCursor.getColumnIndex("time"));
				AccelmString.append(time+" ");
				
				
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(Long.parseLong(time));// Get current time
		String date = formatter.format(curDate);
		AccelmString.append(date+"\n\n");
			}
			saveAccelmJSONToFile( AccelmString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			makeAccelmJSONCursor.close();
			AccelmDB.close();
		}
	}
	
	
	
//	/**
//	 * Save Accelerometer information into JSON format.
//	 */
//	public void makeAccelmJSON() {
//		
//		JSONObject AccelmJSON = new JSONObject();
//		SQLiteDatabase AccelmDB = this.getWritableDatabase();
//		Cursor makeAccelmJSONCursor = null;
//		try {
//			makeAccelmJSONCursor = AccelmDB.query(ACCELM_TABLE, null, null,
//					null, null, null, null, null);
//
//			while (makeAccelmJSONCursor.moveToNext()) {
//				makeAccelmJSONCursor.moveToNext();// moveToNext的使用次数可以控制采样频率的大小
//				String label = makeAccelmJSONCursor
//				.getString(makeAccelmJSONCursor.getColumnIndex("label"));
//		AccelmJSON.put("label", label);
//				String time = makeAccelmJSONCursor
//						.getString(makeAccelmJSONCursor.getColumnIndex("time"));
//				AccelmJSON.put("time", time);
//				String x_axis = makeAccelmJSONCursor
//						.getString(makeAccelmJSONCursor
//								.getColumnIndex("x_axis"));
//				AccelmJSON.put("x_axis", x_axis);
//
//				String y_axis = makeAccelmJSONCursor
//						.getString(makeAccelmJSONCursor
//								.getColumnIndex("y_axis"));
//				AccelmJSON.put("y_axis", y_axis);
//				String z_axis = makeAccelmJSONCursor
//						.getString(makeAccelmJSONCursor
//								.getColumnIndex("z_axis"));
//				AccelmJSON.put("z_axis", z_axis);
//				
//				String unit = makeAccelmJSONCursor
//						.getString(makeAccelmJSONCursor.getColumnIndex("unit"));
//				AccelmJSON.put("unit", unit);
//				saveAccelmJSONToFile( AccelmJSON);
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			makeAccelmJSONCursor.close();
//			AccelmDB.close();
//		}
//	}
	
//	/**
//	 * Save Accelerometer information into JSON format.
//	 */
//	public JSONObject  sendAccelmJSONToServer() {
//		JSONObject AccelmJSON = new JSONObject();
//		SQLiteDatabase AccelmDB = this.getWritableDatabase();
//		Cursor makeAccelmJSONCursor = null;
//		try {
//			makeAccelmJSONCursor = AccelmDB.query(ACCELM_TABLE, null, null,
//					null, null, null, null, null);
//
//			//while (makeAccelmJSONCursor.moveToNext()) {
//				makeAccelmJSONCursor.moveToNext();// moveToNext的使用次数可以控制采样频率的大小
//				String time = makeAccelmJSONCursor
//						.getString(makeAccelmJSONCursor.getColumnIndex("time"));
//				AccelmJSON.put("time", time);
//				String x_axis = makeAccelmJSONCursor
//						.getString(makeAccelmJSONCursor
//								.getColumnIndex("x_axis"));
//				AccelmJSON.put("x_axis", x_axis);
//
//				String y_axis = makeAccelmJSONCursor
//						.getString(makeAccelmJSONCursor
//								.getColumnIndex("y_axis"));
//				AccelmJSON.put("y_axis", y_axis);
//				String z_axis = makeAccelmJSONCursor
//						.getString(makeAccelmJSONCursor
//								.getColumnIndex("z_axis"));
//				AccelmJSON.put("z_axis", z_axis);
//				
//				String unit = makeAccelmJSONCursor
//						.getString(makeAccelmJSONCursor.getColumnIndex("unit"));
//				AccelmJSON.put("unit", unit);
//				
//			//}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			makeAccelmJSONCursor.close();
//			AccelmDB.close();
//		}
//		return AccelmJSON;
//	}

	/**
	 * Save the JSON formatted accelerometer info into a .txt file.
	 * 
	 * @param time
	 *            The time when the file was generated(Notice:might need to change to the time of the accelerometer's time stamp!)
	 * @param AccelmJSON
	 *            Every piece of AccelmJSON info in JSON format
	 */
	public void saveAccelmJSONToFile(String AccelmJSON) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date curDate = new Date(System.currentTimeMillis());// Get current time
		String makeFileTime = formatter.format(curDate);
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "AmazonGame" + File.separator + "Accelm"
				+ makeFileTime + ".txt");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// write to SD card
		PrintStream outputStream = null;
		try {
			outputStream = new PrintStream(new FileOutputStream(file));
			outputStream.print(AccelmJSON);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	// // 存储gps信息，用于输出xml
	// public List<String[]> saveGPS() {
	// List<String[]> allGPS = new ArrayList<String[]>();
	//
	// SQLiteDatabase db = this.getWritableDatabase();
	// Cursor c = null;
	//
	// try {
	// c = db.query(GPS_TABLE, null, null, null, null, null, null, null);
	//
	// while (c.moveToNext()) {
	//
	// c.moveToNext();
	// c.moveToNext();// moveToNext的使用次数可以控制采样频率的大小
	//
	// String[] eachGPS = { "", "", "", "", "" };
	// String longitude = c.getString(c.getColumnIndex("longitude"));
	// eachGPS[1] = longitude;
	//
	// String latitude = c.getString(c.getColumnIndex("latitude"));
	// eachGPS[2] = latitude;
	//
	// String time = c.getString(c.getColumnIndex("time"));
	// eachGPS[0] = time;
	//
	// String location = c.getString(c.getColumnIndex("location"));
	// eachGPS[3] = location;
	// ;
	// allGPS.add(eachGPS);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// c.close();
	// db.close();
	// }
	//
	// return allGPS;
	// }

	//
	/**
	 * List GPS record of certain date.
	 * 
	 * @param selectedDate
	 *            User required date.
	 * @return List<String> GPS record of certain date
	 */
	// public List<String> listGPSRecordForSelectedDate(String selectedDate) {
	// List<String> list = new ArrayList<String>();
	//
	// SQLiteDatabase db = this.getWritableDatabase();
	// Cursor c = null;
	//
	// try {
	// c = db.query(GPS_TABLE, null, null, null, null, null, null, null);
	// while (c.moveToNext()) {
	// if (c.getString(c.getColumnIndex("time"))
	// .contains(selectedDate)) {
	// String longitude = c.getString(c
	// .getColumnIndex("longitude"));
	// String latitude = c.getString(c.getColumnIndex("latitude"));
	// String time = c.getString(c.getColumnIndex("time"));
	// String location = c.getString(c.getColumnIndex("location"));
	// list.add("时间:" + time + "\n" + "经度:" + longitude + "\n"
	// + "纬度:" + latitude + "\n" + "参考位置：" + location);
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// c.close();
	// db.close();
	// }
	// return list;
	// }

}
