package com.example.tranleduy.aahome;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tranleduy.aahome.adapter.CommandAdapter;
import com.example.tranleduy.aahome.adapter.DeviceAdapter;
import com.example.tranleduy.aahome.adapter.ModeAdapter;
import com.example.tranleduy.aahome.data.Database;
import com.example.tranleduy.aahome.data.Preferences;
import com.example.tranleduy.aahome.fragment.FragmentChart;
import com.example.tranleduy.aahome.items.DeviceItem;
import com.example.tranleduy.aahome.items.MessengeItem;
import com.example.tranleduy.aahome.items.ModeItem;
import com.example.tranleduy.aahome.utils.JsonReader;
import com.example.tranleduy.aahome.utils.Math;
import com.example.tranleduy.aahome.utils.Protocol;
import com.example.tranleduy.aahome.utils.Connector;
import com.example.tranleduy.aahome.utils.Variable;
import com.github.lzyzsd.circleprogress.DonutProgress;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractTheme extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                           Connector.ServerListener,
                           ViewPager.OnPageChangeListener,
                           SharedPreferences.OnSharedPreferenceChangeListener,
                           DeviceAdapter.DeviceListener {
    public static final int ACTIVITY_ADD_DEVICE = 1;
    public static final int ACTIVITY_ADD_MODE = 2;
    protected static final boolean DEBUG_APP = true;
    protected Connector connector = null;
    protected boolean connected = false;
    protected Preferences preferences;
    protected Button btnConnect;
    protected EditText editIp, editPort, editCommand;
    protected SectionsPagerAdapter sectionsPagerAdapter;
    protected ViewPager viewPager;
    protected FrameLayout linearLayout;
    protected RecyclerView rcCommand;
    protected DeviceAdapter deviceAdapter;
    protected CommandAdapter commandAdapter;
    protected FloatingActionButton fab;
    protected DonutProgress circleProgressHumidity;
    protected Handler hander = new Handler();
    protected Database database;
    protected ModeAdapter modeRecycleViewAdapter;
    protected SwipeRefreshLayout refreshLayoutDevice, refreshLayoutDevice_main;
    protected boolean isDev = true;
    protected SwitchCompat swAutoLight, swFromUserLight, swSecurity;
    protected RadioButton radAutoDigital, radAutoAnalog;
    protected CheckBox ckbPir, cbQuickConnect;
    protected SeekBar seekLight;
    protected CardView cardOnOffLight;
    protected TabLayout tabLayout;
    protected ImageView imgSecutiry, imgSecurity_main, imgFire_main, imgDoor_main, imgGarage_main;
    protected TextView swSecurity_main, swFire_main, swDoor_main, swGarage_main, txtLightSensor,
            txtValueAnalog, txtTempC, txtTempF;
    protected SwipeRefreshLayout swipeRefreshLayoutMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initData();
        initView();

    }

    protected void initData() {
        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setting.registerOnSharedPreferenceChangeListener(this);
        isDev = setting.getBoolean(Preferences.KEY_DEV, false);

        preferences = new Preferences(getApplicationContext());

        database = new Database(getApplicationContext());

        connector = new Connector(Connector.IP_DEFAULT, Connector.PORT_DEFAULT);
    }

    protected void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        deviceAdapter = new DeviceAdapter(AbstractTheme.this, new Database(AbstractTheme.this), this);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        modeRecycleViewAdapter = new ModeAdapter(AbstractTheme.this, new Database(AbstractTheme.this));
        commandAdapter = new CommandAdapter(AbstractTheme.this, new Database(AbstractTheme.this));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        assert viewPager != null;
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(7);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);
        setUpTabLayout(tabLayout);

        linearLayout = (FrameLayout) findViewById(R.id.content_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!connected) {
            connector.connect();
        }
        int last_page = preferences.getInt(Preferences.LAST_PAGE);
        viewPager.setCurrentItem(last_page);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (connected) {
            connector.disconnect();
            connected = false;
        }
        preferences.putInt(Preferences.LAST_PAGE, viewPager.getCurrentItem());
    }

    public boolean isEnableWifi() {
        return false;
    }

    public void setTempF(final int temp) {
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= temp && atomicBoolean.get(); i++) {
                    final int finalI = i;
                    hander.post(new Runnable() {
                        @Override
                        public void run() {
                            txtTempF.setText(finalI + "°F");
                        }
                    });
                    SystemClock.sleep(30);
                }
            }
        });
        atomicBoolean.set(true);
        thread.start();
    }

    public void setTempC(final int temp) {
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= temp && atomicBoolean.get(); i++) {
                    final int finalI = i;
                    hander.post(new Runnable() {
                        @Override
                        public void run() {
                            txtTempC.setText(finalI + "°C");
                        }
                    });
                    SystemClock.sleep(30);
                }
            }
        });
        atomicBoolean.set(true);
        thread.start();
    }

    public void setHumidity(final int progress) {
        final AtomicBoolean ab = new AtomicBoolean(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= progress + 10 && ab.get(); i++) {
                    final int finalI = i;
                    hander.post(new Runnable() {
                        @Override
                        public void run() {
                            circleProgressHumidity.setProgress(finalI);
                        }
                    });
                    SystemClock.sleep(20);
                }

                for (int i = progress + 10; i >= progress && ab.get(); i--) {
                    final int finalI = i;
                    hander.post(new Runnable() {
                        @Override
                        public void run() {
                            circleProgressHumidity.setProgress(finalI);
                        }
                    });
                    SystemClock.sleep(20);
                }
            }
        });
        ab.set(true);
        thread.start();
        Log.e("THREAD", "Thread start");
    }

    public void syncPinArduino() {
        final int count = deviceAdapter.getItemCount();
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < count && atomicBoolean.get(); i++) {
                    DeviceItem deviceItem = deviceAdapter.getDeviceItem(i);
                    int pin = deviceItem.getId();
                    if (connected) {
                        connector.sendMessenge(
                                Protocol.GET +
                                        Protocol.GET_STATUS_PIN +
                                        pin
                        );
                    }
                    SystemClock.sleep(200);
                }
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayoutDevice_main.setRefreshing(false);
                        refreshLayoutDevice.setRefreshing(false);
                    }
                });
            }
        });
        atomicBoolean.set(true);
        thread.start();
    }

    public void syncModeArduino() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String cmd = Protocol.GET + Protocol.GET_SECURITY;
                connector.sendMessenge(cmd, commandAdapter, DEBUG_APP, connected);
                SystemClock.sleep(200);

                cmd = Protocol.GET + Protocol.GET_FIRE_ALRAM_SYSTEM;
                connector.sendMessenge(cmd, commandAdapter, DEBUG_APP, connected);
                SystemClock.sleep(200);

                cmd = Protocol.GET + Protocol.GET_AUTO_DOOR;
                connector.sendMessenge(cmd, commandAdapter, DEBUG_APP, connected);
                SystemClock.sleep(200);

                cmd = Protocol.GET + Protocol.GET_AUTO_LIGHT_DIGITAL;
                connector.sendMessenge(cmd, commandAdapter, DEBUG_APP, connected);
                SystemClock.sleep(200);

                cmd = Protocol.GET + Protocol.GET_AUTO_LIGHT_ANALOG;
                connector.sendMessenge(cmd, commandAdapter, DEBUG_APP, connected);
                SystemClock.sleep(200);

                cmd = Protocol.GET + Protocol.GET_AUTO_LIGHT_PIR;
                connector.sendMessenge(cmd, commandAdapter, DEBUG_APP, connected);
                SystemClock.sleep(200);
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayoutMode.setRefreshing(false);
                    }
                });
            }
        });
        thread.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds com.example.tranleduy.aahome.items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//        Snackbar.make(linearLayout, item.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
        int id = item.getItemId();
