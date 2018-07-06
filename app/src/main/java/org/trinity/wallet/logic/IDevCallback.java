package org.trinity.wallet.logic;

public interface IDevCallback {
    void invoke(final byte[] privateKey, final byte[] publicKey, final byte[] hashedSignature, final String wif, final String address, final String sign, final String sign16, final byte[] signed);
}
