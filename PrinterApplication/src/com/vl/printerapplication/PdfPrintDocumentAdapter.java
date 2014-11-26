package com.vl.printerapplication;
/**
 * This PdfPrintDocumentAdapter class prepares the PDF document
 * and giving the pdf to Android print frame work
 */
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

public class PdfPrintDocumentAdapter extends PrintDocumentAdapter{
    private MainActivity mActivity;
    private PrintedPdfDocument mPdfDocument;
    public PdfPrintDocumentAdapter(MainActivity activity) {
        this.mActivity = activity;
    }
    @Override
    public void onStart() {
        super.onStart();
        Intent intent = mActivity.getIntent();
        Bundle bundle = intent.getExtras();
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
            PrintAttributes newAttributes,
            CancellationSignal cancellationSignal,
            PrintDocumentAdapter.LayoutResultCallback callback, Bundle extras) {
     // Create a new PdfDocument with the requested page attributes
        mPdfDocument = new PrintedPdfDocument(mActivity, newAttributes);
        
     // Respond to cancellation request
        if(cancellationSignal.isCanceled()){
            callback.onLayoutCancelled();
            return;
        }
        
        // Compute the expected number of printed pages
        int pages = 1;

        if (pages > 0) {
         // Return print information to print framework
            PrintDocumentInfo info = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(pages)
                    .build();
            // Content layout reflow is complete
            callback.onLayoutFinished(info, true);
        } else {
            // Otherwise report an error to the print framework
            callback.onLayoutFailed("Page count calculation failed.");
        }
        


    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination,
            CancellationSignal cancellationSignal, WriteResultCallback callback) {
        for (int i = 0; i < 4; i++) {
            if (pageInRange(pageRanges, i))
            {
//                 PageInfo newPage = new PageInfo.Builder(pageWidth, 
//                             pageHeight, i).create();
                    
                 PdfDocument.Page page = 
                         mPdfDocument.startPage(i);

                 if (cancellationSignal.isCanceled()) {
                  callback.onWriteCancelled();
                  mPdfDocument.close();
                  mPdfDocument = null;
                  return;
                 }
                 drawPage(page, i);
                 mPdfDocument.finishPage(page);  
            }
        }
            
        try {
            mPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            mPdfDocument.close();
            mPdfDocument = null;
        }

        callback.onWriteFinished(pageRanges);

    }
    private boolean pageInRange(PageRange[] pageRanges, int page)
    {
        for (int i = 0; i<pageRanges.length; i++)
        {
            if ((page >= pageRanges[i].getStart()) && 
                                         (page <= pageRanges[i].getEnd()))
                return true;
        }
        return false;
    }
    private void drawPage(PdfDocument.Page page, 
            int pagenumber) {
        Canvas canvas = page.getCanvas();

        pagenumber++; // Make sure page numbers start at 1

        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        canvas.drawText(
                "Test Print Document Page " + pagenumber,
                leftMargin,
                titleBaseLine, 
                paint);

        paint.setTextSize(14);
        canvas.drawText("This is some test content to verify that custom document printing works", leftMargin, titleBaseLine + 35, paint);

        if (pagenumber % 2 == 0)
            paint.setColor(Color.RED);
        else
            paint.setColor(Color.GREEN);

        PageInfo pageInfo = page.getInfo();


        canvas.drawCircle(pageInfo.getPageWidth()/2,
                pageInfo.getPageHeight()/2, 
                150, 
                paint); 
    }
    
    

}
