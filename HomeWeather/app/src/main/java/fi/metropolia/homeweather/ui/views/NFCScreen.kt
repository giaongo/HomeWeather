package fi.metropolia.homeweather.ui.views

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun NFCScreen() {
    var inputText by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)
    if (nfcAdapter == null) {
        Toast.makeText(context, "NFC is not available", Toast.LENGTH_LONG).show()
        return
    }


    val callback = NfcAdapter.ReaderCallback { tag ->
        val ndef = Ndef.get(tag)
        tag?.let {
            val tagId = it.id
            val tagIdHex = tagId.joinToString("") { byte -> "%02x".format(byte) }
            Toast.makeText(context, tagIdHex, Toast.LENGTH_LONG).show()

            val record = NdefRecord.createUri("https://www.metropolia.fi")
            val ndefMessage = NdefMessage(record)

            try {
                ndef.connect()
                ndef.writeNdefMessage(ndefMessage)
            } catch (e: Exception) {
                Log.e("NFC", e.toString())

            } finally {
                ndef.close()
            }
        }
    }

    val options = Bundle()
    options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 1000)
    nfcAdapter.enableReaderMode(context as MainActivity, callback,NfcAdapter.FLAG_READER_NFC_A or
            NfcAdapter.FLAG_READER_NFC_B or
            NfcAdapter.FLAG_READER_NFC_F or
            NfcAdapter.FLAG_READER_NFC_V, options)



    Column {
        Text(text = "NFC screen")
        TextField(value = inputText, onValueChange = {
            inputText = it
        })
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Submit")

        }
    }
}

