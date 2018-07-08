import Common.Utils;
import org.junit.Assert;
import org.junit.Test;

import javax.rmi.CORBA.Util;
import java.io.*;

/**
 *
 */
public class UtilsTest {
    @Test
    public void sizeConvertTest() {
        String bStr1 = "10B";
        String bStr2 = "10  ";
        String kbStr1 = "10KB";
        String kbStr2 = "10KB";
        String mbStr1 = "10MB";
        String mbStr2 = "10M";
        String mbSpace = "10 MB";
        long b1 = Utils.formatFileSizeToByte(bStr1);
        Assert.assertEquals(b1, 10);
        long b2 = Utils.formatFileSizeToByte(bStr2);
        Assert.assertEquals(b2, 10);
        long kb1 = Utils.formatFileSizeToByte(kbStr1);
        Assert.assertEquals(kb1, 10240);
        long kb2 = Utils.formatFileSizeToByte(kbStr2);
        Assert.assertEquals(kb2, 10240);
        long mb1 = Utils.formatFileSizeToByte(mbStr1);
        Assert.assertEquals(mb1, 1024 * 1024 * 10);
        long mb2 = Utils.formatFileSizeToByte(mbStr2);
        Assert.assertEquals(mb2, 1024 * 1024 * 10);
        long mb3 = Utils.formatFileSizeToByte(mbSpace);
        Assert.assertEquals(mb3, 1024 * 1024 * 10);
    }
}
