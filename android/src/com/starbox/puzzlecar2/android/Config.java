package com.starbox.puzzlecar2.android;


        import org.onepf.oms.OpenIabHelper;
        import org.onepf.oms.SkuManager;
        import java.util.HashMap;
        import java.util.Map;
public final class Config {

    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    public static final String SKU_PREMIUM = "premium";

    /**
     * Google play public key.
     */
    public static final String GOOGLE_PLAY_KEY
            ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAijgUrq34Px3Yh6s1uO9gxW2976EFzeGwN+1629S39jj0nnZdGq6V67qodf+1c76xqOgYG5md/qoLp779WeV57IOJx9oXeo6qDR85qvs6x0wfn+F4zz2MTPAVpQh0x7+kRky27v616UYR6TPG6X347bzkkU2e/wrDdGU6DlHPHCiC01E8oSrcT9MJToZPR8EZErAuNzTHYiI0ZPrsqiJt7Zb69iTLHkEx0HVTOBMQZtOQ9gs6RVsEnL/ANHpZZpzqe6YIlqM/LmcKZeg26EHRg126L8PAZSwdQ++rRD0jKVBCODnOmiBr1DlpA60V9wvNsqFv1RImOpIH/xg8C6Rf2wIDAQAB";



    public static final String SKU_PREMIUM_YANDEX = "premium";



    public static final Map<String, String> STORE_KEYS_MAP;

    static {
        STORE_KEYS_MAP = new HashMap<String, String>();
        STORE_KEYS_MAP.put(OpenIabHelper.NAME_GOOGLE, Config.GOOGLE_PLAY_KEY);
        SkuManager.getInstance().mapSku(SKU_PREMIUM, OpenIabHelper.NAME_YANDEX, SKU_PREMIUM_YANDEX);

    }

    private Config() {
    }
}
