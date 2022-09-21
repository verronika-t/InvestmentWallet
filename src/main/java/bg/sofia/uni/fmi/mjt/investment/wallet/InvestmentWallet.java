package bg.sofia.uni.fmi.mjt.investment.wallet;

import bg.sofia.uni.fmi.mjt.investment.wallet.acquisition.Acquisition;
import bg.sofia.uni.fmi.mjt.investment.wallet.acquisition.AcquisitionService;
import bg.sofia.uni.fmi.mjt.investment.wallet.asset.Asset;
import bg.sofia.uni.fmi.mjt.investment.wallet.exceptions.InsufficientResourcesException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exceptions.OfferPriceException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exceptions.UnknownAssetException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exceptions.WalletException;
import bg.sofia.uni.fmi.mjt.investment.wallet.quote.Quote;
import bg.sofia.uni.fmi.mjt.investment.wallet.quote.QuoteService;

import java.time.LocalDateTime;
import java.util.*;

public class InvestmentWallet implements Wallet {

    private QuoteService quoteService;
    private double cashBalance;
    private Map<Asset, Integer> assetQuantityMap;
    private List<Acquisition> acquisitions;

    public InvestmentWallet(QuoteService quoteService) {
        this.quoteService = quoteService;
        this.cashBalance = 0;
        this.assetQuantityMap = new HashMap<>();
        this.acquisitions = new LinkedList<>();

    }

    @Override
    public double deposit(double cash) {
        if (cash < 0) {
            throw new IllegalArgumentException("Negative cash");
        }

        this.cashBalance += cash;
        return this.cashBalance;
    }

    @Override
    public double withdraw(double cash) throws InsufficientResourcesException {
        if (cash < 0) {
            throw new IllegalArgumentException("Negative cash");
        }

        if (this.cashBalance < cash) {
            throw new InsufficientResourcesException("The cash balance is insufficient to proceed with the withdrawal");
        }

        this.cashBalance -= cash;
        return this.cashBalance;
    }

    @Override
    public Acquisition buy(Asset asset, int quantity, double maxPrice) throws OfferPriceException, InsufficientResourcesException, UnknownAssetException {
        if (asset == null || quantity <= 0 || maxPrice <= 0) {
            throw new IllegalArgumentException();
        }

        Quote quote = getQuoteAsset(asset);

        if (quote.askPrice() > maxPrice) {
            throw new OfferPriceException("Higher ask price");
        }

        double totalPriceForTransaction = quantity * quote.askPrice();

        if (this.cashBalance < totalPriceForTransaction) {
            throw new InsufficientResourcesException("Not enough cash for transaction!");
        }

        Acquisition acquisition = new AcquisitionService(quote.askPrice(), LocalDateTime.now(), quantity, asset);
        this.acquisitions.add(acquisition);
        this.cashBalance -= totalPriceForTransaction;

        addAssetToMap(asset, quantity);

        return acquisition;
    }

    private void addAssetToMap(Asset asset, int quantity) {
        int currentQuantity = assetQuantityMap.get(asset) != null ? assetQuantityMap.get(asset) : 0;
        this.assetQuantityMap.put(asset, currentQuantity + quantity);
    }

    private Quote getQuoteAsset(Asset asset) throws UnknownAssetException {
        Quote quote = quoteService.getQuote(asset);

        if (quote == null) {
            throw new UnknownAssetException("Not found this asset!");
        }
        return quote;
    }

    @Override
    public double sell(Asset asset, int quantity, double minPrice) throws InsufficientResourcesException, OfferPriceException, UnknownAssetException {
        if (asset == null || quantity <= 0 || minPrice <= 0) {
            throw new IllegalArgumentException();
        }

        if (assetQuantityMap.get(asset) == null || assetQuantityMap.get(asset) < quantity) {
            throw new InsufficientResourcesException("Not enough quantity");
        }

        Quote quote = getQuoteAsset(asset);

        if (quote.bidPrice() < minPrice) {
            throw new OfferPriceException("Lower bid price than min price!");
        }

        removeAssetQuantityFromMap(asset, quantity);
       return this.cashBalance += quote.bidPrice() * quantity;
    }

    private void removeAssetQuantityFromMap(Asset asset, int quantity) {
        int current = assetQuantityMap.get(asset) != null ? assetQuantityMap.get(asset) : 0;
        if (current - quantity == 0) {
            this.assetQuantityMap.remove(asset);
        } else {
            assetQuantityMap.put(asset, current - quantity);
        }
    }

    @Override
    public double getValuation() throws UnknownAssetException {
        double totalValuation = 0.0;

        for (Map.Entry<Asset, Integer> entry : assetQuantityMap.entrySet()) {
            totalValuation += getValuation(entry.getKey());
        }
        return totalValuation;
    }

    @Override
    public double getValuation(Asset asset) throws UnknownAssetException {
        if (asset == null) {
            throw new IllegalArgumentException();
        }

        if (this.assetQuantityMap.get(asset) == null || this.quoteService.getQuote(asset) == null) {
            throw new UnknownAssetException("Unknown asset!");
        }

      return this.quoteService.getQuote(asset).bidPrice() * this.assetQuantityMap.get(asset) ;
    }

    @Override
    public Asset getMostValuableAsset() throws UnknownAssetException {
        Asset mostValuable = null;
        double maxValuation = 0.0;

        for (Map.Entry<Asset, Integer> entry : assetQuantityMap.entrySet()) {
            double current = this.getValuation(entry.getKey());
            if (current > maxValuation) {
                mostValuable = entry.getKey();
                maxValuation = current;
            }
        }
        return mostValuable;
    }

    @Override
    public Collection<Acquisition> getAllAcquisitions() {

        return List.copyOf(this.acquisitions);
    }

    @Override
    public Set<Acquisition> getLastNAcquisitions(int n) {
       if (n <= 0) {
           throw new IllegalArgumentException();
       }

       int size = this.acquisitions.size();

       if (n >= size) {
           return Set.copyOf(this.acquisitions);
       }
       return Set.copyOf(this.acquisitions.subList(size - n, size));
    }
}