//      if (id == R.id.nav_setting) {
//            Intent intent = new Intent(this, Settings.class);
//            startActivityForResult(intent, 1);
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void connectStatusChange(boolean status) {
        this.connected = status;
        this.btnConnect.setText(status ? "NGẮT KẾT NỐI" : "KẾT NỐI");
        this.editIp.setEnabled(!status);
        this.editPort.setEnabled(!status);
        sectionsPagerAdapter.notifyDataSetChanged();

        if (status) {
            connector.sendMessenge("CONNECTED");
            viewPager.setCurrentItem(preferences.getInt(Preferences.LAST_PAGE));
            deviceAdapter.addSocket(connector);
            modeRecycleViewAdapter.addSocket(connector);
        } else {
            Snackbar.make(linearLayout, "Chưa kết nối", Snackbar.LENGTH_LONG)
                    .setAction("Thử lại", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            connector.connect();
                        }
                    }).show();
//            viewPager.setCurrentItem(0);
        }
        Log.d("Status", String.valueOf(status));
    }

    @Override
    public void newMessengeFromServer(String messenge) {
        Log.e("RECEIVE", messenge);
//        Snackbar.make(btnConnect, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        String currentDateandTime = sdf.format(new Date());

        commandAdapter.addMessengeItem(new MessengeItem(currentDateandTime, MessengeItem.TYPE_IN, messenge));
        rcCommand.scrollToPosition(commandAdapter.getItemCount() - 1);
        processMessenger(messenge);
    }

    protected void processMessenger(String messenge) {
        try {
            JSONObject data = JsonReader.parseJsonFromText(messenge);
            Log.e("JSON", data.toString());
            if (data.has(Protocol.TEMPERATURE)) {
                int tempC = Integer.parseInt(data.getString(Protocol.TEMPERATURE));
                setTempC(tempC);
                Log.e("TEMP ", data.getString(Protocol.TEMPERATURE));

                int tempF = new Math().convertToF(String.valueOf(tempC));
                setTempF(tempF);

                preferences.putInt(Preferences.TEMP_LAST, tempC);

            }
            if (data.has(Protocol.HUMIDITY)) {
                int progress = Integer.parseInt(data.getString(Protocol.HUMIDITY));
                Log.e("HUMI", data.getString(Protocol.HUMIDITY));

                setHumidity(progress);
                preferences.putInt(Preferences.HUMI_LAST, progress);
            }
            if (data.has(Protocol.PIN)) {
                int pin = data.getInt(Protocol.PIN);
                boolean isOn = data.getBoolean(Protocol.VALUE_PIN);
                deviceAdapter.setStatusPin(pin, isOn);
            }
            if (data.has(Protocol.VALUE_LIGHT_SENSOR)) {
                txtLightSensor.setText(data.getString(Protocol.VALUE_PIN));
                preferences.putInt(Preferences.VALUE_LIGHT_SENSOR_LAST, data.getInt(Protocol.VALUE_PIN));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        switch (position) {
            case 0:
                fab.show();
                fab.setImageResource(R.drawable.ic_wifi_white_24dp);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connect();
                    }
                });
                break;
            case 1:
                fab.hide();
                break;
            case 2:
                fab.show();
                fab.setImageResource(R.drawable.ic_add);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AbstractTheme.this, DeviceActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 0);
                        intent.putExtra("com/example/tranleduy/aahome/data", bundle);
                        startActivityForResult(intent, Variable.ACTIVITY_DEVICE);
                    }
                });
                break;
            case 3:
                fab.show();
                fab.setImageResource(R.drawable.ic_reload_white);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!swipeRefreshLayoutMode.isRefreshing()) {
                            swipeRefreshLayoutMode.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayoutMode.setRefreshing(true);
                                }
                            });
                            syncModeArduino();
                        }
                    }
                });
                break;
            case 4:
                fab.hide();
                break;
            default:
                fab.hide();
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void connect() {
        if (!connected) {
            try {
                String ip = editIp.getText().toString();
                int port = Integer.valueOf(editPort.getText().toString());
                connector = new Connector(ip, port);
                connector.setServerListener(AbstractTheme.this);
                connector.connect();
                btnConnect.setText(R.string.connecting);
                preferences.putInt(Preferences.PORT, port);
                preferences.putString(Preferences.IP_ADDRESS, ip);
            } catch (Exception e) {
                Snackbar.make(linearLayout, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        } else {
            connector.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Variable.ACTIVITY_DEVICE) {
            if (resultCode == Variable.ACTIVITY_ADD_DEVICE) {
                Bundle bundle = data.getBundleExtra("com/example/tranleduy/aahome/data");
                DeviceItem deviceItem = (DeviceItem) bundle.getSerializable(Preferences.DEVICE);
                if (deviceItem != null) {
                    long res = database.addDevice(deviceItem);
                    Log.e("DATABASE", res + "");
                    if (res > 0) {
                        deviceAdapter.addDevice(deviceItem);
                    } else {
                        showDialog("Đã tồn tại thiết bị");
                    }
                }
            } else if (resultCode == Variable.ACTIVITY_CHANGE_INFO_DEVICE) {
//                Log.d(AddDeviceActivity.class.getName(), "STEP 2");
                Bundle bundle = data.getBundleExtra("com/example/tranleduy/aahome/data");
                DeviceItem deviceItem = (DeviceItem) bundle.getSerializable(Preferences.DEVICE);
                if (deviceItem != null) {
                    long res = database.updateDevice(deviceItem);
                    Log.e("DATABASE UPDATE", res + "");
                    if (res > 0) {
                        deviceAdapter.updateDevice(deviceItem);
                    } else {
                        showDialog("Chỉnh sửa thất bại");
                    }
                }
            }
        } else if (requestCode == Variable.ACTIVITY_ADD_MODE) {
            if (resultCode == Variable.ACTIVITY_ADD_MODE) {
                Bundle bundle = data.getBundleExtra("com/example/tranleduy/aahome/data");
                ModeItem modeItem = (ModeItem) bundle.getSerializable(Preferences.MODE);
                assert modeItem != null;
                Log.e("ACTIVITY RESULT", modeItem.toString());
                long res = database.addMode(modeItem);
                Log.e("DATABASE", res + "");
                if (res > 0) {
                    modeRecycleViewAdapter.addMode(modeItem);
                } else {
                    showDialog("Đã tồn tại ID");
                }
            }
        }
    }

    protected void showDialog(String s) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(AbstractTheme.this);
        aBuilder.setMessage(s);
        aBuilder.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        aBuilder.create().show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Preferences.KEY_DEV)) {
            boolean value = sharedPreferences.getBoolean(Preferences.KEY_DEV, false);
            if (value != isDev) {
                this.isDev = value;
                sectionsPagerAdapter.notifyDataSetChanged();
            }
            Toast.makeText(AbstractTheme.this, String.valueOf(value), Toast.LENGTH_SHORT).show();
        }
    }

    protected void setUpTabLayout(TabLayout tabLayout) {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_wifi_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_devices_other_black_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_chrome_reader_mode_black_24dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_library_music_black_24dp);
        tabLayout.getTabAt(5).setIcon(R.drawable.ic_show_chart_black_24dp);
        if (isDev) {
            tabLayout.getTabAt(6).setIcon(R.drawable.ic_developer_mode_black_24dp);
        }
    }

    protected void offAutoLight(final boolean analog, final boolean digital, final boolean pir) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = Protocol.POST + Protocol.AUTO_LIGHT_ANALOG + (analog ? "1" : "0");
                connector.sendMessenge(msg, commandAdapter);
                SystemClock.sleep(100);

                msg = Protocol.POST + Protocol.AUTO_LIGHT_DIGITAL + (digital ? "1" : "0");
                connector.sendMessenge(msg, commandAdapter);
                SystemClock.sleep(100);

                msg = Protocol.POST + Protocol.AUTO_LIGHT_PIR + (pir ? "1" : "0");
                connector.sendMessenge(msg, commandAdapter);
                SystemClock.sleep(100);
            }
        });
        thread.start();
    }

    protected void showSnackNotConnect() {
        Snackbar.make(linearLayout, "Không có kết nối", Snackbar.LENGTH_LONG).setAction("Kết nối", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
//                socket.connect();
            }
        }).show();
    }

    @Override
    public void deviceDetail(int pos, DeviceItem deviceItem) {
        String info = "Chân arduino: " + deviceItem.getId() + "\n" +
                              "Tên: " + deviceItem.getName() + "\n" +
                              "Thông tin: " + deviceItem.getDes() + "\n" +
                              "Command: " + deviceItem.getCommand() + "\n" +
                              "Dạng thiết bị: " + "null" + "\n" +
                              "Kiểu: " + ((deviceItem.getType() == 1) ? "Hệ thống" : "Người dùng");
        showDialog(info);
    }

    @Override
    public void deviceChangeInfo(int pos, DeviceItem deviceItem) {
        Intent intent = new Intent(AbstractTheme.this, DeviceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putSerializable("item", deviceItem);
        intent.putExtra("com/example/tranleduy/aahome/data", bundle);
        startActivityForResult(intent, Variable.ACTIVITY_DEVICE);
    }

    @Override
    public void deviceDelete(final int pos, final DeviceItem deviceItem) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(AbstractTheme.this);
        aBuilder.setMessage("Bạn có muốn xóa thiết bị?");
        aBuilder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        aBuilder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long res = database.removeDevice(deviceItem);
                if (res > 0) {
                    deviceAdapter.removeDevice(deviceItem, pos);
                } else {
                    showDialog("Xóa thất bại");
                }
            }
        });
        aBuilder.create().show();

    }

    public interface ChartListener {
        void updateChart();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 5)//chart
                return new FragmentChart(AbstractTheme.this);
            return new PlaceHolderFragment(position);
        }

        @Override
        public int getCount() {
//            return connected ? 4 : 1;
            return isDev ? 7 : 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "KẾT NỐI";
                case 1:
                    return "TỔNG QUAN";
                case 2:
                    return "THIẾT BỊ";
                case 3:
                    return "CHẾ ĐỘ";
                case 4:
                    return "ÂM NHẠC";
                case 5:
                    return "THỐNG KÊ";
                case 6:
                    return "COMMAND";
            }
            return null;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            setUpTabLayout(tabLayout);
        }
    }

    public class PlaceHolderFragment extends Fragment {
        protected int sectionNumber;

        public PlaceHolderFragment(int sectionNumber) {
            this.sectionNumber = sectionNumber;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
            switch (sectionNumber) {
                case 0:
                    final View view1 = inflater.inflate(R.layout.layout_connect, container, false);
                    btnConnect = (Button) view1.findViewById(R.id.btnConnect);
                    editIp = (EditText) view1.findViewById(R.id.txtIpAddress);
                    editPort = (EditText) view1.findViewById(R.id.txtPort);

                    if (preferences.getString(Preferences.IP_ADDRESS) + "" != Preferences.NULL_STRING) {
                        editIp.setText(preferences.getString(Preferences.IP_ADDRESS));
                    }
                    if (preferences.getInt(Preferences.PORT) != Preferences.NULL_INT) {
                        editPort.setText(preferences.getInt(Preferences.PORT) + "");
                    }
                    if (connected && connector != null) {
                        connectStatusChange(true);
                    }
                    btnConnect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            connect();
                        }
                    });

                    cbQuickConnect = (CheckBox) view1.findViewById(R.id.cb_quick_connect);
                    cbQuickConnect.setChecked(preferences.getBoolean(Preferences.QUICK_CONNECT));
                    cbQuickConnect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean b = cbQuickConnect.isChecked();
                            preferences.putBoolean(Preferences.QUICK_CONNECT, b);
                        }
                    });

                    if (preferences.getBoolean(Preferences.QUICK_CONNECT)) {
                        String ip = preferences.getString(Preferences.IP_ADDRESS);
                        int port = preferences.getInt(Preferences.PORT);
                        connector = new Connector(ip, port);
                        connector.setServerListener(AbstractTheme.this);
                        connector.connect();
                        btnConnect.setText(R.string.connecting);
                    }
                    return view1;
                case 1: //main
                    View view2 = inflater.inflate(R.layout.fragment_main, container, false);
                    txtTempC = (TextView) view2.findViewById(R.id.txtTempC);
                    txtTempF = (TextView) view2.findViewById(R.id.txtTempF);
                    circleProgressHumidity = (DonutProgress) view2.findViewById(R.id.cpHumidity);

                    ImageView imgReload = (ImageView) view2.findViewById(R.id.imgReload);
                    imgReload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final Animation animation = AnimationUtils.loadAnimation(AbstractTheme.this, R.anim.img_rotate);
                            v.post(new Runnable() {
                                @Override
                                public void run() {
                                    v.startAnimation(animation);
                                }
                            });
                            if (connected) {
                                connector.sendMessenge(Protocol.GET +
                                                            Protocol.GET_TEMP_HUMI, commandAdapter);
                            }
                        }
                    });

                    try {
                        int temp = preferences.getInt(Preferences.TEMP_LAST);
                        int tempF = new Math().convertToF(String.valueOf(temp));
                        int progressHumi = preferences.getInt(Preferences.HUMI_LAST);

                        setTempC(temp);
                        setTempF(tempF);
                        setHumidity(progressHumi);

                    } catch (Exception e) {
                        Snackbar.make(linearLayout, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                    RecyclerView rcDeviceOn = (RecyclerView) view2.findViewById(R.id.rc_device_on);
                    rcDeviceOn.setHasFixedSize(true);
                    rcDeviceOn.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rcDeviceOn.setAdapter(deviceAdapter);

//                    RecyclerView rcMode_main = (RecyclerView) view2.findViewById(R.id.rc_mode_main);
//                    rcMode_main.setHasFixedSize(true);
//                    rcMode_main.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                    rcMode_main.setAdapter(modeRecycleViewAdapter);

                    refreshLayoutDevice_main = (SwipeRefreshLayout)
                                                       view2.findViewById(R.id.sr_device);
                    refreshLayoutDevice_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            syncPinArduino();
                        }
                    });

                    txtLightSensor = (TextView) view2.findViewById(R.id.txtLightSensor);
                    txtLightSensor.setText(preferences.getInt(Preferences.VALUE_LIGHT_SENSOR_LAST) + "");

                    final boolean isSecurity = preferences.getBoolean(Preferences.IS_SECURITY_ON);
                    imgSecurity_main = (ImageView) view2.findViewById(R.id.imgSecurity);
                    imgSecurity_main.setImageResource(isSecurity ? R.drawable.ic_security_blue : R.drawable.ic_security_red);
