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
    void create(String id, String content) throws IOException, Exception;

    void delete(String id) throws Exception;

    void update(String id, String content) throws IOException, Exception;

    List<String> read(String id) throws IOException, Exception;

    default Thread executeOperation(LoadOperation op) {
        Thread opThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Logger.getLogger(this.getClass().getName()).info(String.format("Executing %s", op.toString()));
                    switch (op.getOperationType()) {
                        case OP_CREATE:
                            create(op.getFileName(), op.getContent());
                            break;
                        case OP_DELETE:
                            delete(op.getFileName());
                            break;
                        case OP_READ:
                            read(op.getFileName());
                            break;
                        case OP_UPDAET:
                            update(op.getFileName(), op.getContent());
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

    void reset() throws Exception;

}
