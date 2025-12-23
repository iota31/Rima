package com.loasisloos.printer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.starmicronics.stario10.StarPrinter
import com.starmicronics.stario10.StarConnectionSettings
import com.starmicronics.stario10.InterfaceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class PrinterConnectionState {
    object Disconnected : PrinterConnectionState()
    object Scanning : PrinterConnectionState()
    object Connecting : PrinterConnectionState()
    object Connected : PrinterConnectionState()
    data class Error(val message: String) : PrinterConnectionState()
}

class PrinterManager(private val context: Context) {

    private val _connectionState = MutableStateFlow<PrinterConnectionState>(PrinterConnectionState.Disconnected)
    val connectionState: StateFlow<PrinterConnectionState> = _connectionState.asStateFlow()

    private var currentPrinter: StarPrinter? = null

    suspend fun discoverAndConnect() {
        if (!checkPermissions()) {
            _connectionState.value = PrinterConnectionState.Error("Permissions manquantes")
            return
        }

        _connectionState.value = PrinterConnectionState.Scanning

        withContext(Dispatchers.IO) {
            try {
                val settings = StarConnectionSettings(
                    interfaceType = InterfaceType.Bluetooth,
                    identifier = "00:00:00:00:00:00" 
                )
                
                val printer = StarPrinter(settings, context)
                
                _connectionState.value = PrinterConnectionState.Connecting
                
                // StarIO10 in Kotlin uses Deferred
                printer.openAsync().await()
                
                currentPrinter = printer
                _connectionState.value = PrinterConnectionState.Connected
                
            } catch (e: Exception) {
                e.printStackTrace()
                _connectionState.value = PrinterConnectionState.Error("Erreur: ${e.message}")
                currentPrinter = null
            }
        }
    }

    suspend fun print(commands: String) {
        if (currentPrinter == null) {
            _connectionState.value = PrinterConnectionState.Error("Imprimante non connectée")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                // printAsync
                currentPrinter?.printAsync(commands)?.await()
            } catch (e: Exception) {
                _connectionState.value = PrinterConnectionState.Error("Echec impression: ${e.message}")
            }
        }
    }

    fun disconnect() {
        try {
            currentPrinter?.closeAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            currentPrinter = null
            _connectionState.value = PrinterConnectionState.Disconnected
        }
    }

    private fun checkPermissions(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                   ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }
}
