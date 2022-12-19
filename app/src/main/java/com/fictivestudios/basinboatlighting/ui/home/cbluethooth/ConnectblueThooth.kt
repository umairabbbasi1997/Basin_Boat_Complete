package com.fictivestudios.basinboatlighting.ui.home.cbluethooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.activities.HomeActivity.Companion.navController
import com.fictivestudios.basinboatlighting.adapter.BlueThoothAdapter
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.ConnectbluethoothBinding
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.getmefit.uitilites.Constants.Companion.BLUETHOOTHGATT
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import java.util.*


class ConnectblueThooth : BaseFragment(), BlueThoothAdapter.ItemClickListener {

    private lateinit var connectbluethoothBinding: ConnectbluethoothBinding


    override fun setTitlebar(titlebar: Titlebar) {
    }

    var devicesList = ArrayList<BluetoothDevice>()


    var mBluetoothDevice: BluetoothDevice? = null

    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getActivityContext?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var mScanning: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        connectbluethoothBinding =
            DataBindingUtil.inflate(inflater, R.layout.connectbluethooth, container, false)

        PreferenceUtils.saveBoolean("lb", false)
        PreferenceUtils.saveBoolean("fb", false)
        PreferenceUtils.saveBoolean("nb", false)


        getActivityContext?.hideButtonHome()
        getActivityContext?.hideBttomBar()



        init()

        connectbluethoothBinding.btonoff.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                PreferenceUtils.saveBoolean("btONOFF", true)

                connectbluethoothBinding.switchonoff.text = "ON"

                setDeviceBluetoothDiscoverable()
                allowLocationDetectionPermissions()

