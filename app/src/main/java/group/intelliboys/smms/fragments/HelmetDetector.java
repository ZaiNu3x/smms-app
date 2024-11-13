package group.intelliboys.smms.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class HelmetDetector {

    private Interpreter tflite;

    public HelmetDetector(Context context) throws IOException {

        tflite = new Interpreter(loadModelFile(context));
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(context.getAssets().openFd("helmet_model.tflite").getFileDescriptor())) {
            FileChannel fileChannel = fileInputStream.getChannel();
            long startOffset = context.getAssets().openFd("helmet_model.tflite").getStartOffset();
            long declaredLength = context.getAssets().openFd("helmet_model.tflite").getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    public boolean detectHelmet(Image image) {

        Bitmap bitmap = imageToBitmap(image);
        float[][] input = preprocessImage(bitmap);


        float[][] output = new float[1][1];


        tflite.run(input, output);


        return output[0][0] > 0.5;
    }

    private Bitmap imageToBitmap(Image image) {

        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();


        byte[] yBytes = new byte[yBuffer.remaining()];
        byte[] uBytes = new byte[uBuffer.remaining()];
        byte[] vBytes = new byte[vBuffer.remaining()];

        yBuffer.get(yBytes);
        uBuffer.get(uBytes);
        vBuffer.get(vBytes);

        // Create YUV image to RGB conversion (requires a library or custom code)
        // For simplicity, you can use the Android YuvImage class to convert to a Bitmap.
        YuvImage yuvImage = new YuvImage(yBytes, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, outStream);
        byte[] jpegData = outStream.toByteArray();

        // Create Bitmap from the jpeg data
        return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
    }

    private float[][] preprocessImage(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        float[][] input = new float[1][224 * 224 * 3];

        int[] pixels = new int[224 * 224];
        resizedBitmap.getPixels(pixels, 0, 224, 0, 0, 224, 224);


        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            input[0][i * 3] = ((pixel >> 16) & 0xFF) / 255.0f;
            input[0][i * 3 + 1] = ((pixel >> 8) & 0xFF) / 255.0f;
            input[0][i * 3 + 2] = (pixel & 0xFF) / 255.0f;
        }
        return input;
    }

    public void close() {

        if (tflite != null) {
            tflite.close();
        }
    }
}
