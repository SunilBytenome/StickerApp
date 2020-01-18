package com.github.gabrielbb.cutout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.Stack;

import static com.github.gabrielbb.cutout.DrawView.DrawViewAction.ADD_STICKER;
import static com.github.gabrielbb.cutout.DrawView.DrawViewAction.AUTO_CLEAR;
import static com.github.gabrielbb.cutout.DrawView.DrawViewAction.MANUAL_CLEAR;
import static com.github.gabrielbb.cutout.DrawView.DrawViewAction.TEXT_DRAW;

class DrawView extends View {

    private Path livePath;
    private Paint pathPaint;
    private static Bitmap imageBitmap;
    String textStyle;
    private final Stack<Pair<Pair<Path, Paint>, Bitmap>> cuts = new Stack<>();
    private final Stack<Pair<Pair<Path, Paint>, Bitmap>> undoneCuts = new Stack<>();
    private float pathX, pathY;
    private static final float TOUCH_TOLERANCE = 4;
    private static final float COLOR_TOLERANCE = 50;
    private Button undoButton;
    private Button redoButton;
    private FrameLayout frameLayout;
    private View loadingModal, etEditText;
    private Paint pText;
    private Canvas canvas;
    private int x;
    private int y, color = -1, textSize = 35;
    private DrawViewAction currentAction;
    private WeakReference<DrawView> drawViewWeakReference;
    private Random mRandom = new Random();
    private String text;
    Context context;
    private boolean isTextDrag;
    private PointF start = new PointF();
    int  stickerX,stickerY,textX,textY;


    private Canvas c;

    private Paint pLine, pBg;
    private Path touchPath;

    private Bitmap b;
    private Uri selectImage;
    private boolean isStickerAdded;

    public enum DrawViewAction {
        AUTO_CLEAR,
        MANUAL_CLEAR,
        ZOOM, TEXT_DRAW,
        ADD_STICKER,
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        livePath = new Path();
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setDither(true);
        pathPaint.setColor(Color.BLUE);
        pathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);