                if (bluetoothAdapter.isEnabled) {
                    scanLeDevice(true)
                }

            } else {

                connectbluethoothBinding.switchonoff.text = "OFF"
                bluetoothAdapter.isEnabled == false
                scanLeDevice(false)
                if (!devicesList.isNullOrEmpty()) {
                    devicesList.clear()
                    connectbluethoothBinding.btdevList.adapter?.notifyDataSetChanged()
                } else {

                }


            }

        }






        return connectbluethoothBinding.root


    }


    fun init() {

        if (PreferenceUtils.getBoolean("btONOFF")) {

            connectbluethoothBinding.btonoff.isChecked = true


            setDeviceBluetoothDiscoverable()
            allowLocationDetectionPermissions()

            if (bluetoothAdapter.isEnabled) {
                scanLeDevice(true)
            }
        } else {
            connectbluethoothBinding.switchonoff.text = "OFF"
            connectbluethoothBinding.btonoff.isChecked = false
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FINE_LOCATION_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    scanLeDevice(true)
                } else {

                }
                return
            }
        }
    }


    private fun allowLocationDetectionPermissions() {
        if (ContextCompat.checkSelfPermission(
                getActivityContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                getActivityContext!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_PERMISSION_REQUEST
            )
        }

    }

    companion object {
        private const val FINE_LOCATION_PERMISSION_REQUEST = 1001
    }


    @SuppressLint("MissingPermission")
    private fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                // Stops scanning after a pre-defined scan period.
                Handler().postDelayed({
                    mScanning = false
                    bluetoothAdapter.bluetoothLeScanner?.stopScan(mLeScanCallback)



                    if (!devicesList.isNullOrEmpty()) {
                        val dummyList = ArrayList<BluetoothDevice>(devicesList)


                        for (item in dummyList) {

                            if (item.name.isNullOrEmpty()) {
                                devicesList.remove(item)
                            }

                        }


                        val adapter = BlueThoothAdapter(getActivityContext!!, devicesList, this)
                        connectbluethoothBinding.btdevList.apply {

                            layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL, false
                            )
                            connectbluethoothBinding.btdevList.adapter = adapter
                            connectbluethoothBinding.btdevList.adapter!!.notifyDataSetChanged()
                        }

                        //  connectbluethoothBinding.btdevList.adapter = adapter
                        /// adapter.notifyDataSetChanged()

                    }


                }, 4000)
                mScanning = true
                bluetoothAdapter.bluetoothLeScanner?.startScan(mLeScanCallback)

            }
            else -> {
                mScanning = false
                bluetoothAdapter.bluetoothLeScanner?.stopScan(mLeScanCallback)
            }
        }
    }


    private var mLeScanCallback: ScanCallback =
        object : ScanCallback() {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)


                result?.device?.let { devicesList.add(it) }
                //   showToast(devicesList.toString(),getActivityContext)

            }

            override fun onBatchScanResults(results: List<ScanResult?>?) {
                super.onBatchScanResults(results)


                Log.w("mylog", "device")


            }


        }

    private val gattCallback = object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w("mylog", "Successfully connected to $deviceAddress")




                    Handler(Looper.getMainLooper()).post {
                        getActivityContext?.runOnUiThread {
                            showToast("Connected Successfully", getActivityContext)
                        }

                        val ans: Boolean = gatt.discoverServices()


                        BLUETHOOTHGATT = gatt

                        try {
                            val action =
                                ConnectblueThoothDirections.actionConnectblueThoothToHomeFragment()
                            navController.navigate(action)
                            //findNavController().navigate(action)
                        } catch (e: Exception) {

                        }






                        Log.d("mylog", "Discover Services started: $ans")
                    }


                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("mylog", "Successfully disconnected from $deviceAddress")
                    gatt.close()
                }
            } else {
                Log.w("mylog", "Error $status encountered for $deviceAddress! Disconnecting...")
                Toast.makeText(
                    getActivityContext,
                    "Error $status encountered for $deviceAddress! Disconnecting...",
                    Toast.LENGTH_SHORT
                ).show()
                gatt.close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (gattService in gatt!!.services) {
                    Log.i(
                        "mylog",
                        "Service UUID Found: " + gattService.uuid.toString() + "name: " + gattService.type.toString()
                    )
                }

            }


        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }


    }


    private fun setDeviceBluetoothDiscoverable() {
        //no need to request bluetooth permission if  discoverability is requested
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(
            BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
            0
        )// 0 to keep it always discoverable
        startActivity(discoverableIntent)
    }

    @SuppressLint("MissingPermission")
    private fun connectwithDevice(deviceAddress: BluetoothDevice) {
        with(deviceAddress) {
            android.util.Log.w("mylog", "Connecting to $deviceAddress")
            Toast.makeText(getActivityContext, "Connecting to $deviceAddress", Toast.LENGTH_SHORT)
                .show()
            this.connectGatt(getActivityContext, false, gattCallback)
            //navController.navigate(R.id.homeFragment)


        }
    }

    var serviceUUIDsList: List<UUID> = ArrayList()
    var characteristicUUIDsList: List<UUID> = ArrayList()
    var descriptorUUIDsList: List<UUID> = ArrayList()

    @SuppressLint("MissingPermission")
    private fun initScanning(bleScanner: BluetoothLeScanner) {

        bleScanner.startScan(getScanCallback())
    }

    private fun getScanCallback(): ScanCallback? {
        return object : ScanCallback() {
            override fun onScanResult(callbackType: Int, scanResult: ScanResult) {
                super.onScanResult(callbackType, scanResult)

                serviceUUIDsList = getServiceUUIDsList(scanResult)
                Log.i("mylog", "Service UUID Found: " + serviceUUIDsList.toString())

            }
        }
    }

    private fun getServiceUUIDsList(scanResult: ScanResult): List<UUID> {
        val parcelUuids = scanResult.scanRecord!!.serviceUuids
        val serviceList: MutableList<UUID> = ArrayList()
        for (i in parcelUuids.indices) {
            val serviceUUID = parcelUuids[i].uuid
            if (!serviceList.contains(serviceUUID)) serviceList.add(serviceUUID)
        }
        return serviceList
    }

    override fun onItemClick(deviceAddress: BluetoothDevice) {
        mBluetoothDevice = deviceAddress
        connectwithDevice(deviceAddress)
    }


}