/*
 * @Project Name MF_MP63_Web
 * @Author: Suneel Kumar
 * @Created Date: 23-05-2025 : 19:20
 * */
package model;

public class TR31PinBlock {
    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    String blockType;

    public String getMainKeyIndex() {
        return mainKeyIndex;
    }

    public void setMainKeyIndex(String mainKeyIndex) {
        this.mainKeyIndex = mainKeyIndex;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKSN() {
        return skn;
    }

    public void setKSN(String keyIndex) {
        this.skn = keyIndex;
    }

    String mainKeyIndex;
    String key;
    String skn;

}