//                    swSecurity_main = (TextView) view2.findViewById(R.id.txtSecurity);
//                    swSecurity_/main.setText(isSecurity ? "Đang bật" : "Đã tắt");
                    LinearLayout llSecurity = (LinearLayout) view2.findViewById(R.id.llSecurity_main);
                    llSecurity.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewPager.setCurrentItem(3);
                        }
                    });

                    boolean isFireOn = preferences.getBoolean(Preferences.IS_FIRE_ON);
                    imgFire_main = (ImageView) view2.findViewById(R.id.imgFire);
                    imgFire_main.setImageResource(isFireOn ? R.drawable.ic_fire_blue : R.drawable.ic_fire_red);
//                    swFire_main = (TextView) view2.findViewById(R.id.txtFire);
//                    swFire_main.setText(isFireOn ? "Đang bật" : "Đã tắt");
                    LinearLayout llFire = (LinearLayout) view2.findViewById(R.id.llFire_main);
                    llFire.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewPager.setCurrentItem(3);
                        }
                    });

                    boolean isDoorOpen = preferences.getBoolean(Preferences.IS_OPEN_DOOR);
                    imgDoor_main = (ImageView) view2.findViewById(R.id.imgDoor);
//                    swDoor_main = (TextView) view2.findViewById(R.id.txtDoor);
//                    swDoor_main.setText("Tự động: " + (isDoorOpen ? "bật" : "tắt"));
                    LinearLayout llDoor = (LinearLayout) view2.findViewById(R.id.llDoor_main);
                    llDoor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if (!connected) {
//                                showSnackNotConnect();
//                            } else {
                            Intent intent = new Intent(AbstractTheme.this, DoorControlActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("ip", "192.168.4.1");
                            bundle.putInt("port", 80);
                            intent.putExtra("com/example/tranleduy/aahome/data", bundle);
                            startActivity(intent);
//                            }
                        }
                    });

                    LinearLayout llGarage = (LinearLayout) view2.findViewById(R.id.llGarage_main);
                    llGarage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if (!connected) {
//                                showSnackNotConnect();
//                            } else {
                            Intent intent = new Intent(AbstractTheme.this, GarageControlActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("ip", "192.168.4.1");
                            bundle.putInt("port", 80);
                            intent.putExtra("com/example/tranleduy/aahome/data", bundle);
                            startActivity(intent);
//                            }
                        }
                    });


