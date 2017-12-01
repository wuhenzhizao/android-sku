package com.wuhenzhizao.sku.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by wuhenzhizao on 2017/3/6.
 */
public class Sku implements Parcelable {
    private String id;
    private String mainImage;
    private int stockQuantity;
    private boolean inStock;
    private long originPrice;
    private long sellingPrice;
    private List<SkuAttribute> attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public long getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(long originPrice) {
        this.originPrice = originPrice;
    }

    public long getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(long sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public List<SkuAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SkuAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "Sku{" +
                "id='" + id + '\'' +
                ", mainImage='" + mainImage + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", inStock=" + inStock +
                ", originPrice=" + originPrice +
                ", sellingPrice=" + sellingPrice +
                ", attributes=" + attributes +
                '}';
    }

    public Sku() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.mainImage);
        dest.writeInt(this.stockQuantity);
        dest.writeByte(this.inStock ? (byte) 1 : (byte) 0);
        dest.writeLong(this.originPrice);
        dest.writeLong(this.sellingPrice);
        dest.writeTypedList(this.attributes);
    }

    protected Sku(Parcel in) {
        this.id = in.readString();
        this.mainImage = in.readString();
        this.stockQuantity = in.readInt();
        this.inStock = in.readByte() != 0;
        this.originPrice = in.readLong();
        this.sellingPrice = in.readLong();
        this.attributes = in.createTypedArrayList(SkuAttribute.CREATOR);
    }

    public static final Creator<Sku> CREATOR = new Creator<Sku>() {
        @Override
        public Sku createFromParcel(Parcel source) {
            return new Sku(source);
        }

        @Override
        public Sku[] newArray(int size) {
            return new Sku[size];
        }
    };
}
