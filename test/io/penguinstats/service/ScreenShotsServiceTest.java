package io.penguinstats.service;


import io.penguinstats.bean.ItemDrop;
import io.penguinstats.bean.ScreenShot;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ScreenShotsServiceTest {

    private ScreenShotsService screenShotsService;

    ScreenShotsServiceTest() {
        this.screenShotsService = ScreenShotsService.getInstance();
    }

    @Test
    public void testMatchDrops() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new File("./resources/oneDropShrink_0.png"));
//        byte[] bytes = FileUtils.readFileToByteArray(new File("./resources/oneDrop_0.jpg"));
        ScreenShot screenShot = new ScreenShot();
        screenShot.setScreenShot(bytes);
        ItemDrop itemDrop = screenShotsService.matchDrops(screenShot);
        Assert.assertEquals(itemDrop.getDrops().size(), 1);
    }
}