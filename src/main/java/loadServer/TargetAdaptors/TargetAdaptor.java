package loadServer.TargetAdaptors;


import Common.LoadOperation;
import sun.java2d.loops.GraphicsPrimitive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static loadClient.loadController.EventSeqMaker.*;

/**
 *
 */
public interface TargetAdaptor {
    void create(String id, String content) throws IOException;

    void delete(String id);

    void update(String id, String content) throws IOException;

    List<String> read(String id) throws IOException;

    default void executeOperation(LoadOperation op) throws IOException {
        Thread opThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
                } catch (IOException e) {

                }
            }
        });
        opThread.start();
    }

    void login();

}
