package io.penguinstats.service;

import io.penguinstats.bean.Drop;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.bean.ScreenShot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class ScreenShotsService {

    private static final double threshold = 0.8;
    private static ScreenShotsService instance = new ScreenShotsService();
    private ITesseract tesseract;
    private Map<Integer, Mat> items;

    public ScreenShotsService() {
        // load openCV
        OpenCV.loadLocally();

        // load tesseract
        CLibrary.INSTANCE.setlocale(CLibrary.LC_ALL, "C");
        CLibrary.INSTANCE.setlocale(CLibrary.LC_NUMERIC, "C");
        CLibrary.INSTANCE.setlocale(CLibrary.LC_CTYPE, "C");
        tesseract = new Tesseract();
        tesseract.setDatapath("resources");
        tesseract.setLanguage("digits");

        // load items, TODO: add more
        items = new HashMap<>();
        for (int i = -1; i < 1; i++) {
            File resources = new File("./resources/items/" + i + ".png");
            items.put(i, Imgcodecs.imread(resources.getAbsolutePath()));
        }
    }

    public static ScreenShotsService getInstance() {
        return instance;
    }

    private static Mat matchTemplate(Mat source, Mat template) {
        Mat result = new Mat();
        Imgproc.cvtColor(source, source, Imgproc.COLOR_BGRA2BGR);
        // resize to 1080 for now, since template match cannot detect scale automatically
        Imgproc.resize(source, source, new Size(1080.0 / source.height() * source.width(), 1080));
        //Template matching method
        Imgproc.matchTemplate(source, template, result, Imgproc.TM_CCOEFF_NORMED);

        MinMaxLocResult mmr = Core.minMaxLoc(result);

        if (mmr.maxVal < threshold) {
            return null;
        }

        // extract matched Mat and convert to binary gray scale
        Mat matched = source.submat((int) mmr.maxLoc.y, (int) mmr.maxLoc.y + template.rows(), (int) mmr.maxLoc.x,
            (int) mmr.maxLoc.x + template.cols());
        matched = matched.submat(130, 170, 85, 155);
        Imgproc.cvtColor(matched, matched, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(matched, matched, 150, 255, Imgproc.THRESH_BINARY_INV);

        Imgcodecs.imwrite("resources/matchResult.jpg", matched);
        return matched;
    }

    public ItemDrop matchDrops(ScreenShot screenShot) {
        ItemDrop ret = new ItemDrop();
        List<Drop> drops = new LinkedList<>();
        Mat source = Imgcodecs.imdecode(new MatOfByte(screenShot.getScreenShot()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        for (Entry<Integer, Mat> entry : items.entrySet()) {
            Mat out = matchTemplate(source, entry.getValue());
            if (null != out) {
                try {
                    String quantity = OCR(out).trim();
                    drops.add(new Drop(entry.getKey(), Integer.valueOf(quantity)));
                } catch (TesseractException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ret.setDrops(drops);
        return ret;
    }

    private String OCR(Mat source) throws TesseractException, IOException {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", source, mob);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(mob.toArray()));
        return tesseract.doOCR(image);
    }

    public String OCR(File file) throws TesseractException {
        return tesseract.doOCR(file);
    }
}
