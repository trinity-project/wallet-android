package org.trinity.wallet.entity;

public class PaymentCodeBean {
    private String TNAP;
    private String randomHash;
    /**
     * There is no 0x head here but server needs 0x.
     */
    private String assetId;
    private double price;
    private String comment;

    public String getTNAP() {
        return TNAP;
    }

    public void setTNAP(String TNAP) {
        this.TNAP = TNAP;
    }

    public String getRandomHash() {
        return randomHash;
    }

    public void setRandomHash(String randomHash) {
        this.randomHash = randomHash;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
