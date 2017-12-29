package com.asociadosmonterrubio.admin.activities;


import com.onbarcode.barcode.android.*;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class AndroidBarcodeView extends View {

    private String number;
    private int init_x;
    private int init_y;

    public AndroidBarcodeView(Context context) {
        super(context);
    }

    public AndroidBarcodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AndroidBarcodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AndroidBarcodeView(Context context, String number, int init_x, int init_y) {
	    super(context);
        this.number = number;
        this.init_x = init_x;
        this.init_y = init_y;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        try {
            testCODE39(canvas, number, init_x, init_y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testCODE39(Canvas canvas, String number, int init_x, int init_y) throws Exception {
        Code39 barcode = new Code39();

        /*
           Code39 Valid data char set:
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9 (Digits)
                A - Z (Uppercase letters)
                - (Dash), $ (Dollar), % (Percentage), (Space), . (Point), / (Slash), + (Plus)

           Code39 extension Valid data char set:
                All ASCII 128 characters
        */
        // Code39 encodes upper case chars only, for lower case chars, use Code 39 extension
        barcode.setData(number);

        barcode.setExtension(false);

        barcode.setAddCheckSum(false);

        // Code 39 Wide Narrow bar Ratio
        // Valid value is from 2.0 to 3.0 inclusive.
        barcode.setN(3.0f);
        // The space between 2 characters in code 39; This a multiple of X; The default is 1.;
        // Valid value is from 1.0 (inclusive) to 5.3 (exclusive)
        barcode.setI(1.0f);
        barcode.setShowStartStopInText(true);

        // Unit of Measure, pixel, cm, or inch
        barcode.setUom(IBarcode.UOM_PIXEL);
        // barcode bar module width (X) in pixel
        barcode.setX(2f);
        // barcode bar module height (Y) in pixel
        barcode.setY(40f);

        // barcode image margins
        barcode.setLeftMargin(0f);
        barcode.setRightMargin(0f);
        barcode.setTopMargin(0f);
        barcode.setBottomMargin(0f);

        // barcode image resolution in dpi
        barcode.setResolution(72);

        // disply barcode encoding data below the barcode
        barcode.setShowText(true);
        // barcode encoding data font style
        barcode.setTextFont(new AndroidFont("Arial", Typeface.NORMAL, 12));
        // space between barcode and barcode encoding data
        barcode.setTextMargin(6);
        barcode.setTextColor(AndroidColor.black);
        
        // barcode bar color and background color in Android device
        barcode.setForeColor(AndroidColor.black);
        barcode.setBackColor(AndroidColor.white);

        /*
        specify your barcode drawing area
	    */
	    RectF bounds = new RectF(init_x, init_y, 0, 0);
        barcode.drawBarcode(canvas, bounds);
    }

}
