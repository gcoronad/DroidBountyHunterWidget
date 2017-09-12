package droidbountyhunterwidget.example.com.droidbountyhunterwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.CalendarView;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Created by gcoronad on 11/09/2017.
 */

public class FugitivosWidget extends AppWidgetProvider {
    private static final String uri = "content://edu.training.contentproviders";
    public static final Uri CONTENT_URI = Uri.parse(uri);
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_FOTO = "photo";
    public static String capturado = "0";
    public static String CLOCK_WIDGET_UPDATE = "com.example.droidbountyhunterwidget.ACTUALIZAR_SEG_WIDGET";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int i = 0; i < appWidgetIds.length; i++) {
            int widgetId = appWidgetIds[i];

            actualizarWidget(context, appWidgetManager, widgetId);
        }
    }

    private static void actualizarWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        RemoteViews controles = new RemoteViews(context.getPackageName(), R.layout.widget_principal);

        try {
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(CONTENT_URI, null, capturado, null, null);
            String Nombre = "", Path = "";
            if (cur.moveToFirst()) {
                Nombre = cur.getString(cur.getColumnIndex(COLUMN_NAME_NAME));
                Path = cur.getString(cur.getColumnIndex(COLUMN_NAME_FOTO));
            }
            cur.close();

            controles.setTextViewText(R.id.lblFugitivoCapturado, Nombre);

            if (capturado.equals("0")) {
                controles.setTextViewText(R.id.lblFugitivoCapturado, "Fugitivos");
                Bitmap bit_map = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                controles.setImageViewBitmap(R.id.imgFoto, bit_map);
            }
            else if(Path.equals("")){
                controles.setTextViewText(R.id.lblFugitivoCapturado, "Capturados");
                Bitmap bit_map = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                controles.setImageViewBitmap(R.id.imgFoto, bit_map);
            }else{
                controles.setTextViewText(R.id.lblFugitivoCapturado, "Capturados");
                Bitmap bit_map = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                //Bitmap bit_map = PictureTools.decodeSampledBitmapFromUri(Path, 50, 50);
                controles.setImageViewBitmap(R.id.imgFoto, bit_map);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Intent intent = new Intent("com.example.droidbountyhunterwidget.ACTUALIZAR_WIDGET");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        controles.setOnClickPendingIntent(R.id.btnCambiar, pendingIntent);

        appWidgetManager.updateAppWidget(widgetId, controles);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals("com.example.droidbountyhunterwidget.ACTUALIZAR_WIDGET")){
            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);

            if(capturado.equals("0")){
                capturado = "1";
            }else{
                capturado = "0";
            }

            if(widgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
                actualizarWidget(context, widgetManager, widgetId);
            }


        }

        if(CLOCK_WIDGET_UPDATE.equals(intent.getAction())){
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getSimpleName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

            for(int appWidgetID : ids){
                actualizarWidget(context, appWidgetManager, appWidgetID);
            }
        }
    }

    private PendingIntent createClockTickIntent(Context context){
        Intent intent = new Intent(CLOCK_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 15000, createClockTickIntent(context));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
    }
}
