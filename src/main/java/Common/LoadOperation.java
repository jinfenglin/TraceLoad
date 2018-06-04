package Common;

/**
 *
 */
public class LoadOperation {
    public enum Operation {
        CREATE, DELETE, READ, UPDATE
    }

    Operation operation;
    String updateContentSourceFile; //Only be used for UPDATE operation
    String targetFile;

    public LoadOperation(Operation operation, String targetFile) {
        this.operation = operation;
        this.targetFile = targetFile;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getUpdateContentSourceFile() {
        return updateContentSourceFile;
    }

    public void setUpdateContentSourceFile(String updateContentSourceFile) {
        this.updateContentSourceFile = updateContentSourceFile;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }
}
