package loadServer.TargetAdaptors;


import Common.LoadOperation;
import sun.java2d.loops.GraphicsPrimitive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import static loadClient.loadController.EventSeqMaker.*;

/**
 *
 */
public interface TargetAdaptor {
    void create(String id, String content, String dataSourceName) throws Exception;

    void delete(String id, String dataSourceName) throws Exception;

    void update(String id, String content, String dataSourceName) throws Exception;

    List<String> read(String id, String dataSourceName) throws Exception;

    default Thread executeOperation(LoadOperation op, String dataSourceNaem) {
        Thread opThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Logger.getLogger(this.getClass().getName()).info(String.format("Executing %s", op.toString()));
                    switch (op.getOperationType()) {
                        case OP_CREATE:
                            create(op.getFileName(), op.getContent(), dataSourceNaem);
                            break;
                        case OP_DELETE:
                            delete(op.getFileName(), dataSourceNaem);
                            break;
                        case OP_READ:
                            read(op.getFileName(), dataSourceNaem);
                            break;
                        case OP_UPDAET:
                            update(op.getFileName(), op.getContent(), dataSourceNaem);
                            break;
                    }
                } catch (Exception e) {
                    Logger.getLogger(this.getClass().getName()).info(String.format("Failed to execute %s", op.toString()));
                    e.printStackTrace();
                }
            }
        });
        opThread.start();
        return opThread;
    }

    void login() throws Exception;

    void close() throws Exception;

    /**
     * Each data source represent one type of artifacts. In the reset step, adaptor should
     * construct the tables/directories for further processing.
     *
     * @param dataSourceNames
     * @throws Exception
     */
    void reset(List<String> dataSourceNames) throws Exception;

}
