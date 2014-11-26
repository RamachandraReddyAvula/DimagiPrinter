
package com.vl.printerapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
     // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);
        

        // Set job name, which will be displayed in the print queue
        String jobName = this.getString(R.string.app_name);
        
     // Start a print job, passing in a PrintDocumentAdapter implementation
        // to handle the generation of a print document
        printManager.print(jobName, new PdfPrintDocumentAdapter(this), null);



    }
}
