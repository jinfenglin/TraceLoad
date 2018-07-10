package loadServer;

import Common.LoadOperation;
import loadClient.loadController.Target;
import loadServer.TargetAdaptors.TargetAdaptor;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static Common.Utils.getFileNameWOPostfix;

/**
 * Load/read data on target platform
 */
public class LoadThread implements Runnable {
    File loadFile;
    Target loadTarget;
    Logger logger;

    public LoadThread(File opFile, Target loadTarget) {
        this.loadFile = opFile;
        this.loadTarget = loadTarget;
        logger = Logger.getLogger(getClass().getName());
    }

    public long getPassedTime(long startTime) {
        long curTime = System.currentTimeMillis();
        return curTime - startTime;
    }

    @Override
    public void run() {
        try {
            String dataSourceName = getFileNameWOPostfix(loadFile.getName());
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(loadFile));
            TargetAdaptor adaptor = loadTarget.getTargetAdaptor();
            adaptor.login();
            Queue<LoadOperation> opQueue = new ArrayDeque<>();
            while (true) {
                Object object = objectInputStream.readObject();
                if (object instanceof String) {
                    break;
                }
                LoadOperation loadOp = (LoadOperation) object;
                opQueue.add(loadOp);
            }
            long startTime = System.currentTimeMillis();
            List<Thread> operations = new ArrayList<>();
            while (opQueue.size() > 0) {
                LoadOperation op = opQueue.peek();
                long passedTime = getPassedTime(startTime);
                if (passedTime > Long.valueOf(op.getTime())) {
                    logger.info(String.format("Issued operation %s on time %s", op.toString(), passedTime));
                    opQueue.poll();
                    operations.add(adaptor.executeOperation(op, dataSourceName));
                }
            }
            //wait till all loading operations are finished
            for (Thread t : operations) {
                t.join();
            }
            long endTime = System.currentTimeMillis();
            long passedTime = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);
            logger.info(String.format("Load Task start at %s on target %s finished with load file %s, used Time:%s seconds, end at time %s", startTime, loadTarget.toString(), loadFile.getPath(), passedTime, endTime));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
