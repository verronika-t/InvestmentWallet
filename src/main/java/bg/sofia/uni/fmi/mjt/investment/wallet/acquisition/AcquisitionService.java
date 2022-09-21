package bg.sofia.uni.fmi.mjt.investment.wallet.acquisition;

import bg.sofia.uni.fmi.mjt.investment.wallet.asset.Asset;

import java.time.LocalDateTime;

public class AcquisitionService implements Acquisition {

    private Double price;
    private LocalDateTime timestamp;
    private int quantity;
    private Asset asset;

    public AcquisitionService(Double price, LocalDateTime timestamp, int quantity, Asset asset) {
        this.price = price;
        this.timestamp = timestamp;
        this.quantity = quantity;
        this.asset = asset;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public Asset getAsset() {
        return this.asset;
    }
}
