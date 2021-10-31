package systemdesign.repository.entity;

public class ItemEntity {
    //@Version
    int seq;
    String uuid;
    private String itemCode;
    private String itemName;
    private double price;
    private int quantity;
    public ItemEntity (int seq, String itemCode, String itemName, int price, int quantity) {
        this.seq = seq;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getSeq() {
        return seq;
    }

    //hashcode
    //equals
    @Override
    public String toString() {
        return "Seq: " + seq + " ItemCode: " + itemCode;
    }
}
