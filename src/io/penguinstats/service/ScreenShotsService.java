package io.penguinstats.service;

import io.penguinstats.bean.Drop;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.bean.ScreenShot;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class ScreenShotsService {

    private static final double threshold = 0.5;
    private static ScreenShotsService instance = new ScreenShotsService();
    private Map<Integer, Mat> items;

    public ScreenShotsService() {
        OpenCV.loadLocally();
        items = new HashMap<>();
        File resources = new File("./resources/items/0.png");
        items.put(0, Imgcodecs.imread(resources.getAbsolutePath()));
    }

    public static ScreenShotsService getInstance() {
        return instance;
    }

    private static double matchTemplate(Mat source, Mat template) {
        Mat result = new Mat();
        Imgproc.cvtColor(source, source, Imgproc.COLOR_BGRA2BGR);
        // resize to 1080 for now, since template match cannot detect scale automatically
        Imgproc.resize(source, source, new Size(1080.0 / source.height() * source.width(), 1080));
        //Template matching method
        Imgproc.matchTemplate(source, template, result, Imgproc.TM_CCOEFF_NORMED);

        MinMaxLocResult mmr = Core.minMaxLoc(result);
        drawMatch(source, template, mmr);
        return mmr.maxVal;
    }

    /**
     * Draw a rectangle on original img for matched template
     */
    private static Mat drawMatch(Mat source, Mat template, MinMaxLocResult mmr) {
        Mat output = source.clone();
        Point matchLoc = mmr.maxLoc;
        Imgproc.rectangle(output, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()),
            new Scalar(255, 255, 255));
        Imgcodecs.imwrite("resources/matchResult.jpg", output);
        return output;
    }

    public ItemDrop matchDrops(ScreenShot screenShot) {
        ItemDrop ret = new ItemDrop();
        List<Drop> drops = new LinkedList<>();
        Mat source = Imgcodecs.imdecode(new MatOfByte(screenShot.getScreenShot()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        for (Entry<Integer, Mat> entry : items.entrySet()) {
            if (matchTemplate(source, entry.getValue()) > threshold) {
                drops.add(new Drop(entry.getKey(), 1));
            }
        }
        ret.setDrops(drops);
        return ret;
    }
}
