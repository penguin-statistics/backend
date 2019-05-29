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
    public void testMoney() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new File("./resources/noDrop_0.jpg"));
        ScreenShot screenShot = new ScreenShot();
        screenShot.setScreenShot(bytes);
        ItemDrop itemDrop = screenShotsService.matchDrops(screenShot);
        Assert.assertEquals(itemDrop.getDrops().size(), 1);
        Assert.assertEquals(itemDrop.getDrops().get(0).getQuantity(), 216);
    }

    @Test
    public void testRockMoney() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new File("./resources/oneDrop_0.jpg"));
//        byte[] bytes = FileUtils.readFileToByteArray(new File("./resources/oneDrop_0.jpg"));
        ScreenShot screenShot = new ScreenShot();
        screenShot.setScreenShot(bytes);
        ItemDrop itemDrop = screenShotsService.matchDrops(screenShot);
        Assert.assertEquals(itemDrop.getDrops().size(), 2);
        Assert.assertEquals(itemDrop.getDrops().get(0).getQuantity(), 216);
        Assert.assertEquals(itemDrop.getDrops().get(1).getQuantity(), 1);
    }

    @Test
    public void testRockMoneyShrink() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new File("./resources/oneDropShrink_0.png"));
//        byte[] bytes = FileUtils.readFileToByteArray(new File("./resources/oneDrop_0.jpg"));
        ScreenShot screenShot = new ScreenShot();
        screenShot.setScreenShot(bytes);
        ItemDrop itemDrop = screenShotsService.matchDrops(screenShot);
        Assert.assertEquals(itemDrop.getDrops().size(), 2);
        Assert.assertEquals(itemDrop.getDrops().get(0).getQuantity(), 216);
        Assert.assertEquals(itemDrop.getDrops().get(1).getQuantity(), 1);
    }

    @Test
    public void testCE5() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new File("./resources/CE-5.jpg"));
//        byte[] bytes = FileUtils.readFileToByteArray(new File("./resources/oneDrop_0.jpg"));
        ScreenShot screenShot = new ScreenShot();
        screenShot.setScreenShot(bytes);
        ItemDrop itemDrop = screenShotsService.matchDrops(screenShot);
        Assert.assertEquals(itemDrop.getDrops().size(), 1);
        Assert.assertEquals(itemDrop.getDrops().get(0).getQuantity(), 7500);
    }

    @Test
    public void testChernobyl() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new File("./resources/chernobyl.jpg"));
        ScreenShot screenShot = new ScreenShot();
        screenShot.setScreenShot(bytes);
        ItemDrop itemDrop = screenShotsService.matchDrops(screenShot);
        Assert.assertEquals(itemDrop.getDrops().size(), 1);
        Assert.assertEquals(itemDrop.getDrops().get(0).getQuantity(), 200);
    }
}