        pBg = new Paint();
        pBg.setColor(Color.WHITE);
        pLine = new Paint();
        pLine.setColor(Color.GREEN);
        pLine.setAntiAlias(true);
        pLine.setStyle(Paint.Style.STROKE);
        pLine.setStrokeWidth(12);
        touchPath = new Path();
    }

    public void setButtons(Button undoButton, Button redoButton, FrameLayout frameLayout) {
        this.undoButton = undoButton;
        this.redoButton = redoButton;
        this.frameLayout = frameLayout;
    }


    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        super.onSizeChanged(600, 512, oldWidth, oldHeight);
        resizeBitmap(getWidth(), getHeight());
        b = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        c = new Canvas(b);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("widthMeasureSpec", MeasureSpec.toString(widthMeasureSpec) + " " + MeasureSpec.toString(heightMeasureSpec));

        int desiredWidth = 512;
        int desiredHeight = 512;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, width);
        Log.e("width", width + "" + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        this.canvas = canvas;
        int centreX = (canvas.getWidth() - imageBitmap.getWidth()) / 2;
        int centreY = (canvas.getHeight() - imageBitmap.getHeight()) / 2;
        if (imageBitmap != null) {
            canvas.drawBitmap(this.imageBitmap, centreX, centreY, null);
            for (Pair<Pair<Path, Paint>, Bitmap> action : cuts) {
                if (action.first != null) {
                    canvas.drawPath(action.first.first, action.first.second);
                }
            }
            if (currentAction == MANUAL_CLEAR) {
                canvas.drawPath(livePath, pathPaint);
            } else if (currentAction == TEXT_DRAW) {
            }
        }


        canvas.drawBitmap(b, 0, 0, pBg);
        canvas.drawPath(touchPath, pLine);
        redrawImage(text, color, textSize);
        canvas.restore();

    }

    private void touchStart(float x, float y) {
        pathX = x;
        pathY = y;

        undoneCuts.clear();
        redoButton.setEnabled(false);

        if (currentAction == AUTO_CLEAR) {
            new AutomaticPixelClearingTask(this).execute((int) x, (int) y);
        } else {
            livePath.moveTo(x, y);
        }

        invalidate();
    }

    private void touchMove(float x, float y) {
        if (currentAction == MANUAL_CLEAR) {
            float dx = Math.abs(x - pathX);
            float dy = Math.abs(y - pathY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                livePath.quadTo(pathX, pathY, (x + pathX) / 2, (y + pathY) / 2);
                pathX = x;
                pathY = y;
            }
        }
    }


    private void touchUp() {
        if (currentAction == MANUAL_CLEAR) {
            livePath.lineTo(pathX, pathY);
            cuts.push(new Pair<>(new Pair<>(livePath, pathPaint), null));
            livePath = new Path();
            undoButton.setEnabled(true);
        }
    }

    public void undo() {
        if (cuts.size() > 0) {

            Pair<Pair<Path, Paint>, Bitmap> cut = cuts.pop();

            if (cut.second != null) {
                undoneCuts.push(new Pair<>(null, imageBitmap));
                this.imageBitmap = cut.second;
            } else {
                undoneCuts.push(cut);
            }

            if (cuts.isEmpty()) {
                undoButton.setEnabled(false);
            }

            redoButton.setEnabled(true);

            invalidate();
        }
        //toast the user
    }

    public void redo() {
        if (undoneCuts.size() > 0) {
            Pair<Pair<Path, Paint>, Bitmap> cut = undoneCuts.pop();
            if (cut.second != null) {
                cuts.push(new Pair<>(null, imageBitmap));
                this.imageBitmap = cut.second;
            } else {
                cuts.push(cut);
            }
            if (undoneCuts.isEmpty()) {
                redoButton.setEnabled(false);
            }
            undoButton.setEnabled(true);
            invalidate();
        }//toast the user
    }

   @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (imageBitmap != null) {

            if (currentAction == MANUAL_CLEAR) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        if (isTextDrag) {
                            x = (int) ev.getX();
                            y = (int) ev.getY();
                        } else {
                            touchStart(ev.getX(), ev.getY());
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:

                        if (isTextDrag) {
                            x = (int) ev.getX();
                            y = (int) ev.getY();

                        } else {
                            touchMove(ev.getX(), ev.getY());
                        }
                        invalidate();
                        return true;
                    case MotionEvent.ACTION_UP:

                        if (isTextDrag) {
                        } else {
                            touchUp();
                        }
                        invalidate();
                        return true;
                }
            } else if (currentAction == AUTO_CLEAR) {

                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchStart(ev.getX(), ev.getY());
                        invalidate();
                        return true;
                }
            } else if (currentAction == TEXT_DRAW) {
                float touchX = ev.getX();
                float touchY = ev.getY();
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchPath.moveTo(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touchPath.lineTo(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_UP:
                        touchPath.lineTo(touchX, touchY);
                        c.drawPath(touchPath, pLine);
                        touchPath.reset();
                        break;
                    default:
                        return false;
                }
                invalidate();
                return true;

            } else if (currentAction == ADD_STICKER) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        x = (int) ev.getX();
                        y = (int) ev.getY();
                        invalidate();
                        return true;
                    case MotionEvent.ACTION_MOVE:

                        x = (int) ev.getX();
                        y = (int) ev.getY();

                        invalidate();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                }
            }

        }

        return super.onTouchEvent(ev);
    }

    private void resizeBitmap(int width, int height) {
        if (width > 0 && height > 0 && imageBitmap != null) {
            imageBitmap = BitmapUtility.getResizedBitmap(this.imageBitmap, width, height);
            imageBitmap.setHasAlpha(true);
            invalidate();
        }
    }

    public void setBitmap(Bitmap bitmap, String text, boolean isTextDrag, String textStyle, int color, int textSize, Uri selectImage) {

        if (imageBitmap!=null){

        }else {
            this.imageBitmap = bitmap;
        }
        this.text = text;
        this.isTextDrag = isTextDrag;
        this.textStyle = textStyle;
        this.color = color;
        this.textSize = textSize;
        this.selectImage = selectImage;
        resizeBitmap(getWidth(), getHeight());
    }


    public Bitmap getCurrentBitmap() {
        return this.imageBitmap;
    }

    public void setAction(DrawViewAction newAction) {
        this.currentAction = newAction;
    }

    public void setStrokeWidth(int strokeWidth) {
        pathPaint = new Paint(pathPaint);
        pathPaint.setStrokeWidth(strokeWidth);
    }

    public void setLoadingModal(View loadingModal) {
        this.loadingModal = loadingModal;

    }

    private static class AutomaticPixelClearingTask extends AsyncTask<Integer, Void, Bitmap> {

        private WeakReference<DrawView> drawViewWeakReference;

        AutomaticPixelClearingTask(DrawView drawView) {
            this.drawViewWeakReference = new WeakReference<>(drawView);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            drawViewWeakReference.get().loadingModal.setVisibility(VISIBLE);

            drawViewWeakReference.get().cuts.push(new Pair<>(null, drawViewWeakReference.get().imageBitmap));
        }

        @Override
        protected Bitmap doInBackground(Integer... points) {
            Bitmap newBitmap = null;
            try {

                Bitmap oldBitmap = drawViewWeakReference.get().imageBitmap;

                int colorToReplace = oldBitmap.getPixel(points[0], points[1]);

                int width = oldBitmap.getWidth();
                int height = oldBitmap.getHeight();
                int[] pixels = new int[width * height];
                oldBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

                int rA = Color.alpha(colorToReplace);
                int rR = Color.red(colorToReplace);
                int rG = Color.green(colorToReplace);
                int rB = Color.blue(colorToReplace);

                int pixel;

                // iteration through pixels
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        // get current index in 2D-matrix
                        int index = y * width + x;
                        pixel = pixels[index];
                        int rrA = Color.alpha(pixel);
                        int rrR = Color.red(pixel);
                        int rrG = Color.green(pixel);
                        int rrB = Color.blue(pixel);

                        if (rA - COLOR_TOLERANCE < rrA && rrA < rA + COLOR_TOLERANCE && rR - COLOR_TOLERANCE < rrR && rrR < rR + COLOR_TOLERANCE &&
                                rG - COLOR_TOLERANCE < rrG && rrG < rG + COLOR_TOLERANCE && rB - COLOR_TOLERANCE < rrB && rrB < rB + COLOR_TOLERANCE) {
                            pixels[index] = Color.TRANSPARENT;
                        }
                    }
                }
                newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            } catch (Exception e) {

            }

            if (newBitmap == null) {
                newBitmap = imageBitmap;
            }
            return newBitmap;
        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            drawViewWeakReference.get().imageBitmap = result;
            drawViewWeakReference.get().undoButton.setEnabled(true);
            drawViewWeakReference.get().loadingModal.setVisibility(INVISIBLE);
            drawViewWeakReference.get().invalidate();
        }
    }

    public void redrawImage(String text, int color, int textSize) {
        if (x == 0) {
            x = imageBitmap.getWidth() / 2;
            y = imageBitmap.getHeight() / 2 + 150;
        }
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(color);
        paintText.setTextSize(textSize);
        Typeface chops = Typeface.createFromAsset(context.getAssets(),
                textStyle);
        paintText.setTypeface(chops);
        paintText.setStyle(Paint.Style.FILL);

        Rect rectText = new Rect();
        paintText.getTextBounds(text, 0, text.length(), rectText);
        textX= x - (rectText.width() / 2);
        textY= y - (text.length() / 2);
        canvas.drawText(text,textX, textY, paintText);
       // addStickers();
    }

    public void addStickers() {
        if (x == 0) {
            x = imageBitmap.getWidth() / 2;
            y = imageBitmap.getHeight() / 2 + 150;
        }
        if (currentAction == ADD_STICKER) {
            isStickerAdded = true;
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectImage);
                stickerX=  x - (bitmap.getWidth() / 2);
                stickerY= y - (bitmap.getHeight() / 2);
                canvas.drawBitmap(bitmap, stickerX, stickerY, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            if (isStickerAdded) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectImage);
                    canvas.drawBitmap(bitmap, stickerX, stickerY, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}