//                    swSecurity_main = (SwitchCompat) view2.findViewById(R.id.swSecurity);
//                    swSecurity_main.setChecked(isSecurity);
//                    swAutoLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                            imgSecurity_main.setImageResource(isChecked ? R.drawable.ic_security_blue : R.drawable.ic_security_red);
//                            swSecurity.setChecked(isSecurity);
//                            if (connected) {
//                                String cmd = Protocol.POST + Protocol.SET_SECURITY +
//                                        (isChecked ? "1" : "0");
//                                socket.sendMessenge(cmd, commandAdapter);
//                                preferences.putBoolean(Preferences.IS_SECURITY_ON, isChecked);
//                            } else {
//                                showSnackNotConnect();
//                            }
//                        }
//                    });
                    return view2;


                case 2: //device
                    View view4 = inflater.inflate(R.layout.layout_device, container, false);
                    RecyclerView rcAllDevice = (RecyclerView) view4.findViewById(R.id.rc_device);
                    rcAllDevice.setHasFixedSize(true);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(AbstractTheme.this, 2);
                    rcAllDevice.setLayoutManager(gridLayoutManager);
                    rcAllDevice.setAdapter(deviceAdapter);

                    refreshLayoutDevice = (SwipeRefreshLayout) view4.findViewById(R.id.sr_device);
                    refreshLayoutDevice.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            syncPinArduino();
                        }
                    });

                    syncPinArduino();
                    return view4;
                case 3: //mode
                    final View v = inflater.inflate(R.layout.layout_mode, container, false);
                    swipeRefreshLayoutMode = (SwipeRefreshLayout) v.findViewById(R.id.srMode);
                    swipeRefreshLayoutMode.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            syncModeArduino();
                        }
                    });
                    swAutoLight = (SwitchCompat) v.findViewById(R.id.swAutoLight);
                    swFromUserLight = (SwitchCompat) v.findViewById(R.id.swFromUser);
                    swFromUserLight.setChecked(!preferences.getBoolean(Preferences.IS_AUTO_LIGHT));

                    radAutoDigital = (RadioButton) v.findViewById(R.id.radAutoDigital);
                    radAutoDigital.setChecked(preferences.getBoolean(Preferences.IS_AUTO_LIGHT_DIGITAL));

                    radAutoAnalog = (RadioButton) v.findViewById(R.id.radAutoAnalog);
                    radAutoAnalog.setChecked(preferences.getBoolean(Preferences.IS_AUTO_LIGHT_ANALOG));

                    ckbPir = (CheckBox) v.findViewById(R.id.ckbAutoPIR);
                    ckbPir.setChecked(preferences.getBoolean(Preferences.IS_AUTO_LIGHT_PIR));

                    seekLight = (SeekBar) v.findViewById(R.id.seekLight);
                    seekLight.setProgress(preferences.getInt(Preferences.VALUE_LIGHT_ANALOG));

                    cardOnOffLight = (CardView) v.findViewById(R.id.cardOnOff);
                    txtValueAnalog = (TextView) v.findViewById(R.id.txtValueAnalog);
                    txtValueAnalog.setText(preferences.getInt(Preferences.VALUE_LIGHT_ANALOG) + "");

                    swAutoLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            radAutoAnalog.setEnabled(isChecked);
                            radAutoDigital.setEnabled(isChecked);
                            ckbPir.setEnabled(isChecked);
                            swFromUserLight.setChecked(!isChecked);


                        }
                    });
                    swAutoLight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (connected) {
                                boolean isChecked = swAutoLight.isChecked();
                                if (!isChecked) {
                                    offAutoLight(false, false, false);
                                } else {
                                    offAutoLight(radAutoAnalog.isChecked(), radAutoDigital.isChecked(), ckbPir.isChecked());
                                }
                                preferences.putBoolean(Preferences.IS_AUTO_LIGHT, isChecked);
                            } else {
                                showSnackNotConnect();
                            }
                        }
                    });
                    swAutoLight.setChecked(preferences.getBoolean(Preferences.IS_AUTO_LIGHT));

                    swFromUserLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            cardOnOffLight.setEnabled(isChecked);
                            seekLight.setEnabled(isChecked);
                            swAutoLight.setChecked(!isChecked);
                        }
                    });
                    swFromUserLight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (connected) {
                                boolean isChecked = swFromUserLight.isChecked();
                                int value = seekLight.getProgress();
                                String msg = Protocol.POST + Protocol.SET_VALUE_AUTO_LIGHT_ANALOG + value;
                                connector.sendMessenge(msg, commandAdapter);
                                preferences.putBoolean(Preferences.IS_AUTO_LIGHT, !isChecked);
                            } else {
                                showSnackNotConnect();
                            }
                        }
                    });

                    radAutoDigital.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isChecked = radAutoDigital.isChecked();
                            if (connected) {

                                String msg = Protocol.POST + Protocol.SET_AUTO_LIGHT_DIGITAL + (isChecked ? "1" : "0");
                                connector.sendMessenge(msg, commandAdapter);
                                preferences.putBoolean(Preferences.IS_AUTO_LIGHT_DIGITAL, isChecked);

                            } else {
                                showSnackNotConnect();
                            }
                            radAutoAnalog.setChecked(!isChecked);
                        }
                    });

                    radAutoAnalog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isChecked = radAutoAnalog.isChecked();
                            if (connected) {

                                String msg = Protocol.POST + Protocol.SET_AUTO_LIGHT_ANALOG + (isChecked ? "1" : "0");
                                connector.sendMessenge(msg, commandAdapter);
                                preferences.putBoolean(Preferences.IS_AUTO_LIGHT_ANALOG, isChecked);

                            } else {
                                showSnackNotConnect();
                            }
                            radAutoDigital.setChecked(!isChecked);
                        }
                    });

                    ckbPir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (connected) {
                                boolean isChecked = ckbPir.isChecked();
                                String msg = Protocol.POST + Protocol.SET_AUTO_LIGHT_PIR + (isChecked ? "1" : "0");
                                connector.sendMessenge(msg, commandAdapter);
                                preferences.putBoolean(Preferences.IS_AUTO_LIGHT_PIR, isChecked);
                            } else {
                                showSnackNotConnect();
                            }
                        }
                    });

                    seekLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            txtValueAnalog.setText("" + progress + "/255");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            if (connected) {
                                int value = seekLight.getProgress();
                                String msg = Protocol.POST + Protocol.SET_VALUE_AUTO_LIGHT_ANALOG + value;
                                connector.sendMessenge(msg, commandAdapter);
                                preferences.putInt(Preferences.VALUE_LIGHT_ANALOG, value);
                            } else {
                                showSnackNotConnect();
                            }
                        }
                    });

                    cardOnOffLight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewPager.setCurrentItem(2);
                        }
                    });

                    imgSecutiry = (ImageView) v.findViewById(R.id.imgSecurity);
                    imgSecutiry.setImageResource((preferences.getBoolean(Preferences.IS_SECURITY_ON) ?
                                                          R.drawable.ic_security_blue : R.drawable.ic_security_red));
                    swSecurity = (SwitchCompat) v.findViewById(R.id.ckbSecurity);
                    swSecurity.setChecked(preferences.getBoolean(Preferences.IS_SECURITY_ON));
                    swSecurity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                imgSecutiry.setImageResource(R.drawable.ic_security_blue);
                            } else {
                                imgSecutiry.setImageResource(R.drawable.ic_security_red);
                            }
                        }
                    });
                    swSecurity.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (connected) {
                                boolean isChecked = swSecurity.isChecked();
                                String msg = Protocol.POST + Protocol.SET_SECURITY + (isChecked ? "1" : "0");
                                connector.sendMessenge(msg, commandAdapter);
                                preferences.putBoolean(Preferences.IS_SECURITY_ON, isChecked);

                            } else {
                                showSnackNotConnect();
                            }
                        }
                    });
                    return v;
                case 5:

                case 6: //command
                    View view5 = inflater.inflate(R.layout.fragment_command, container, false);
                    rcCommand = (RecyclerView) view5.findViewById(R.id.rc_conversation);
                    rcCommand.setHasFixedSize(true);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setStackFromEnd(true);

                    rcCommand.setLayoutManager(linearLayoutManager);
                    rcCommand.setAdapter(commandAdapter);

                    Button btnSend = (Button) view5.findViewById(R.id.bt_send_messenge);
                    editCommand = (EditText) view5.findViewById(R.id.ed_input_messenge);
                    commandAdapter.setEditTextCommand(editCommand);
                    btnSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String command = editCommand.getText().toString();
                            if (connector != null && connected) {
                                connector.sendMessenge(command, commandAdapter);
                                editCommand.getText().clear();
                                rcCommand.scrollToPosition(commandAdapter.getItemCount() - 1);
                            } else {
//                                connectStatusChange(false);
                                showSnackNotConnect();
                            }
                        }
                    });
                    return view5;
            }
            return null;
        }
    }

}
