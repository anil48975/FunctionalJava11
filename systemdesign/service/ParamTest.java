package systemdesign.service;

import java.io.File;

public class ParamTest {
    public static void main(String[] args) {
        ReadParam.INPUT_STRING.setParam("abc");
        String str = ReadParam.INPUT_STRING.getParam();
        System.out.println(str);
    }
}

enum ReadParam {
    INPUT_STRING {
        private String paramValue;
        public String getParam() {
            return paramValue;
        }
        public <T> void setParam(T paramValue) {
            if (!String.class.getTypeName().equals(paramValue.getClass().getTypeName())) {
                throw new RuntimeException("Param value should be of type String");
            } else {
                this.paramValue = (String) paramValue;
            }
        }
    },
    INPUT_FILE {
        private File paramValue;
        public File getParam() {
            return paramValue;
        }
        public <T> void setParam(T paramValue) {
            if (!File.class.getTypeName().equals(paramValue.getClass().getTypeName())) {
                throw new RuntimeException("Param value should be of type File");
            } else {
                this.paramValue = (File) paramValue;
            }
        }
    };

    public abstract <T> void setParam(T paramValue);
    public abstract <T> T getParam();
}
