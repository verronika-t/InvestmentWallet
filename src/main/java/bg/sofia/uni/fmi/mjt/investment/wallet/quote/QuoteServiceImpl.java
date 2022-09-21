package bg.sofia.uni.fmi.mjt.investment.wallet.quote;

import bg.sofia.uni.fmi.mjt.investment.wallet.asset.Asset;

import java.util.Map;

public class QuoteServiceImpl implements QuoteService {

    private Map<Asset, Quote> assetQuoteMap;

    public QuoteServiceImpl(Map<Asset, Quote> assetQuoteMap) {
        this.assetQuoteMap = assetQuoteMap;
    }

    @Override
    public Quote getQuote(Asset asset) {
        if (asset == null) {
            throw new  IllegalArgumentException();
        }
        if (!assetQuoteMap.containsKey(asset)) {
            return null;
        }

        return assetQuoteMap.get(asset);
    }
}
