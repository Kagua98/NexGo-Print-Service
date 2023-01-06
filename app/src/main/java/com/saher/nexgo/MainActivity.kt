package com.saher.nexgo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

import com.nexgo.oaf.apiv3.APIProxy
import com.nexgo.oaf.apiv3.SdkResult
import com.nexgo.oaf.apiv3.device.printer.AlignEnum
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener
import com.nexgo.oaf.apiv3.device.printer.Printer
import com.saher.nexgo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), OnPrintListener {

    var printer: Printer? = null

    val binding: ActivityMainBinding

    get() = DataBindingUtil.setContentView(this, R.layout.activity_main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize the SDK components
        val engine = APIProxy.getDeviceEngine(this)
        printer = engine.printer

        //Initialize the printer
        printer!!.initPrinter()


        binding.button.setOnClickListener {

            printer!!.appendQRcode("Sample QR Code for the PrintSample Application!",384, 7, 3, AlignEnum.CENTER)
            printer!!.appendPrnStr("Laguna Hills, CA", 30, AlignEnum.CENTER, false)

            printer!!.startPrint(true, this)
        }
        when (printer!!.status) {
            SdkResult.Success -> binding.printerStatus.text = "Found Printer"
            SdkResult.Printer_NoDevice -> binding.printerStatus.text = "Didn't find Printer"
            SdkResult.Printer_Fault -> binding.printerStatus.text = "Printer Fault"
            SdkResult.Fail -> binding.printerStatus.text = "Printer Failed"
        }
    }


    override fun onPrintResult(resultCode: Int) {
        runOnUiThread(Runnable {

            var textView: TextView = findViewById(R.id.printerStatus)

            when (resultCode) {
                SdkResult.Success -> textView.text = "Printer job finished successfully!"
                SdkResult.Printer_Print_Fail -> textView.text ="Printer Failed: $resultCode"
                SdkResult.Printer_Busy -> textView.text = "Printer is Busy: $resultCode"
                SdkResult.Printer_PaperLack -> textView.text = "Printer is out of paper: $resultCode"
                SdkResult.Printer_Fault -> textView.text = "Printer fault: $resultCode"
                SdkResult.Printer_TooHot -> textView.text ="Printer temperature is too hot: $resultCode"
                SdkResult.Printer_UnFinished -> textView.text = "Printer job is unfinished: $resultCode"
                SdkResult.Printer_Other_Error -> textView.text = "Printer Other_Error: $resultCode"
                else -> textView.text = "Generic Fail Error: $resultCode"
            }

        })

    }
}