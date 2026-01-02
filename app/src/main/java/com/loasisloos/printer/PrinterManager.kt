package com.loasisloos.printer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.starmicronics.stario10.InterfaceType
import com.starmicronics.stario10.StarConnectionSettings
import com.starmicronics.stario10.StarPrinter
import com.starmicronics.stario10.StarDeviceDiscoveryManager
import com.starmicronics.stario10.StarDeviceDiscoveryManagerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed class PrinterConnectionState {
    object Disconnected : PrinterConnectionState()
    object Scanning : PrinterConnectionState()
    object Connecting : PrinterConnectionState()
    object Connected : PrinterConnectionState()
    data class Error(val message: String) : PrinterConnectionState()
}

class PrinterManager(private val context: Context) {

    private val _connectionState =
        MutableStateFlow<PrinterConnectionState>(PrinterConnectionState.Disconnected)
    val connectionState: StateFlow<PrinterConnectionState> = _connectionState.asStateFlow()

    private var currentPrinter: StarPrinter? = null

    suspend fun discoverAndConnect() {
        if (!checkPermissions()) {
            _connectionState.value =
                PrinterConnectionState.Error("Permission Bluetooth manquante")
            return
        }

        _connectionState.value = PrinterConnectionState.Scanning

        withContext(Dispatchers.IO) {
            try {
                // 1. Create Discovery Manager
                val manager = StarDeviceDiscoveryManagerFactory.create(
                    listOf(InterfaceType.Bluetooth),
                    context
                )
                manager.discoveryTime = 10000 // 10 seconds timeout

                // 2. Discover via Callback using suspendCancellableCoroutine to bridge to coroutines
                val printer = suspendCancellableCoroutine<StarPrinter?> { cont ->
                    var resumes = false
                    
                    manager.callback = object : StarDeviceDiscoveryManager.Callback {
                        override fun onPrinterFound(printer: StarPrinter) {
                            if (!resumes) {
                                resumes = true
                                manager.stopDiscovery()
                                cont.resume(printer)
                            }
                        }

                        override fun onDiscoveryFinished() {
                            if (!resumes) {
                                resumes = true
                                cont.resume(null) // Not found
                            }
                        }
                    }
                    
                    try {
                        manager.startDiscovery()
                    } catch (e: Exception) {
                        if (!resumes) {
                            resumes = true
                            cont.resume(null)
                        }
                    }
                    
                    // Handle cancellation
                    cont.invokeOnCancellation {
                        try { manager.stopDiscovery() } catch(e: Exception) {}
                    }
                }

                if (printer == null) {
                    throw Exception("Aucune imprimante Star trouvée")
                }

                // 3. Connect using the found identifier
                val identifier = printer.connectionSettings.identifier
                val settings = StarConnectionSettings(
                    interfaceType = InterfaceType.Bluetooth,
                    identifier = identifier
                )

                val newPrinter = StarPrinter(settings, context)

                _connectionState.value = PrinterConnectionState.Connecting
                newPrinter.openAsync().await()

                currentPrinter = newPrinter
                _connectionState.value = PrinterConnectionState.Connected

            } catch (e: Exception) {
                e.printStackTrace()
                _connectionState.value =
                    PrinterConnectionState.Error(e.message ?: "Erreur inconnue")
                currentPrinter = null
            }
        }
    }

    suspend fun print(data: String) {
        val printer = currentPrinter ?: run {
            _connectionState.value =
                PrinterConnectionState.Error("Imprimante non connectée")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                printer.printAsync(data).await()
            } catch (e: Exception) {
                _connectionState.value =
                    PrinterConnectionState.Error("Échec impression: ${e.message}")
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
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
