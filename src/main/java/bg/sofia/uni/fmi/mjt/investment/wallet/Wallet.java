package bg.sofia.uni.fmi.mjt.investment.wallet;

import java.util.Collection;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.investment.wallet.acquisition.Acquisition;
import bg.sofia.uni.fmi.mjt.investment.wallet.asset.Asset;
import bg.sofia.uni.fmi.mjt.investment.wallet.exceptions.InsufficientResourcesException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exceptions.OfferPriceException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exceptions.UnknownAssetException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exceptions.WalletException;

public interface Wallet {

    /**
     *
     * @param cash the cash to deposit in the investment wallet
     * @return the cash balance in the wallet after the transaction
     * @throws IllegalArgumentException if the @cash is negative
     */
    double deposit(double cash);

    /**
     *
     * @param cash the cash to withdraw from the investment wallet
     * @return the cash balance in the wallet after the transaction
     * @throws IllegalArgumentException       if the @cash is negative
     * @throws InsufficientResourcesException if the cash balance is insufficient to
     *                                        proceed with the withdrawal
     */
    double withdraw(double cash) throws InsufficientResourcesException, InsufficientResourcesException;

    /**
     *
     * @param asset    the asset to buy
     * @param quantity the quantity to buy
     * @param maxPrice the maxPrice to pay for a unit of @asset
     * @return the acquisition of the @asset
     *
     * @throws IllegalArgumentException when:
     * @quantity is negative
     * @maxPrice is negative
     * @asset is null
     *
     * @throws UnknownAssetException          if there is no defined quote for
     *                                        the @asset
     * @throws OfferPriceException            if the ask price of the @asset is
     *                                        higher than the @maxPrice
     * @throws InsufficientResourcesException if there is not enough balance for the
     *                                        transaction
     */
    Acquisition buy(Asset asset, int quantity, double maxPrice) throws WalletException, OfferPriceException, InsufficientResourcesException, UnknownAssetException;

    /**
     *
     * @param asset    the asset to sell
     * @param quantity the quantity to sell
     * @param minPrice the minPrice to sell a unit of @asset for
     * @return the cash added to the balance after the sell
     * @throws IllegalArgumentException when:
     * @asset is null
     * @quantity is negative
     * @minPrice is negative
     * @throws InsufficientResourcesException if there is not enough quantity of
     *                                        the @asset
     * @throws UnknownAssetException          if there is no defined quote for
     *                                        the @asset
     * @OfferPriceException if the bid price of the @asset is lower than
     *                      the @minPrice
     */
    double sell(Asset asset, int quantity, double minPrice) throws WalletException, InsufficientResourcesException, OfferPriceException, UnknownAssetException;

    /**
     *
     * @return the valuations of all of the assets in the wallet combined
     */
    double getValuation() throws UnknownAssetException;

    /**
     * Valuation is the current market price of an asset.
     *
     * Valuation of a specific @asset in the investment wallet is the current bid
     * price of the @asset multiplied by the quantity of the @asset in the wallet
     *
     * @param asset
     * @return the valuation of the @asset
     * @throws IllegalArgumentException if @asset is null
     * @throws UnknownAssetException    if @asset is not currently in the wallet
     * @throws UnknownAssetException    if there is no defined quote for the @asset
     */
    double getValuation(Asset asset) throws UnknownAssetException, UnknownAssetException;

    /**
     * @return the asset with the highest valuation
     */
    Asset getMostValuableAsset() throws UnknownAssetException;

    /**
     * @return all acquisitions in the wallet as an unmodifiable copy
     */
    Collection<Acquisition> getAllAcquisitions();

    /**
     *
     * @param n the maximum number of acquisitions to return
     * @return an unmodifiable copy of the last @n acquisitions in the wallet. If
     *         there are less than @n acquisitions in the wallet, return all of
     *         them. The order of the acquisitions in the returned set is undefined
     * @throws IllegalArgumentException when @n is negative
     */
    Set<Acquisition> getLastNAcquisitions(int n);
}